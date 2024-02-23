package org.hotel.backend.domain;



import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "booking_extra")
@Data
@NoArgsConstructor
public class BookingExtra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingExtraId;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "extra_id")
    private Extra extra;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private BookingRoomUser bookingRoomUser;
}