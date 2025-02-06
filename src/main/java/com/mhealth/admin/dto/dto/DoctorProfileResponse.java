package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorProfileResponse {
    private String doctorName;
    private String doctorPicture;
    private String hospitalNameWithCountry;
    private List<String> specializationName;
    private Float experience;
    private List<String> languageSpoken;
    private String hospitalName;
    private String country;
    private String province;
    private String city;
}