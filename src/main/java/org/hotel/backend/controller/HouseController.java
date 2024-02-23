package org.hotel.backend.controller;


import lombok.extern.slf4j.Slf4j;
import org.hotel.backend.dto.HouseCreateCommand;
import org.hotel.backend.dto.HouseInfo;
import org.hotel.backend.dto.HouseInfoWithoutRoomList;
import org.hotel.backend.dto.HouseUpdateCommand;
import org.hotel.backend.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/houses")
@Slf4j
public class HouseController {

    private HouseService houseService;

    @Autowired
    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @PostMapping("/saveHouse")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<HouseInfo> saveHouse(@Valid @RequestBody HouseCreateCommand command) {
        log.info("Http request, POST / /api/houses, body: " + command.toString());
        HouseInfo houseInfo = houseService.saveHouse(command);
        return new ResponseEntity<>(houseInfo, HttpStatus.CREATED);
    }

    @PutMapping("/updateHouse/{houseId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<HouseInfo> updateHouseById(@PathVariable("houseId") Long houseId, @Valid @RequestBody HouseUpdateCommand command) {
        log.info("Http request, PUT /api/houses/{houseId} body: " + command.toString() + " with variable: " + houseId);
        HouseInfo houseInfo = houseService.updateHouseById(houseId, command);
        return new ResponseEntity<>(houseInfo, HttpStatus.ACCEPTED);
    }

    @GetMapping("/findAllHouses")
    public ResponseEntity<List<HouseInfoWithoutRoomList>> findAllHouses(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Http request, GET / /api/houses/findAllHouses");
        List<HouseInfoWithoutRoomList> houseInfoWithoutRoomLists = houseService.listAllHouses(pageNo, pageSize);
        return new ResponseEntity<>(houseInfoWithoutRoomLists, HttpStatus.OK);
    }

    @GetMapping("/findAllHousesForAdmin")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<HouseInfoWithoutRoomList>> findAllHousesForAdmin(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Http request, GET / /api/houses/findAllHousesForAdmin");
        List<HouseInfoWithoutRoomList> houseInfoWithoutRoomLists = houseService.listAllHousesForAdmin(pageNo, pageSize);
        return new ResponseEntity<>(houseInfoWithoutRoomLists, HttpStatus.OK);
    }

    @GetMapping("/findHouseById/{houseId}")
    public ResponseEntity<HouseInfo> findHouseById(@PathVariable("houseId") Long houseId) {
        log.info("Http request, GET / /api/houses/findHouseById/{houseId} with variable: " + houseId);
        HouseInfo houseInfo = houseService.getHouseById(houseId);
        return new ResponseEntity<>(houseInfo, HttpStatus.OK);
    }

    @DeleteMapping("/closeHouseById/{houseId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> closeHouseById(@PathVariable("houseId") Long houseId) {
        log.info("Http request, DELETE / /api/houses/closeHouseById/{houseId} with variable: " + houseId);
        houseService.closeHouseById(houseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/openHouseById/{houseId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<HouseInfo> openHouseById(@PathVariable("houseId") Long houseId) {
        log.info("Http request, DELETE / /api/houses/openHouseById/{houseId} with variable: " + houseId);
        HouseInfo houseInfo = houseService.openHouseById(houseId);
        return new ResponseEntity<>(houseInfo, HttpStatus.OK);
    }
}