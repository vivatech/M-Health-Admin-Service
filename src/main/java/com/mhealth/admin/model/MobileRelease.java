package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.DeviceType;
import com.mhealth.admin.dto.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mh_mobile_release")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileRelease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "app_version", nullable = false)
    private String appVersion;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "is_depricated", nullable = false)
    private Boolean isDeprecated;

    @Column(name = "is_terminated", nullable = false)
    private Boolean isTerminated;

    @Column(name = "message")
    @Lob
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType = UserType.Patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType = DeviceType.Android;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}