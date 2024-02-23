package org.hotel.backend.repository;


import org.hotel.backend.domain.BookingExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingExtraRepository extends JpaRepository<BookingExtra, Long> {

    @Query("select b from BookingExtra b where b.bookingRoomUser.bookingId=:bookingId")
    List<BookingExtra> findAllExtrasByBookingId(Long bookingId);
}