package org.hotel.backend.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "extra")
@Data
@NoArgsConstructor
public class Extra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long extraId;

    @Column(name = "extra_description")
    private String extraDescription;

    @Column(name = "price")
    private double price;

    @OneToMany(mappedBy = "extra")
    private List<BookingExtra> bookingExtraList;
}