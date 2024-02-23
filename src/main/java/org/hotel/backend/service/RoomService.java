package org.hotel.backend.service;


import org.hotel.backend.domain.House;
import org.hotel.backend.domain.Room;
import org.hotel.backend.domain.RoomStatus;
import org.hotel.backend.domain.RoomType;
import org.hotel.backend.dto.*;
import org.hotel.backend.exceptionhandling.DuplicateRoomException;
import org.hotel.backend.exceptionhandling.HouseNotFoundException;
import org.hotel.backend.exceptionhandling.RoomNotFoundException;
import org.hotel.backend.repository.RoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {
    private RoomRepository roomRepository;
    private HouseService houseService;
    private ModelMapper modelMapper;
    private BookingRoomUserService bookingRoomUserService;

    @Autowired
    public RoomService(RoomRepository roomRepository, HouseService houseService, ModelMapper modelMapper, BookingRoomUserService bookingRoomUserService) {
        this.roomRepository = roomRepository;
        this.houseService = houseService;
        this.modelMapper = modelMapper;
        this.bookingRoomUserService = bookingRoomUserService;
    }

    public RoomInfo saveRoom(RoomCreateCommand command) {

        if (roomRepository.existsByRoomNumber(command.getRoomNumber()) && houseService.existsByHouseId(command.getHouseId())) {
            throw new DuplicateRoomException(command.getRoomNumber(), command.getHouseId());
        }

        Room roomToSave = modelMapper.map(command, Room.class);
        roomToSave.setCapacity(RoomType.determineCapacityByType(RoomType.valueOf(command.getRoomType()))); //kapacitás beállítása a típus alapján
        House house = houseService.findHouseById(command.getHouseId());
        roomToSave.setHouse(house);
        Room savedRoom = roomRepository.save(roomToSave);

        RoomInfo roomInfo = modelMapper.map(savedRoom, RoomInfo.class);
        long houseID = savedRoom.getHouse().getHouseId();
        roomInfo.setHouseID(houseID);
        return roomInfo;

    }

    public RoomInfo updateRoomByRoomId(Long roomId, RoomUpdateCommand command) {
        Room roomToUpdate = findRoomByRoomId(roomId);
        roomToUpdate.setCapacity(RoomType.determineCapacityByType(RoomType.valueOf(command.getRoomType()))); //kapacitás beállítása a típus alapján
        modelMapper.map(command, roomToUpdate);
        House house = houseService.findHouseById(command.getHouseId());
        roomToUpdate.setHouse(house);
        RoomInfo roomInfo = modelMapper.map(roomToUpdate, RoomInfo.class);
        long houseID = roomToUpdate.getHouse().getHouseId();
        roomInfo.setHouseID(houseID);
        return roomInfo;
    }

    public Room findRoomByRoomId(Long roomId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            throw new RoomNotFoundException(roomId);
        }
        return roomOptional.get();
    }

    public RoomInfo getRoomById(Long roomId) {
        Room room = findRoomByRoomId(roomId);
        RoomInfo roomInfo = modelMapper.map(room, RoomInfo.class);
        long houseID = room.getHouse().getHouseId();
        roomInfo.setHouseID(houseID);
        return roomInfo;
    }

    public List<RoomListInfo> listAllRooms(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Room> roomPage = roomRepository.findAllByStatus(RoomStatus.AVAILABLE
                , pageable);

        List<RoomListInfo> roomListInfos = roomPage.getContent()
                .stream()
                .map(room -> modelMapper.map(room, RoomListInfo.class))
                .collect(Collectors.toList());
        return roomListInfos;

    }

    public List<RoomListInfo> listAllRoomsForAdmin(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Room> roomPage = roomRepository.findAll(pageable);

        List<RoomListInfo> roomListInfos = roomPage.getContent()
                .stream()
                .map(room -> modelMapper.map(room, RoomListInfo.class))
                .collect(Collectors.toList());
        return roomListInfos;

    }

    public List<RoomListInfo> listAllRoomsByHouseId(Long houseId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Room> roomPage = roomRepository.findAllByHouseHouseIdAndStatus(houseId, RoomStatus.AVAILABLE, pageable);

        List<RoomListInfo> roomListInfoList = roomPage.getContent()
                .stream()
                .map(room -> modelMapper.map(room, RoomListInfo.class))
                .collect(Collectors.toList());

        if (roomListInfoList.isEmpty()) {
            throw new HouseNotFoundException(houseId);
        }
        return roomListInfoList;
    }

    public List<RoomListInfo> listAllRoomsByHouseIdForAdmin(Long houseId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Room> roomPage = roomRepository.findAllByHouseHouseId(houseId, pageable);

        List<RoomListInfo> roomListInfoList = roomPage.getContent()
                .stream()
                .map(room -> modelMapper.map(room, RoomListInfo.class))
                .collect(Collectors.toList());

        if (roomListInfoList.isEmpty()) {
            throw new HouseNotFoundException(houseId);
        }
        return roomListInfoList;
    }

    public void deleteRoom(Long id) {
        Room room = findRoomByRoomId(id);
        room.setStatus(RoomStatus.NOT_AVAILABLE);
        roomRepository.save(room);
    }

    private Map<String, List<String>> extractFilterParams(Map<String, String> allParams) {
        Map<String, List<String>> filterParams = new HashMap<>();
        for (String param : Arrays.asList("room_type")) {
            if (allParams.containsKey(param)) {
                filterParams.put(param, List.of(allParams.get(param).split(",")));
            }
        }
        return filterParams;
    }


    private List<Room> filterOutBookedRooms(List<Room> roomList, List<Room> bookedRooms) {
        return roomList.stream()
                .filter(room -> !bookedRooms.contains(room))
                .collect(Collectors.toList());
    }


    public List<RoomListInfo> checkAvailability(int pageNo, int pageSize, ReservationRequest reservationRequest) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        int capacity = reservationRequest.getNumberOfGuests() > 3 ? 1 : reservationRequest.getNumberOfGuests();
        Page<Room> availableRoomsPage = roomRepository.availabilityRooms(pageable, reservationRequest.getHouseId(), capacity);
        Page<Room> bookedRoomsPage = bookingRoomUserService.findBookedRoomsInInterval(pageable, reservationRequest.getInDate(), reservationRequest.getOutDate());

        List<Room> availableRooms = availableRoomsPage.getContent();
        List<Room> bookedRooms = bookedRoomsPage.getContent();

        List<Room> resultBookList = filterOutBookedRooms(availableRooms, bookedRooms);

        List<RoomListInfo> resultBookListInfoList = resultBookList.stream()
                .map(room -> modelMapper.map(room, RoomListInfo.class))
                .collect(Collectors.toList());
        return resultBookListInfoList;
    }


    public List<RoomListInfo> listRoomsDynamicFilter(int pageNo, int pageSize, Map<String, String> allParams) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Map<String, List<String>> filterParams = extractFilterParams(allParams);

        Page<Room> roomList = roomRepository.findWithDynamicFilter(
                pageable,
                mapRoomTypeValues(filterParams.get("room_type")));

        List<RoomListInfo> resultList = roomList.stream()
                .map(room -> modelMapper.map(room, RoomListInfo.class))
                .collect(Collectors.toList());

        return resultList;
    }

    public List<RoomType> mapRoomTypeValues(List<String> RoomTypeStringList) {
        List<RoomType> productTypeList = new ArrayList<>();
        for (String productTypeString : RoomTypeStringList) {
            RoomType productType = RoomType.valueOf(productTypeString);
            productTypeList.add(productType);
        }
        return productTypeList;
    }

    public RoomWhompingWillowInfo whompingWillow(ReservationRequest reservationRequest) {
        RoomWhompingWillowInfo roomWhompingWillowInfo = new RoomWhompingWillowInfo();
        Random random1 = new Random();
        int randomHouseId = random1.nextInt(4) + 1;
        reservationRequest.setHouseId((long) randomHouseId);

        List<RoomListInfo> resultList = checkAvailability(0, 100, reservationRequest);

        if (!resultList.isEmpty()) {
            Random random2 = new Random();
            int randomIndex = random2.nextInt(resultList.size());
            RoomListInfo randomRoomListInfo = resultList.get(randomIndex);

            roomWhompingWillowInfo = modelMapper.map(randomRoomListInfo, RoomWhompingWillowInfo.class);
            roomWhompingWillowInfo.setHouseId((long) randomIndex);
        }
        return roomWhompingWillowInfo;
    }
}