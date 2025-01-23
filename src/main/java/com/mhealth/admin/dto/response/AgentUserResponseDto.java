package com.mhealth.admin.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentUserResponseDto {
    private Integer userId;
    private String name;
    private String email;
    private String contactNumber;
    private String notificationLanguage;
    private String status;
}

