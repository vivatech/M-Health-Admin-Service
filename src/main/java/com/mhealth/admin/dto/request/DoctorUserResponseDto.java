package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUserResponseDto {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private Integer countryId;
    private Integer provinceId;
    private Integer cityId;
    private Float experience;
    private String aboutMe;
    private String doctorClassification;
    private String countryCode;
    private String hospitalAddress;
    private String hasDoctorVideo;
    private String residenceAddress;
    private String extraActivities;
    private String classification;
    private Integer hospitalId;
    private String gender;
    private String universityName;
    private Integer passingYear;
    private String merchantNumber;
    private Float visitAdminCommission;
    private Float callAdminCommission;
    private Float visitConsultationFee;
    private Float callConsultationFee;
    private Float visitFinalConsultationFee;
    private Float callFinalConsultationFee;
    private String notificationLanguage;
    private List<String> languagesFluency;
    private List<Integer> specializationList;
    private String profilePicture;
    private List<Map<String, String>> documents;
}

