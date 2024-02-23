package org.hotel.backend.service;


import org.hotel.backend.domain.FileRegistry;
import org.hotel.backend.dto.FileRegistryInfo;
import org.hotel.backend.dto.FileResource;
import org.hotel.backend.repository.UploadRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public abstract class FileUploaderService {
    private HouseService houseService;
    private RoomService roomService;
    private UploadRepository uploadRepository;
    private ModelMapper modelMapper;

    @Autowired
    public FileUploaderService(HouseService houseService, RoomService roomService, UploadRepository uploadRepository, ModelMapper modelMapper) {
        this.houseService = houseService;
        this.roomService = roomService;
        this.uploadRepository = uploadRepository;
        this.modelMapper = modelMapper;
    }


    public Long processFile(CommonsMultipartFile commonsMultipartFile, String title, String category, String house_id, String room_id) throws IOException {
        FileRegistry fileRegistry = storeFile(commonsMultipartFile, category);
        fileRegistry.setTitle(title);
        fileRegistry.setCategory(category);
        if (house_id != null && !house_id.isEmpty()) {
            fileRegistry.setHouse(houseService.findHouseById(Long.valueOf(house_id)));
        }
        if (room_id != null && !room_id.isEmpty()) {
            fileRegistry.setRoom(roomService.findRoomByRoomId(Long.valueOf(room_id)));
        }
        Long id = uploadRepository.save(fileRegistry).getId();

        return id;
    }

    protected abstract FileRegistry storeFile(CommonsMultipartFile commonsMultipartFile, String category) throws IOException;

    public abstract FileResource getFile(Long id);

    public FileRegistry findFileById(Long fileId) {
        Optional<FileRegistry> fileOptional = uploadRepository.findById(fileId);
        if (fileOptional.isEmpty()) {
//            throw new FileRegistryNotFoundException(houseId);
        }
        return fileOptional.get();

    }

    public List<FileRegistryInfo> getFileRegistryList() {
        return uploadRepository.findAll().stream()
                .map(fileRegistry -> modelMapper.map(fileRegistry, FileRegistryInfo.class))
                .collect(Collectors.toList());
    }

    public List<FileRegistryInfo> getFileRegistryListForRoom(Long roomId) {
        return uploadRepository.findAll().stream()
                .filter(fileRegistry -> fileRegistry.getRoom() != null && fileRegistry.getRoom().getRoomId().equals(roomId))
                .map(fileRegistry -> modelMapper.map(fileRegistry, FileRegistryInfo.class))
                .collect(Collectors.toList());
    }

    public abstract void deleteFile(Long resourceId) throws IOException;

    public void deleteFilefromDB(Long fileId) {
        FileRegistry fileRegistry = findFileById(fileId);
        fileRegistry.setFilePath(null);
        fileRegistry.setHouse(null);
        fileRegistry.setRoom(null);
    }
}