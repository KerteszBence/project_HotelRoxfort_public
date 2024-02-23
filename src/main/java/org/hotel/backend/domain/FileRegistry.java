package org.hotel.backend.domain;



import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hotel.backend.dto.UploadResponse;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "media")
@Data
public class FileRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileRegistryId;

    @ManyToOne
    @JoinColumn(name = "house_id")
    private House house;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String publicId;

    @Column(name = "file_path")
    private String filePath;
    @Column(name = "original_file_name")
    private String originalFileName;
    @Column(name = "file_size")
    private Long fileSize;
    @Column(name = "media_type")
    private String mediaType;
    @Column(name = "title")
    private String title;
    @Column(name = "category")
    private String category;
    @Column(name = "upload_datetime")
    @JsonFormat(locale = "hu", shape = JsonFormat.Shape.STRING, pattern = "yyyy. MM. dd. HH:mm:ss (Z)")
    private ZonedDateTime uploadDateTime = ZonedDateTime.now();

    public FileRegistry() {
    }

    public FileRegistry(UploadResponse uploadResponse, CommonsMultipartFile commonsMultipartFile) {
        this.filePath = uploadResponse.getSecureUrl();
        this.originalFileName = commonsMultipartFile.getFileItem().getName();
        this.fileSize = uploadResponse.getBytes();
        this.mediaType = commonsMultipartFile.getContentType();
    }

    public FileRegistry(String fullFilePath, long size, String contentType, String originalFileName) {
        this.filePath = fullFilePath;
        this.fileSize = size;
        this.mediaType = contentType;
        this.originalFileName = originalFileName;
    }

    public Long getId() {
        return fileRegistryId;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ZonedDateTime getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(ZonedDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}