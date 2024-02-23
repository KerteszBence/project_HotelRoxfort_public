package org.hotel.backend.service;


import org.apache.commons.io.IOUtils;
import org.hotel.backend.domain.FileRegistry;
import org.hotel.backend.dto.FileResource;
import org.hotel.backend.repository.UploadRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class ServerStorageFileUploaderImp extends FileUploaderService {

    private HttpServletRequest request;

    @Autowired
    public ServerStorageFileUploaderImp(HouseService houseService, RoomService roomService, UploadRepository uploadRepository, ModelMapper modelMapper, HttpServletRequest request) {
        super(houseService, roomService, uploadRepository, modelMapper);
        this.request = request;
    }

    @Override
    protected FileRegistry storeFile(CommonsMultipartFile commonsMultipartFile, String category) throws IOException {
        String uploadsDir = "/uploads/";
        String realPathToUploads = request.getServletContext().getRealPath(uploadsDir);
        if (!new File(realPathToUploads).exists()) {
            new File(realPathToUploads).mkdir();
        }
        String originalFilename = commonsMultipartFile.getOriginalFilename();
        String fullFilePath = realPathToUploads + originalFilename;
        File destination = new File(fullFilePath);
        commonsMultipartFile.transferTo(destination);

        FileRegistry newUpload = new FileRegistry(fullFilePath,
                commonsMultipartFile.getSize(),
                commonsMultipartFile.getContentType(),
                originalFilename);
        return newUpload;
    }

    @Override
    public FileResource getFile(Long id) {
        FileRegistry uploadById = findFileById(id);
        FileSystemResource fileResource = new FileSystemResource(uploadById.getFilePath());
        File file = fileResource.getFile();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytes = IOUtils.toByteArray(in);
            return new FileResource(bytes, uploadById.getMediaType(), uploadById.getOriginalFileName());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void deleteFile(Long resourceId) throws IOException {
        FileRegistry fileToDelete = findFileById(resourceId);
        String filePath = fileToDelete.getFilePath();
        deleteFilefromDB(resourceId);
        Paths.get(filePath).toFile().delete();
    }
}