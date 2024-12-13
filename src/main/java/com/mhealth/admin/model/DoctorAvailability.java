package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_doctor_availability")
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_slot_id")
    private Integer doctorSlotId;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName ="user_id", nullable = false)
    private Users doctorId;

    @Column(name = "slot_type_id")
    private Integer slotTypeId;

    @ManyToOne
    @JoinColumn(name = "slot_id", referencedColumnName ="slot_id", nullable = false)
    private SlotMaster slotId;

    @Column(name = "day")
    private String day;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

