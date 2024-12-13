package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mh_home_care_duration")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeCareDuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int duration;
}
