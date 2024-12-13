package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mh_partner_nurse")
public class PartnerNurse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "country_code")
    private Integer countryCode;

    @Column(name = "zaad_number")
    private String zaadNumber;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "password")
    private String password;

    @Lob
    @Column(name = "qualification")
    private String qualification;

    @Lob
    @Column(name = "residence_address")
    private String residenceAddress;

    @Column(name = "city_id")
    private String cityId;

    @Column(name = "yr_exp")
    private String yrExp;

    @Lob
    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "notification_language")
    private String notificationLanguage;

    @Column(name = "gender")
    private String gender;

    @Column(name = "status")
    private String status;

    @Column(name = "is_suspended")
    private String isSuspended;

    @Column(name = "is_online")
    private String isOnline;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "experience")
    private String experience;

    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
