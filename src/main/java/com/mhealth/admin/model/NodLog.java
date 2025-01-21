package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.Channel;
import com.mhealth.admin.dto.enums.OrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mh_nod_logs")
public class NodLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "search_id", nullable = false, length = 20)
    private String searchId;

    @Column(name = "lat", nullable = false, length = 50)
    private String lat;

    @Column(name = "lng", nullable = false, length = 20)
    private String lng;

    @Column(name = "reason", columnDefinition = "text")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, columnDefinition = "enum('Web','Mobile','USSD') default 'Web'")
    private Channel channel;

    @Column(name = "order_type")
    private String orderType;

    @Column(name = "transaction_type", nullable = false, length = 5, columnDefinition = "varchar(5) default 'NOD'")
    private String transactionType;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
}
