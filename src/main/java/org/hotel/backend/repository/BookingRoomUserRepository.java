package org.hotel.backend.repository;


import org.hotel.backend.domain.BookingRoomUser;
import org.hotel.backend.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRoomUserRepository extends JpaRepository<BookingRoomUser, Long> {
    List<BookingRoomUser> findByRoomRoomIdAndOutDateAfterAndInDateBefore(
            Long roomId, LocalDateTime startDate, LocalDateTime endDate);

    Optional<BookingRoomUser> findByVerificationToken(String token);

    @Query("SELECT br.room FROM BookingRoomUser br " +
            "WHERE (br.inDate BETWEEN :inDate AND :outDate) OR (br.outDate BETWEEN :inDate AND :outDate) " +
            "OR (:inDate BETWEEN br.inDate AND br.outDate) OR (:outDate BETWEEN br.inDate AND br.outDate)")
    Page<Room> findBookedRoomsInInterval(Pageable pageable, @Param("inDate") LocalDateTime inDate, @Param("outDate") LocalDateTime outDate);

    List<BookingRoomUser> findByIsVerifiedFalseAndVerificationTokenExpirationBefore(LocalDateTime now);
}