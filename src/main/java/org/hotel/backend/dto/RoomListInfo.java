package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomListInfo {

    private Long roomNumber;
    private String roomDescription;
    private String roomType;
    private int capacity;
    private double pricePerNight;
    private String status;
    private List<FileRegistryInfo> fileRegistryList;

}