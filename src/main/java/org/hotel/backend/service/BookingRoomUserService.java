package org.hotel.backend.service;


import org.hotel.backend.domain.AppUser;
import org.hotel.backend.domain.BookingRoomUser;
import org.hotel.backend.domain.Room;
import org.hotel.backend.dto.BookingInfo;
import org.hotel.backend.repository.BookingRoomUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional


public class BookingRoomUserService {
    private ModelMapper modelMapper;
    private BookingRoomUserRepository bookingRoomUserRepository;

    @Autowired
    public BookingRoomUserService(ModelMapper modelMapper, BookingRoomUserRepository bookingRoomUserRepository) {
        this.modelMapper = modelMapper;
        this.bookingRoomUserRepository = bookingRoomUserRepository;
    }

    public void saveBooking(BookingRoomUser bookingRoomUserServiceToSave) {
        bookingRoomUserRepository.save(bookingRoomUserServiceToSave);
    }


    public BookingRoomUser findBookingById(Long id) {
        Optional<BookingRoomUser> bookingRoomUserOptional = bookingRoomUserRepository.findById(id);
        if (bookingRoomUserOptional.isEmpty()) {
            throw new BookingNotFoundException(id);
        }
        return bookingRoomUserOptional.get();
    }

    public List<BookingInfo> findAllBookings(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<BookingRoomUser> bookingRoomUserPage = bookingRoomUserRepository.findAll(pageable);

        List<BookingInfo> bookingInfos = bookingRoomUserPage.getContent()
                .stream()
                .map(bookingRoomUser -> {
                    BookingInfo bookingInfo = modelMapper.map(bookingRoomUser, BookingInfo.class);

                    if (bookingRoomUser.getAppUser() != null) {
                        bookingInfo.setAppUserId(bookingRoomUser.getAppUser().getUserId());
                    }

                    if (bookingRoomUser.getRoom() != null) {
                        bookingInfo.setRoomId(bookingRoomUser.getRoom().getRoomId());
                    }

                    return bookingInfo;
                }).collect(Collectors.toList());
        return bookingInfos;


    }

    public BookingRoomUser findBookingByRoomId(Long id) {
//        return bookingRoomUserRepository.findAll()
//                .stream()
//                .filter(bookingRoomUser -> {
//                    Room room = bookingRoomUser.getRoom();
//                    if (room != null && room.getRoomId() != null) {
//                        return room.getRoomId().equals(id);
//                    } else {
//                        throw new BookingByRoomNotFoundException(id);
//                    }
//                })
//                .findFirst()
//                .orElseThrow(() -> new BookingNotFoundException(id));

        Optional<BookingRoomUser> result = bookingRoomUserRepository.findAll()
                .stream()
                .filter(bookingRoomUser -> {
                    Room room = bookingRoomUser.getRoom();
                    return room != null && room.getRoomId() != null && room.getRoomId().equals(id);
                })
                .findFirst();

        if (result.isEmpty()) {
            throw new BookingByRoomNotFoundException(id);
        }

        return result.get();
    }

    public List<BookingRoomUser> findBookingByUserId(Long id) {

        List<BookingRoomUser> resultList = bookingRoomUserRepository.findAll()
                .stream()
                .filter(bookingRoomUser -> {
                    AppUser appUser = bookingRoomUser.getAppUser();
                    return appUser != null && appUser.getUserId() != null && appUser.getUserId().equals(id);
                })
                .collect(Collectors.toList());

        if (resultList.isEmpty()) {
            throw new BookingByUserNotFoundException(id);
        }

        return resultList;
    }

    public boolean isRoomAvailable(Long roomId, LocalDateTime inDate, LocalDateTime outDate) {

        List<BookingRoomUser> existingBookings = bookingRoomUserRepository.findByRoomRoomIdAndOutDateAfterAndInDateBefore(
                roomId, inDate, outDate);
        return existingBookings.isEmpty();
    }

    public Optional<BookingRoomUser> findByVerificationToken(String token) {
        return bookingRoomUserRepository.findByVerificationToken(token);
    }

    public void save(BookingRoomUser bookingRoomUser) {
        bookingRoomUserRepository.save(bookingRoomUser);
    }

//    public void deleteBooking(Long bookingId) {
//        Optional<BookingRoomUser> bookingRoomUser = bookingRoomUserRepository.findById(bookingId);
//    }

    public Page<Room> findBookedRoomsInInterval(Pageable pageable, LocalDateTime inDate, LocalDateTime outDate) {
        return bookingRoomUserRepository.findBookedRoomsInInterval(pageable, inDate, outDate);
    }

    public List<BookingRoomUser> findUnverifiedBookings() {
        return bookingRoomUserRepository.findByIsVerifiedFalseAndVerificationTokenExpirationBefore(LocalDateTime.now());
    }

    @Scheduled(fixedRate = 30000)
    public void deleteUnverifiedBookings() {
        List<BookingRoomUser> unverifiedBookings = findUnverifiedBookings();

        for (BookingRoomUser booking : unverifiedBookings) {
            bookingRoomUserRepository.delete(booking);
        }
    }

    public List<BookingRoomUser> findAllBookingsForEmail() {
        return bookingRoomUserRepository.findAll();
    }

}