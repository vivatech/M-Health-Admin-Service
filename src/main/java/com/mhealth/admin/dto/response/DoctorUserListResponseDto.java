package com.mhealth.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUserListResponseDto {
    private Integer userId;
    private String doctorName;
    private String clinicName;
    private String isInternational;
    private List<String> specializations;
    private List<Map<String, Object>> charges;
    private String contactNumber;
    private String email;
    private String status;

}
