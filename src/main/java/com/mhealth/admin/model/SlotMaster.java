package com.mhealth.admin.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mh_slot_master")
public class SlotMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Integer slotId;

    @ManyToOne
    @JoinColumn(name = "slot_type_id", nullable = false)
    private SlotType slotType;

    @Column(name = "slot_day", nullable = false)
    private String slotDay;

    @Column(name = "slot_time", nullable = false)
    private String slotTime;

    @Column(name = "slot_start_time")
    private LocalTime slotStartTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
