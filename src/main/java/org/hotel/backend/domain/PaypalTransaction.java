package org.hotel.backend.domain;



import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "paypal_transaction")
@Data
@NoArgsConstructor
public class PaypalTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentId;
    private String payerId;
}