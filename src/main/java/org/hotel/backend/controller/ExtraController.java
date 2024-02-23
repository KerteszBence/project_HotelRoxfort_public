package org.hotel.backend.controller;


import lombok.extern.slf4j.Slf4j;
import org.hotel.backend.dto.ExtraCreateCommand;
import org.hotel.backend.dto.ExtraInfo;
import org.hotel.backend.dto.ExtraUpdateCommand;
import org.hotel.backend.service.AppUserService;
import org.hotel.backend.service.BookingExtraService;
import org.hotel.backend.service.ExtraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/extras")
@Slf4j
public class ExtraController {

    private final ExtraService extraService;
    private final BookingExtraService bookingExtraService;
    private final AppUserService appUserService;

    @Autowired
    public ExtraController(ExtraService extraService, BookingExtraService bookingExtraService, AppUserService appUserService) {
        this.extraService = extraService;
        this.bookingExtraService = bookingExtraService;
        this.appUserService = appUserService;
    }

    @PostMapping("/saveExtra")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ExtraInfo> saveExtra(@Valid @RequestBody ExtraCreateCommand command) {
        log.info("Http request, POST / /api/extras/saveExtra, body: " + command.toString());
        ExtraInfo extraInfo = extraService.saveExtra(command);
        return new ResponseEntity<>(extraInfo, HttpStatus.CREATED);
    }

    @PutMapping("/updateExtra/{extraId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ExtraInfo> updateExtraById(@PathVariable("extraId") Long extraId,
                                                     @Valid @RequestBody ExtraUpdateCommand command) {
        log.info("Http request, PUT /api/extras/{extraId} body: " + command.toString() + " with variable: " + extraId);
        ExtraInfo extraInfo = extraService.updateExtraById(extraId, command);
        return new ResponseEntity<>(extraInfo, HttpStatus.ACCEPTED);
    }

    @GetMapping("/findAllExtras")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<ExtraInfo>> findAllExtras() {
        log.info("Http request, GET / /api/extras/findAllExtras");
        List<ExtraInfo> extraInfoList = extraService.listAllExtras();
        return new ResponseEntity<>(extraInfoList, HttpStatus.OK);
    }

    @GetMapping("/findAllExtras/{bookingId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<ExtraInfo>> findAllExtrasByBookingId(@PathVariable("bookingId") Long bookingId) {
        log.info("Http request, GET / /api/extras/findAllExtras/{bookingId}");
        List<ExtraInfo> extraInfoList = bookingExtraService.listAllExtrasByBookingId(bookingId);
        return new ResponseEntity<>(extraInfoList, HttpStatus.OK);
    }

    @PutMapping("/createBill/{bookingId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void>createBillByBookingId(@PathVariable("bookingId") Long bookingId) throws IOException {
        log.info("Http request, PUT /api/users/createBill/{bookingId} with variable: " + bookingId);
        appUserService.createBill(bookingId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}