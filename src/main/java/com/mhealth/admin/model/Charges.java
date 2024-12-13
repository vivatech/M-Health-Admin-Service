package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.CommissionType;
import com.mhealth.admin.dto.enums.FeeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "mh_charges")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Charges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_id")
    private Integer chargeId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type")
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "commission_type", nullable = false)
    private CommissionType commissionType;

    @Column(name = "consultation_fees", nullable = false)
    private Float consultationFees;

    @Column(name = "commission")
    private Float commission;

    @Column(name = "final_consultation_fees")
    private Float finalConsultationFees;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;


}
