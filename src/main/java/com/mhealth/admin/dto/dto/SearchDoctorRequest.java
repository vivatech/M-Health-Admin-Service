package com.mhealth.admin.dto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Page no is required!")
    @Min(value = 0, message = "Page no must be greater than or equal to 0")
    private Integer pageNumber;

    @NotNull(message = "Page size is required!")
    @Min(value = 1, message = "Page size must be greater than or equal to 1")
    private Integer pageSize;
    private Boolean isInternational;
}