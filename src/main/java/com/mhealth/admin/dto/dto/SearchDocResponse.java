package com.mhealth.admin.dto.dto;

import com.mhealth.admin.dto.consultationDto.ConsultationFees;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDocResponse {
    private Integer id;
    private String name;
    private Integer cases;
    private String aboutMe;
    private String experience;
    private String profilePicture;
    private Float rating;
    private Float maxFees;
    private Integer review;
    private Integer hospitalId;
    private String hospitalName;
    private List<String> speciality;
    private List<Integer> specializationIds;
    private List<String> language;
    private ConsultationFees consultationFees;
    private Boolean isAvailableToday = false;
}

