package org.hotel.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.hotel.backend.dto.*;
import org.hotel.backend.service.AppUserService;
import org.hotel.backend.service.BookingRoomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class AppUserController {

    private final AppUserService appUserService;
    private final BookingRoomUserService bookingRoomUserService;

    @Autowired
    public AppUserController(AppUserService appUserService, BookingRoomUserService bookingRoomUserService) {
        this.appUserService = appUserService;
        this.bookingRoomUserService = bookingRoomUserService;
    }

    @GetMapping("/me")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<UserDetails> getLoggedInUser() {
        log.info("Http request, GET / /api/users/me");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails loggedInUser = (User) authentication.getPrincipal();
        return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Http request, GET / /api/users/logout");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    @PostMapping("/saveAppUser")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<AppUserInfo> saveAppUser(@Valid @RequestBody AppUserCreateCommand command) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request, POST / /api/users/saveAppUser");
        AppUserInfo appUserInfo = appUserService.saveAppUser(command);
        return new ResponseEntity<>(appUserInfo, HttpStatus.CREATED);
    }

    @GetMapping("/findUserById/{userId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<AppUserInfo> findUserById(@PathVariable("userId") Long id) {
        log.info("Http request, GET / /api/users/{userId} with variable: " + id);
        AppUserInfo appUserInfo = appUserService.getUserById(id);
        return new ResponseEntity<>(appUserInfo, HttpStatus.FOUND);
    }

    @PutMapping("/updateAppUser/{appuserId}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<AppUserInfo> updateAppUserByIdAsUser(@PathVariable("appuserId") Long id, @Valid @RequestBody AppUserUpdateCommand command) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request, PUT /api/users/{appuserId} body: " + command.toString() + " with variable: " + id);
        AppUserInfo appUserInfo = appUserService.updateAppUserByIdAsUser(id, command);
        return new ResponseEntity<>(appUserInfo, HttpStatus.ACCEPTED);
    }

    @PutMapping("/updateAppUser/admin/{appuserId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<AppUserInfo> updateAppUserByIdAsAdmin(@PathVariable("appuserId") Long id, @Valid @RequestBody AppUserAdminUpdateCommand command) {
        log.info("Http request, PUT /api/users/{appuserId} body: " + command.toString() + " with variable: " + id);
        AppUserInfo appUserInfo = appUserService.updateAppUserByIdAsAdmin(id, command);
        return new ResponseEntity<>(appUserInfo, HttpStatus.ACCEPTED);
    }

    @GetMapping("/findAllAppUsers")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<AppUserListInfo>> findAllAppUsers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Http request, GET / /api/users/findAllUsers");
        List<AppUserListInfo> appUserListInfos = appUserService.findAllAppUsers(pageNo, pageSize);
        return new ResponseEntity<>(appUserListInfos, HttpStatus.FOUND);
    }

    @DeleteMapping("/deleteAppUserById/{appuserId}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Void> deleteAppUser(@PathVariable("appuserId") Long id) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request, DELETE / /api/users/deleteAppUserById/{appuserId} with variable: " + id);
        appUserService.deleteAppUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/saveBooking/{appuserId}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Void> saveBooking(@PathVariable("appuserId") Long id, @RequestBody BookingCreateCommand command) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request, POST / /api/users/saveBooking/{appuserId} with variable :" + id + " with body: " + command.toString());
        appUserService.saveAppUserToRoom(id, command);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/updateBooking/{actualroomId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<BookingUpdateInfo> updateBooking(@PathVariable("actualroomId") Long id, @RequestBody BookingUpdateCommand command) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request, POST / /api/users/saveBooking/{actualroomId} with variable :" + id + " with body: " + command.toString());
        BookingUpdateInfo bookingUpdateInfo = appUserService.updateBooking(id, command);
        return new ResponseEntity<>(bookingUpdateInfo, HttpStatus.ACCEPTED);
    }

    @GetMapping("/findBookingByAppUserId/{appuserId}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<List<BookingInfo>> findBookingByAppUserId(@PathVariable("appuserId") Long id) {
        log.info("Http request, GET / /api/users/findBookingByAppUserId/{appuserId} with variable: " + id);
        List<BookingInfo> bookingInfos = appUserService.findBookingByAppUserId(id);
        return new ResponseEntity<>(bookingInfos, HttpStatus.FOUND);
    }



    @GetMapping("/findAllBookings")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<BookingInfo>> findAllBookings(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Http request, GET / /api/users/findAllBookings");
        List<BookingInfo> bookingInfos = bookingRoomUserService.findAllBookings(pageNo, pageSize);
        return new ResponseEntity<>(bookingInfos, HttpStatus.FOUND);
    }

    @DeleteMapping("/deleteBooking/{roomId}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Void> deleteBooking(@PathVariable("roomId") Long id) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request, DELETE / /api/users/deleteBooking/{roomId} with variable: " + id);
        appUserService.deleteBooking(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/remainderMail/{bookingId}")
    public ResponseEntity<Void> sendRemanderMailById(@PathVariable("bookingId") Long id) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request, GET / /api/users/remainderMail/{bookId} with variable: " + id);
        appUserService.sendReminderMailById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            boolean isVerified = appUserService.verifyUserByToken(token);
            if (isVerified) {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        //     "            <img src=\"http://localhost:8080/images/5.png\" alt=\"Hotel Roxfort Logo\" class=\"logo\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\"> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Registration successfully verified!" + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Invalid or expired token." + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.BAD_REQUEST);
            }
        } catch (TokenNotFoundException e) {
            return new ResponseEntity<>("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Hotel Roxfort - Welcome!</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: 'Arial', sans-serif;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        .container {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 20px auto;\n" +
                    "            background-color: #fff;\n" +
                    "            border-radius: 8px;\n" +
                    "            overflow: hidden;\n" +
                    "        }\n" +
                    "\n" +
                    "        .header {\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff;\n" +
                    "            text-align: center;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .logo {\n" +
                    "            max-width: 100px;\n" +
                    "            height: auto;\n" +
                    "        }\n" +
                    "\n" +
                    "        h1 {\n" +
                    "            margin: 0;\n" +
                    "            font-size: 24px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .welcome-text {\n" +
                    "            font-size: 16px;\n" +
                    "            line-height: 1.5;\n" +
                    "            margin-bottom: 15px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link {\n" +
                    "            display: inline-block;\n" +
                    "            padding: 10px 20px;\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff !important;\n" +
                    "            text-decoration: none;\n" +
                    "            border-radius: 5px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link:hover {\n" +
                    "            background-color: #2fa64d;\n" +
                    "        }\n" +
                    "\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            padding: 10px;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Hotel Roxfort</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Verification link has expired. Please request a new one." + " </p>" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verifyBooking")
    public ResponseEntity<String> verifyBooking(@RequestParam("token") String token) {
        try {
            boolean isVerified = appUserService.verifyBookingByToken(token);
            if (isVerified) {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Booking successfully verified!" + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Invalid or expired token." + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.BAD_REQUEST);
            }
        } catch (TokenNotFoundException e) {
            return new ResponseEntity<>("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Hotel Roxfort - Welcome!</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: 'Arial', sans-serif;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        .container {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 20px auto;\n" +
                    "            background-color: #fff;\n" +
                    "            border-radius: 8px;\n" +
                    "            overflow: hidden;\n" +
                    "        }\n" +
                    "\n" +
                    "        .header {\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff;\n" +
                    "            text-align: center;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .logo {\n" +
                    "            max-width: 100px;\n" +
                    "            height: auto;\n" +
                    "        }\n" +
                    "\n" +
                    "        h1 {\n" +
                    "            margin: 0;\n" +
                    "            font-size: 24px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .welcome-text {\n" +
                    "            font-size: 16px;\n" +
                    "            line-height: 1.5;\n" +
                    "            margin-bottom: 15px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link {\n" +
                    "            display: inline-block;\n" +
                    "            padding: 10px 20px;\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff !important;\n" +
                    "            text-decoration: none;\n" +
                    "            border-radius: 5px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link:hover {\n" +
                    "            background-color: #2fa64d;\n" +
                    "        }\n" +
                    "\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            padding: 10px;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Hotel Roxfort</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Verification link has expired. Please request a new one." + " </p>" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/deleteBooking")
    public ResponseEntity<String> deleteBooking(@RequestParam("token") String token) {
        try {
            boolean isVerified = appUserService.undeleteBookingByToken(token);
            if (isVerified) {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Booking undelete verified." + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\">&nbsp Invalid or expired token." + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.BAD_REQUEST);
            }
        } catch (TokenNotFoundException e) {
            return new ResponseEntity<>("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Hotel Roxfort - Welcome!</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: 'Arial', sans-serif;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        .container {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 20px auto;\n" +
                    "            background-color: #fff;\n" +
                    "            border-radius: 8px;\n" +
                    "            overflow: hidden;\n" +
                    "        }\n" +
                    "\n" +
                    "        .header {\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff;\n" +
                    "            text-align: center;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .logo {\n" +
                    "            max-width: 100px;\n" +
                    "            height: auto;\n" +
                    "        }\n" +
                    "\n" +
                    "        h1 {\n" +
                    "            margin: 0;\n" +
                    "            font-size: 24px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .welcome-text {\n" +
                    "            font-size: 16px;\n" +
                    "            line-height: 1.5;\n" +
                    "            margin-bottom: 15px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link {\n" +
                    "            display: inline-block;\n" +
                    "            padding: 10px 20px;\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff !important;\n" +
                    "            text-decoration: none;\n" +
                    "            border-radius: 5px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link:hover {\n" +
                    "            background-color: #2fa64d;\n" +
                    "        }\n" +
                    "\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            padding: 10px;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Hotel Roxfort</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Verification link has expired. Please request a new one." + " </p>" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verifyUpdate")
    public ResponseEntity<String> verifyUpdate(@RequestParam("token") String token) {
        try {
            boolean isVerified = appUserService.verifyUpdateByToken(token);
            if (isVerified) {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\">&nbsp &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp User update successfully verified!" + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Invalid or expired token." + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.BAD_REQUEST);
            }
        } catch (TokenNotFoundException e) {
            return new ResponseEntity<>("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Hotel Roxfort - Welcome!</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: 'Arial', sans-serif;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        .container {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 20px auto;\n" +
                    "            background-color: #fff;\n" +
                    "            border-radius: 8px;\n" +
                    "            overflow: hidden;\n" +
                    "        }\n" +
                    "\n" +
                    "        .header {\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff;\n" +
                    "            text-align: center;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .logo {\n" +
                    "            max-width: 100px;\n" +
                    "            height: auto;\n" +
                    "        }\n" +
                    "\n" +
                    "        h1 {\n" +
                    "            margin: 0;\n" +
                    "            font-size: 24px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .welcome-text {\n" +
                    "            font-size: 16px;\n" +
                    "            line-height: 1.5;\n" +
                    "            margin-bottom: 15px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link {\n" +
                    "            display: inline-block;\n" +
                    "            padding: 10px 20px;\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff !important;\n" +
                    "            text-decoration: none;\n" +
                    "            border-radius: 5px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link:hover {\n" +
                    "            background-color: #2fa64d;\n" +
                    "        }\n" +
                    "\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            padding: 10px;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Hotel Roxfort</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Verification link has expired. Please request a new one." + " </p>" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verifyDelete")
    public ResponseEntity<String> verifyDelete(@RequestParam("token") String token) {
        try {
            boolean isVerified = appUserService.verifyDeleteByToken(token);
            if (isVerified) {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp User reactivation successfully verified!" + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Hotel Roxfort - Welcome!</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: 'Arial', sans-serif;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #fff;\n" +
                        "            border-radius: 8px;\n" +
                        "            overflow: hidden;\n" +
                        "        }\n" +
                        "\n" +
                        "        .header {\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff;\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .logo {\n" +
                        "            max-width: 100px;\n" +
                        "            height: auto;\n" +
                        "        }\n" +
                        "\n" +
                        "        h1 {\n" +
                        "            margin: 0;\n" +
                        "            font-size: 24px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .welcome-text {\n" +
                        "            font-size: 16px;\n" +
                        "            line-height: 1.5;\n" +
                        "            margin-bottom: 15px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            background-color: #342929;\n" +
                        "            color: #fff !important;\n" +
                        "            text-decoration: none;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .verification-link:hover {\n" +
                        "            background-color: #2fa64d;\n" +
                        "        }\n" +
                        "\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            padding: 10px;\n" +
                        "            background-color: #f2f2f2;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Hotel Roxfort</h1>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Invalid or expired token." + " </p>" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", HttpStatus.BAD_REQUEST);
            }
        } catch (TokenNotFoundException e) {
            return new ResponseEntity<>("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Hotel Roxfort - Welcome!</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: 'Arial', sans-serif;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        .container {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 20px auto;\n" +
                    "            background-color: #fff;\n" +
                    "            border-radius: 8px;\n" +
                    "            overflow: hidden;\n" +
                    "        }\n" +
                    "\n" +
                    "        .header {\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff;\n" +
                    "            text-align: center;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .logo {\n" +
                    "            max-width: 100px;\n" +
                    "            height: auto;\n" +
                    "        }\n" +
                    "\n" +
                    "        h1 {\n" +
                    "            margin: 0;\n" +
                    "            font-size: 24px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .welcome-text {\n" +
                    "            font-size: 16px;\n" +
                    "            line-height: 1.5;\n" +
                    "            margin-bottom: 15px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link {\n" +
                    "            display: inline-block;\n" +
                    "            padding: 10px 20px;\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff !important;\n" +
                    "            text-decoration: none;\n" +
                    "            border-radius: 5px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link:hover {\n" +
                    "            background-color: #2fa64d;\n" +
                    "        }\n" +
                    "\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            padding: 10px;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Hotel Roxfort</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p class=\"welcome-text\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Verification link has expired. Please request a new one." + " </p>" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateBooking/extras/{bookingId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> updateBookingWithExtras(@PathVariable("bookingId") Long bookingId, @RequestBody BookingExtraUpdateCommand command) throws MessagingException, UnsupportedEncodingException {
        log.info("Http request, POST / /api/users/saveBooking/extras/{bookingId} with variable :" + bookingId + " with body: " + command.toString());
        appUserService.updateBookingWithExtras(bookingId, command);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/sendBillInEmail/{bookingId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> sendBillInEmailByBookingId(@PathVariable("bookingId") Long bookingId) throws IOException, MessagingException {
        log.info("Http request, PUT /api/users/sendBillInEmail/{bookingId} with variable: " + bookingId);
        BookingRoomUser bookingRoomUser = bookingRoomUserService.findBookingById(bookingId);
        List<BookingExtra> bookingExtraList = appUserService.listAllBookingExtrasByBookingId(bookingId);
        appUserService.sendBillInEmail(bookingRoomUser, bookingExtraList);
        appUserService.createBill(bookingId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}