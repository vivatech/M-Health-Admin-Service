package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.YesNo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mh_package_user")
public class PackageUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private Packages packageInfo;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "total_chat")
    private Integer totalChat;

    @Column(name = "total_video_call")
    private Integer totalVideoCall;

    @Column(name = "total_health_tip")
    private Integer totalHealthTip;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_expire", nullable = false)
    @Enumerated(EnumType.STRING)
    private YesNo isExpire;
}
