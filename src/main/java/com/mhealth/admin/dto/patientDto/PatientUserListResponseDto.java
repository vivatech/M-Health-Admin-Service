package com.mhealth.admin.dto.patientDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientUserListResponseDto {
    private Integer userId;
    private String name;
    private String email;
    private String contactNumber;
    private String status;
}
