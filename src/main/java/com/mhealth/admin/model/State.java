package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mh_state")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "abbreviation_code")
    private String abbreviationCode;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
}

