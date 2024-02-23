package org.hotel.backend.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
public class BookingRoomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "appUser_id")
    private AppUser appUser;

    @OneToMany(mappedBy = "bookingRoomUser")
    private List<BookingExtra> bookingExtraList;

    private LocalDateTime inDate;

    private LocalDateTime outDate;

    private String verificationToken;

    private LocalDateTime verificationTokenExpiration;

    private boolean isVerified = false;

    private boolean isUpdatePending = false;

    private boolean isDeleted = false;
}