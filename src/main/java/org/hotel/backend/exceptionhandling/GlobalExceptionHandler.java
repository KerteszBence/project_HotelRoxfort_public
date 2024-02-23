package org.hotel.backend.exceptionhandling;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> handleValidationException(MethodArgumentNotValidException exception) {
        List<ValidationError> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        validationErrors.forEach(validationError -> {
            log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleUserNotFoundException(UserNotFoundException exception) {
        ValidationError validationError = new ValidationError("appUserId",
                "appUser not found with id: " + exception.getId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleRoomNotFoundException(RoomNotFoundException exception) {
        ValidationError validationError = new ValidationError("roomId",
                "Room not found with id: " + exception.getRoomId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleBookingNotFoundException(BookingNotFoundException exception) {
        ValidationError validationError = new ValidationError("bookingId",
                "Booking not found with id: " + exception.getBookingId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingByUserNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleBookingByUserNotFoundException(BookingByUserNotFoundException exception) {
        ValidationError validationError = new ValidationError("appUserId",
                "Booking not found with appUserId: " + exception.getUserId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingByRoomNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleBookingByRoomNotFoundException(BookingByRoomNotFoundException exception) {
        ValidationError validationError = new ValidationError("roomId",
                "Booking not found with roomId: " + exception.getRoomId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserEmailNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleUserEmailNotFoundException(UserEmailNotFoundException exception) {
        ValidationError validationError = new ValidationError("appUserMail",
                "appUserMail not found with mail: " + exception.getEmail());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HouseNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleHouseNotFoundException(HouseNotFoundException exception) {
        ValidationError validationError = new ValidationError("House",
                "House not found with id: " + exception.getId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleNotAuthorizedException.class)
    public ResponseEntity<List<ValidationError>> handleRoleNotAuthorizedException(RoleNotAuthorizedException exception) {
        ValidationError validationError = new ValidationError("Role",
                "User has no access to this Role");
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordNotValidException.class)
    public ResponseEntity<List<ValidationError>> handlePasswordNotValidException(PasswordNotValidException exception) {
        ValidationError validationError = new ValidationError("Password",
                "Invalid password. It must be at least 8 characters long, contain at least one of these [A-Z], and at least one of these [!@#$%^&*()-_+=].");
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<List<ValidationError>> handleRoomNotAvailableException(RoomNotAvailableException exception) {
        ValidationError validationError = new ValidationError("Date",
                "Room is not available with id: " + exception.getId() + " from: " + exception.getInDate() + " to: " + exception.getOutDate());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateHouseException.class)
    public ResponseEntity<List<ValidationError>> handleDuplicateHouseException() {
        ValidationError validationError = new ValidationError("House",
                "House already exists with the same data");
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateRoomException.class)
    public ResponseEntity<List<ValidationError>> handleDuplicateRoomException(DuplicateRoomException exception) {
        ValidationError validationError = new ValidationError("Room",
                "Room already exists with the same roomNumber: " + exception.getRoomNumber() + " in the House with houseId: " + exception.getHouseId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<List<ValidationError>> handleDuplicateEmailException(DuplicateEmailException exception) {
        ValidationError validationError = new ValidationError("Email",
                "User has already registered with email: " + exception.getEmail());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleTokenNotFoundException() {
        ValidationError validationError = new ValidationError("Token",
                "User token invalid or expired.");
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExtraNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleExtraNotFoundException(ExtraNotFoundException exception) {
        ValidationError validationError = new ValidationError("Extra",
                "Extra not found with id: " + exception.getId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }
}