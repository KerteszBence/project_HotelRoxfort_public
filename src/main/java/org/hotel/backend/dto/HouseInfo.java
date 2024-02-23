package org.hotel.backend.dto;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseInfo {

    private Long houseId;
    private String houseName;
    private String houseRoute;
    private String houseDescription;
    private boolean houseAvailable;
    private List<FileRegistryInfo> fileRegistryList;
    private List<RoomInfo> roomList;
}