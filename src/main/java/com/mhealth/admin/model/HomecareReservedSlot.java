package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_homecare_reserved_slots")
public class HomecareReservedSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "case_id", nullable = false)
    private Integer caseId;

    @Column(name = "slot_id", nullable = false)
    private Integer slotId;

    @Column(name = "consult_date", nullable = false)
    private LocalDate consultDate;

}
