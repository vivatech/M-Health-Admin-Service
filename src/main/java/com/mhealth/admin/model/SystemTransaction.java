package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.Channel;
import com.mhealth.admin.dto.enums.OrderType;
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
@Table(name = "mh_system_transaction")
public class SystemTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "ref_id", referencedColumnName = "case_id")
    private Consultation refId;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Users patientId;

    @Column(name = "transaction_code", length = 50)
    private String transactionCode;

    @Column(name = "transaction_message", columnDefinition = "TEXT")
    private String transactionMessage;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, columnDefinition = "enum('Web','Mobile','USSD') default 'Web'")
    private Channel channel;

    @Column(name = "order_type", columnDefinition = "enum('1','0')")
    private String orderType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
