package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.SupportTicketStatus;
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
@Table(name = "mh_support_ticket")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "support_ticket_id")
    private Integer supportTicketId;

    @Column(name = "support_ticket_title")
    private String supportTicketTitle;

    @Column(name = "support_ticket_description")
    private String supportTicketDescription;

    @Column(name = "attachment_id")
    private Integer attachmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "support_ticket_status")
    private SupportTicketStatus supportTicketStatus = SupportTicketStatus.Open;

    @Column(name = "support_ticket_created_by")
    private Integer supportTicketCreatedBy;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "support_ticket_created_at")
    private LocalDateTime supportTicketCreatedAt;

    @Column(name = "support_ticket_updated_at")
    private LocalDateTime supportTicketUpdatedAt;

}

