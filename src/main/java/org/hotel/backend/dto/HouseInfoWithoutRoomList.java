package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseInfoWithoutRoomList {

    private Long houseId;
    private String houseName;
    private String houseRoute;
    private String houseDescription;
}