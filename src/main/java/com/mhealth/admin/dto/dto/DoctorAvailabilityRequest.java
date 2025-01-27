package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityRequest {
    private Integer userId;
    private Integer doctorId;
    private String consultType;
    private Integer pageNo;
    private Integer pageSize;
}
