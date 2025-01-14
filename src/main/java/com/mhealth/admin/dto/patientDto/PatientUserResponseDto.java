package com.mhealth.admin.dto.patientDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientUserResponseDto {
    private Integer userId;
    private String fullName;
    private String email;
    private String countryCode;
    private String contactNumber;
    private String countryName;
    private int countryId;
    private String stateName;
    private int stateId;
    private String cityName;
    private int cityId;
    private String gender;
    private LocalDate dob;
    private String residentialAddress;
    private String notificationLanguage;
}
