package org.hotel.backend.repository;


import org.hotel.backend.domain.Extra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtraRepository extends JpaRepository<Extra, Long> {
}