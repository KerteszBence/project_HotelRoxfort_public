package org.hotel.backend.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "room")
@Data
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(name = "room_number")
    private Long roomNumber;

    @Column(name = "room_description")
    @Size(max = 1000, message = "Description length must be at most 1000 characters")
    private String roomDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    private RoomType roomType;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "price_per_night")
    private double pricePerNight;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "house_id")
    private House house;

    @OneToMany(mappedBy = "room")
    private List<FileRegistry> fileRegistryList;

    @OneToMany(mappedBy = "room")
    private List<BookingRoomUser> bookingRoomUserList;
}