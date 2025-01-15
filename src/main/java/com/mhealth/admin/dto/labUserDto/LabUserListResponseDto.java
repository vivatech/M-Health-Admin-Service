package com.mhealth.admin.dto.labUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LabUserListResponseDto {
    private Integer userId;
    private String labName;
    private String fullName;
    private String labRegistrationNumber;
    private String contactNumber;
    private String status;
    private String city;
}
