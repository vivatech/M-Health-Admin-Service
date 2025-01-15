package com.mhealth.admin.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentUserResponseDto {
    private Integer userId;
    private String clinicName;
    private String email;
    private String contactNumber;
    private String notificationContactNumber;
    private String merchantNumber;
    private Integer priority;
    private String clinicAddress;
    private String notificationLanguage;
    private String status;
}

