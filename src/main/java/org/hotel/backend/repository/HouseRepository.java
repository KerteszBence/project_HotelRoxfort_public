package org.hotel.backend.repository;


import org.hotel.backend.domain.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    boolean existsByHouseNameAndHouseRouteAndHouseDescription(
            String houseName, String houseRoute, String houseDescription);

    boolean existsByHouseId(long houseId);
}