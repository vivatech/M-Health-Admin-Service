package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mh_group_orders_consultation")
public class GroupOrderConsultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private Users userId;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
    private Orders orderId;

    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "slsh_amount", nullable = false)
    private Double slshAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "currency", length = 20)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Column(name = "created_on", nullable = false)
    private Date createdOn;

    @Column(name = "paid_on")
    private Date paidOn;

    public enum Status {
        Paid,
        Pending
    }

    public enum UserType {
        InternationalDoctors
    }
}
