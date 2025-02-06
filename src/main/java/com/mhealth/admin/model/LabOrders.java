package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.LabItemType;
import com.mhealth.admin.dto.enums.OrderStatus;
import com.mhealth.admin.dto.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_lab_orders")
public class LabOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "case_id", referencedColumnName = "case_id")
    private Consultation caseId;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Users patientId;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Users doctor;

    @ManyToOne
    @JoinColumn(name = "lab_id")
    private Users lab;

    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "report_charge")
    private Float reportCharge;

    @Column(name = "extra_charges")
    private Float extraCharges;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "currency_amount")
    private Float currencyAmount;

    @Column(name = "currency")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @Column(name = "report_time_slot")
    private String reportTimeSlot;

    @Column(name = "sample_collection_mode", nullable = false)
    private String sampleCollectionMode;

    @Column(name = "address")
    private String address;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "payment_gateway_type")
    private String paymentGatewayType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private OrderStatus paymentStatus;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type")
    private LabItemType itemType;

    @Column(name = "commission", nullable = false)
    private Float commission;

    @Column(name = "commission_slsh", nullable = false)
    private Float commissionSlsh;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_transfered", nullable = false)
    private TransferStatus isTransferred = TransferStatus.NO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "labOrdersId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LabConsultation> labConsultationList;
}

