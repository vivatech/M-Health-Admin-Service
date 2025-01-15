package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.Classification;
import com.mhealth.admin.dto.enums.YesNo;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUserRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String password;
    private Integer countryId;
    private Integer stateId;
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
    private YesNo isInternational;
    private String merchantNumber;
    private Float visitAdminCommission;
    private Float callAdminCommission;
    private Float visitConsultationFee;
    private Float callConsultationFee;
    private Float visitFinalConsultationFee;
    private Float callFinalConsultationFee;
    private String notificationLanguage;
    private List<Integer> languagesFluency;
    private List<Integer> specializations;
    private MultipartFile profilePicture;
    private MultipartFile doctorIdDocument;
    private List<Map<String, MultipartFile>> documents;
}

