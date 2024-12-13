package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "mh_specialisation")
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false,unique = true)
    private String name;

    @Column(name = "name_sl", nullable = false)
    private String nameSl;

    @Column(name = "photo", nullable = false)
    private String photo;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "description_sl")
    private String descriptionSl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAI status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_featured", nullable = false)
    private Integer isFeatured;

}
