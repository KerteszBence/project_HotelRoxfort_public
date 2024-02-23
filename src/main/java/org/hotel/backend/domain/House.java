package org.hotel.backend.domain;



import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "house")
@Data
@NoArgsConstructor
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long houseId;

    @Column(name = "house_name", unique = true)
    private String houseName;

    @Column(name = "house_route")
    private String houseRoute;

    @Column(name = "house_description")
    private String houseDescription;

    @Column(name = "house_available")
    private boolean houseAvailable = true;

    @OneToMany(mappedBy = "house")
    private List<FileRegistry> fileRegistryList;

    @OneToMany(mappedBy = "house")
    private List<Room> roomList;
}