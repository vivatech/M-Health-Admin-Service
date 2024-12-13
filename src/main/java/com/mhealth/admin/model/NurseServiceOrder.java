package com.mhealth.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mh_nurse_service_order")
public class NurseServiceOrder {
    @EmbeddedId
    private NurseServiceOrderKey id;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "nurse_id", nullable = false)
    private Integer nurseId;
}
