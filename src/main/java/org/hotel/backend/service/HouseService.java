package org.hotel.backend.service;


import org.hotel.backend.domain.House;
import org.hotel.backend.dto.HouseCreateCommand;
import org.hotel.backend.dto.HouseInfo;
import org.hotel.backend.dto.HouseInfoWithoutRoomList;
import org.hotel.backend.dto.HouseUpdateCommand;
import org.hotel.backend.repository.HouseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@Transactional
public class HouseService {

    private HouseRepository houseRepository;
    private ModelMapper modelMapper;

    @Autowired
    public HouseService(HouseRepository houseRepository, ModelMapper modelMapper) {
        this.houseRepository = houseRepository;
        this.modelMapper = modelMapper;
    }

    public HouseInfo saveHouse(HouseCreateCommand command) {

        if (houseRepository.existsByHouseNameAndHouseRouteAndHouseDescription(
                command.getHouseName(), command.getHouseRoute(), command.getHouseDescription())) {
            throw new DuplicateHouseException();
        }

        House house = modelMapper.map(command, House.class);
        House savedHouse = houseRepository.save(house);
        return modelMapper.map(savedHouse, HouseInfo.class);
    }

    public House findHouseById(Long houseId) {
        Optional<House> houseOptional = houseRepository.findById(houseId);
        if (houseOptional.isEmpty()) {
            throw new HouseNotFoundException(houseId);
        }
        return houseOptional.get();
    }


    public HouseInfo updateHouseById(Long houseId, HouseUpdateCommand command) {

        if (houseRepository.existsByHouseNameAndHouseRouteAndHouseDescription(
                command.getHouseName(), command.getHouseRoute(), command.getHouseDescription())) {
            throw new DuplicateHouseException();
        }

        House house = findHouseById(houseId);
        modelMapper.map(command, house);
        return modelMapper.map(house, HouseInfo.class);
    }

    public List<HouseInfoWithoutRoomList> listAllHouses(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<House> housePage = houseRepository.findAll(pageable);

        List<HouseInfoWithoutRoomList> houseInfoWithoutRoomLists = housePage.getContent()
                .stream()
                .filter(House::isHouseAvailable)
                .map(house -> modelMapper.map(house, HouseInfoWithoutRoomList.class))
                .collect(Collectors.toList());
        return houseInfoWithoutRoomLists;
    }

    public List<HouseInfoWithoutRoomList> listAllHousesForAdmin(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<House> housePage = houseRepository.findAll(pageable);

        List<HouseInfoWithoutRoomList> houseInfoWithoutRoomLists = housePage.getContent()
                .stream()
                .map(house -> modelMapper.map(house, HouseInfoWithoutRoomList.class))
                .collect(Collectors.toList());

        return houseInfoWithoutRoomLists;
    }

    public HouseInfo getHouseById(Long houseId) {
        return modelMapper.map(findHouseById(houseId), HouseInfo.class);
    }

    public void closeHouseById(Long houseId) {
        House house = findHouseById(houseId);
        house.setHouseAvailable(false);
        house.getRoomList().forEach(room -> room.setStatus(NOT_AVAILABLE));
    }

    public HouseInfo openHouseById(Long houseId) {
        House house = findHouseById(houseId);
        house.setHouseAvailable(true);
        return modelMapper.map(house, HouseInfo.class);
    }

    public boolean existsByHouseId(long houseId) {
        boolean isExist = houseRepository.existsByHouseId(houseId);
        return isExist;
    }
}

