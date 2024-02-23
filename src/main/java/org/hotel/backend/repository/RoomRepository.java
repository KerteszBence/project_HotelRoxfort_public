package org.hotel.backend.repository;


import org.hotel.backend.domain.Room;
import org.hotel.backend.domain.RoomStatus;
import org.hotel.backend.domain.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(Long roomNumber);

    boolean existsByRoomNumberAndRoomDescriptionAndCapacityAndPricePerNight(Long roomNumber, String roomDescription, int capacity, double pricePerNight);

    Page<Room> findAllByStatus(RoomStatus status, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.house.houseId = :house_id AND r.capacity >= :capacity AND r.status = 'AVAILABLE'")
    Page<Room> availabilityRooms(Pageable pageable, @Param("house_id") long house_id, @Param("capacity") int capacity);

    @Query("SELECT r FROM Room r WHERE r.roomType IN :roomType")
    Page<Room> findWithDynamicFilter(Pageable pageable, @Param("roomType") List<RoomType> roomType);

    Page<Room> findAllByHouseHouseIdAndStatus(Long houseId, RoomStatus status, Pageable pageable);

    Page<Room> findAllByHouseHouseId(Long houseId, Pageable pageable);
}