package com.mhealth.admin.model;
import com.mhealth.admin.dto.enums.PaymentStatus;
import com.mhealth.admin.dto.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "mh_group_payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Column(name = "currency", length = 20)
    private String currency;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "slsh_amount", nullable = false)
    private float slshAmount;

    @Column(name = "transaction_id", length = 20)
    private String transactionId;

    @Column(name = "proof_file", length = 250)
    private String proofFile;

    @Column(name = "photo", length = 250)
    private String photo;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
}
