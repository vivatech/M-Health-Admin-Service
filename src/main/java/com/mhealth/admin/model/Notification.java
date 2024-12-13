package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mh_notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @Column(name = "from_id")
    private Integer fromId;

    @Column(name = "to_id")
    private Integer toId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_read", columnDefinition = "ENUM('0', '1')", nullable = false)
    private String isRead;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "case_id")
    private Integer caseId;

    @Column(name = "url")
    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
