package org.hotel.backend.repository;



import org.hotel.backend.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Object> findByVerificationToken(String token);

    List<AppUser> findByIsVerifiedFalseAndVerificationTokenExpirationBefore(LocalDateTime expirationTime);

    List<AppUser> findByIsUpdatePendingTrueAndVerificationTokenExpirationBefore(LocalDateTime now);
}