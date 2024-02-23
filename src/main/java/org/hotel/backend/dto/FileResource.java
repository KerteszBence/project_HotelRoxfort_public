package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResource {
    private byte[] data;
    private String mediaType;
    private String originalFileName;
}