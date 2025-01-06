package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "auth_assignment")
public class AuthAssignment {

    @EmbeddedId
    private AuthAssignmentKey id;

    @Column(name = "created_at")
    private Integer createdAt;
}
