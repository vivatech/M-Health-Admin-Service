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
@Table(name = "mh_support_ticket_msgs")
public class SupportTicketMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "support_ticket_msgs_id")
    private Integer supportTicketMsgsId;

    @ManyToOne
    @JoinColumn(name = "support_ticket_id")
    private SupportTicket supportTicket;

    @Column(name = "support_ticket_msgs_detail", nullable = false)
    private String supportTicketMsgsDetail;

    @Column(name = "attachment_id")
    private Integer attachmentId;

    @Column(name = "support_ticket_msgs_created_by", nullable = false)
    private Integer supportTicketMsgsCreatedBy;

    @Column(name = "support_ticket_msgs_created_at", nullable = false)
    private LocalDateTime supportTicketMsgsCreatedAt;

}

