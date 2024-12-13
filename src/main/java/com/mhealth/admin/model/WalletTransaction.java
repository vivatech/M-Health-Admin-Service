package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_wallet_transaction")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Users patientId;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "payment_gateway_type")
    private String paymentGatewayType;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_status", nullable = false)
    private String transactionStatus;

    @Column(name = "ref_transaction_id")
    private String refTransactionId;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "service_type", nullable = false)
    private String serviceType;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "is_debit_credit", nullable = false)
    private String isDebitCredit;

    @Column(name = "previous_balance", nullable = false)
    private Float previousBalance;

    @Column(name = "current_balance", nullable = false)
    private Float currentBalance;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "payer_id", nullable = false)
    private Integer payerId;

    @Column(name = "payee_id", nullable = false)
    private Integer payeeId;

    @Column(name = "payment_number")
    private String paymentNumber;

    @Column(name = "payer_mobile", nullable = false)
    private String payerMobile;

    @Column(name = "payee_mobile", nullable = false)
    private String payeeMobile;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "issuer_transaction_id")
    private String issuerTransactionId;

}

