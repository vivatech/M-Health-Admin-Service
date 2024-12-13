package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_users_relatives")
public class UserRelative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "relation_with_patient", nullable = false)
    private String relationWithPatient;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
