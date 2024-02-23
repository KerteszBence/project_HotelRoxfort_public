package org.hotel.backend.controller;


import lombok.extern.slf4j.Slf4j;
import org.hotel.backend.dto.*;
import org.hotel.backend.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@Slf4j
public class RoomController {
    private RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/saveRoom")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<RoomInfo> saveRoom(@Valid @RequestBody RoomCreateCommand command) {
        log.info("Http request, POST / /api/rooms, body: " + command.toString());
        RoomInfo roomInfo = roomService.saveRoom(command);
        return new ResponseEntity<>(roomInfo, HttpStatus.CREATED);
    }

    @PutMapping("/updateRoom/{roomId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<RoomInfo> updateRoomById(@PathVariable("roomId") Long roomId, @Valid @RequestBody RoomUpdateCommand command) {
        log.info("Http request, PUT /api/rooms/{roomId} body: " + command.toString() + " with variable: " + roomId);
        RoomInfo roomInfo = roomService.updateRoomByRoomId(roomId, command);
        return new ResponseEntity<>(roomInfo, HttpStatus.ACCEPTED);
    }

    @GetMapping("/findRoomById/{roomId}")
    public ResponseEntity<RoomInfo> findRoomById(@PathVariable("roomId") Long roomId) {
        log.info("Http request, GET / /api/rooms/{roomId} with variable: " + roomId);
        RoomInfo roomInfo = roomService.getRoomById(roomId);
        return new ResponseEntity<>(roomInfo, HttpStatus.OK);
    }

    @GetMapping("/findAllRooms")
    public ResponseEntity<List<RoomListInfo>> findAllRooms(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Http request, GET / /api/houses/findAllRooms");
        List<RoomListInfo> roomListInfos = roomService.listAllRooms(pageNo, pageSize);
        return new ResponseEntity<>(roomListInfos, HttpStatus.OK);
    }

    @GetMapping("/dynamicFilter")
    public ResponseEntity<List<RoomListInfo>> getAllFilteredRoom(
            @RequestParam Map<String, String> allParams,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize
    ) {
        log.info("Http request, GET / /api/rooms/dynamicFilter");
        List<RoomListInfo> productListDetailsList = roomService.listRoomsDynamicFilter(pageNo, pageSize, allParams);
        return new ResponseEntity<>(productListDetailsList, HttpStatus.OK);
    }

    @PostMapping("/checkAvailability")
    public ResponseEntity<List<RoomListInfo>> checkAvailability(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestBody ReservationRequest reservationRequest) {
        log.info("Http request, GET / /api/rooms/checkAvailability with variable: " + reservationRequest.toString());
        System.out.println("checkAvailability");
        List<RoomListInfo> roomListInfos = roomService.checkAvailability(pageNo, pageSize, reservationRequest);
        return new ResponseEntity<>(roomListInfos, HttpStatus.OK);
    }


    @PostMapping("/whompingWillow")
    public ResponseEntity<RoomWhompingWillowInfo> whompingWillow(
            @RequestBody ReservationRequest reservationRequest) {
        log.info("Http request, GET / /api/rooms/whompingWillow with variable: " + reservationRequest.toString());
        RoomWhompingWillowInfo roomWhompingWillowInfo = roomService.whompingWillow(reservationRequest);

        return new ResponseEntity<>(roomWhompingWillowInfo, HttpStatus.OK);
    }


    @GetMapping("/findAllRoomsForAdmin")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<RoomListInfo>> findAllRoomsForAdmin(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Http request, GET / /api/houses/findAllRoomsForAdmin");
        List<RoomListInfo> roomListInfos = roomService.listAllRoomsForAdmin(pageNo, pageSize);
        return new ResponseEntity<>(roomListInfos, HttpStatus.OK);
    }

    @GetMapping("/findAllRoomsByHouseId/{houseId}")
    public ResponseEntity<List<RoomListInfo>> findAllRoomsByHouseId(@PathVariable("houseId") Long houseId, @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Http request, GET / /api/rooms/findAllRoomsByHouseId/{houseId} with variable: " + houseId);
        List<RoomListInfo> roomListInfos = roomService.listAllRoomsByHouseId(houseId, pageNo, pageSize);
        return new ResponseEntity<>(roomListInfos, HttpStatus.OK);
    }

    @GetMapping("/findAllRoomsByHouseIdForAdmin/{houseId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<RoomListInfo>> findAllRoomsByHouseIdForAdmin(@PathVariable("houseId") Long houseId, @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Http request, GET / /api/rooms/findAllRoomsByHouseIdForAdmin/{houseId} with variable: " + houseId);
        List<RoomListInfo> roomListInfos = roomService.listAllRoomsByHouseIdForAdmin(houseId, pageNo, pageSize);
        return new ResponseEntity<>(roomListInfos, HttpStatus.OK);
    }

    @DeleteMapping("/deleteRoomByRoomId/{roomId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long id) {
        log.info("Http request, DELETE / /api/rooms/deleteRoomByRoomId/{roomId} with variable: " + id);
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}