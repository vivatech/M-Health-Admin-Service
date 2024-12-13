package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "mh_wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "wallet_id")
    private BigInteger walletId;

    @Column(name = "wallet_number")
    private Integer walletNumber;

    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_type")
    private UserType userType;

    @Column(name = "balance")
    private Float balance;

    @Column(name = "previous_balance")
    private Float previousBalance;

    @Column(name = "blance_credit")
    private Float balanceCredit;

    @Column(name = "balance_debit")
    private Float balanceDebit;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
