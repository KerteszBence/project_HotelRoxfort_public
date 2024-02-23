package org.hotel.backend.service;


import org.hotel.backend.domain.BookingExtra;
import org.hotel.backend.dto.ExtraInfo;
import org.hotel.backend.repository.BookingExtraRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingExtraService {
    private BookingExtraRepository bookingExtraRepository;
    private ModelMapper modelMapper;

    @Autowired
    public BookingExtraService(BookingExtraRepository bookingExtraRepository, ModelMapper modelMapper) {
        this.bookingExtraRepository = bookingExtraRepository;
        this.modelMapper = modelMapper;
    }

    public void saveBookingExtra(BookingExtra bookingExtra) {
        bookingExtraRepository.save(bookingExtra);
    }

    public List<ExtraInfo> listAllExtrasByBookingId(Long bookingId) {
        List<BookingExtra> bookingExtraList = bookingExtraRepository.findAllExtrasByBookingId(bookingId);

        return bookingExtraList.stream()
                .map(bookingExtra -> {
                    ExtraInfo extraInfo = new ExtraInfo();
                    extraInfo.setExtraDescription(bookingExtra.getExtra().getExtraDescription());
                    extraInfo.setPrice(bookingExtra.getExtra().getPrice());
                    return extraInfo;
                })
                .collect(Collectors.toList());
    }

    public List<BookingExtra> listAllBookingExtrasByBookingId(Long bookingId) {
        return bookingExtraRepository.findAllExtrasByBookingId(bookingId);
    }
}