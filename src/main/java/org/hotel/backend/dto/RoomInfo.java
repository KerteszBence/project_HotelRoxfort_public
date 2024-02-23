package org.hotel.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfo {

    private Long roomId;
    private Long roomNumber;
    private String roomDescription;
    private String roomType;
    private int capacity;
    private double pricePerNight;
    private String status;
    private List<FileRegistryInfo> fileRegistryList;
    private long houseID;
}