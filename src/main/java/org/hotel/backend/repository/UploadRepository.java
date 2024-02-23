package org.hotel.backend.repository;


import org.hotel.backend.domain.FileRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadRepository extends JpaRepository<FileRegistry, Long> {
}