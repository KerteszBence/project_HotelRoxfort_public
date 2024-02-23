package org.hotel.backend.service;



import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.hotel.backend.domain.FileRegistry;
import org.hotel.backend.dto.FileResource;
import org.hotel.backend.dto.UploadResponse;
import org.hotel.backend.repository.UploadRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Service
@Transactional
public class CloudinaryFileUploadImp extends FileUploaderService {

    private Cloudinary cloudinary;

    @Autowired
    public CloudinaryFileUploadImp(HouseService houseService, RoomService roomService, UploadRepository uploadRepository, ModelMapper modelMapper, Cloudinary cloudinary) {
        super(houseService, roomService, uploadRepository, modelMapper);
        this.cloudinary = cloudinary;
    }

    @Override
    protected FileRegistry storeFile(CommonsMultipartFile commonsMultipartFile, String category) {
        Map params = ObjectUtils.asMap(
                "folder", category,
                "access_mode", "authenticated",
                "access_type", "token",
                "overwrite", false,
                "type", "upload",
                "resource_type", "auto",
                "use_filename", false);

        UploadResponse uploadResponse = new UploadResponse();

        File fileToUpload = new File(System.getProperty("java.io.tmpdir") + '/' + commonsMultipartFile.getOriginalFilename());
        try {
            commonsMultipartFile.transferTo(fileToUpload);
            uploadResponse = new ObjectMapper()
                    .convertValue(cloudinary.uploader().upload(fileToUpload, params), UploadResponse.class);
        } catch (IOException e) {
        }
        FileRegistry fileRegistry = new FileRegistry(uploadResponse, commonsMultipartFile);
        fileRegistry.setPublicId(uploadResponse.getPublic_id());
        return fileRegistry;
    }

    @Override
    public FileResource getFile(Long id) {
        FileRegistry upload = findFileById(id);
        String url = upload.getFilePath();
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream())) {
            byte[] bytes = IOUtils.toByteArray(in);
            return new FileResource(bytes, upload.getMediaType(), upload.getOriginalFileName());
        } catch (IOException e) {
            return null;
        }
    }


    @Override
    public void deleteFile(Long resourceId) throws IOException {

        FileRegistry fileToDelete = findFileById(resourceId);

        String publicId = fileToDelete.getPublicId();
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        System.out.println(result);

        deleteFilefromDB(resourceId);
    }
}