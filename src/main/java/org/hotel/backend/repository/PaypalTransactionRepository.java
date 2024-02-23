package org.hotel.backend.repository;


import org.hotel.backend.domain.PaypalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaypalTransactionRepository extends JpaRepository<PaypalTransaction, Long> {
}