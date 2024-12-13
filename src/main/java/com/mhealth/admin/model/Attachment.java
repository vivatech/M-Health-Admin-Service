package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Integer attachmentId;

    @Column(name = "attachment_label", nullable = false, length = 300)
    private String attachmentLabel;

    @Column(name = "attachment_name", length = 300)
    private String attachmentName;

    @Column(name = "attachment_path", columnDefinition = "TEXT")
    private String attachmentPath;

    @Column(name = "attachment_type", nullable = false, length = 50)
    private String attachmentType;

    @Column(name = "attachment_status", nullable = false)
    private Integer attachmentStatus=1;
}
