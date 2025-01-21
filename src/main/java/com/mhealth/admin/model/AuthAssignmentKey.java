package com.mhealth.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AuthAssignmentKey implements Serializable {

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "user_id", nullable = false)
    private String userId;
}

