package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mh_video_attachment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "case_id")
    private Integer caseId;

    @Column(name = "from_id", nullable = false)
    private Integer fromId;

    @Column(name = "to_id", nullable = false)
    private Integer toId;

    @Column(name = "file_name", nullable = false, length = 256)
    private String fileName;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "file_type", nullable = false, length = 20)
    private String fileType;
}

