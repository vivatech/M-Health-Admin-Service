package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDoctorRequest {
    private String doctorName;
    private List<Integer> specializationIds;
    private String availability;
    private String sortBy;
    private Integer languageFluency;
    private Integer cityId;
    private Integer clinicId;
    private String consultType;
    private Integer pageNumber;
    private Integer pageSize;
    private Boolean isInternational;
}