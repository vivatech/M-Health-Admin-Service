package com.mhealth.admin.dto.labUserDto;

import com.mhealth.admin.dto.response.DocumentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabUserResponseDto {
    private Integer userId;
    private String fullName;
    private String email;
    private String countryCode;
    private String contactNumber;
    private String countryName;
    private int countryId;
    private String stateName;
    private int stateId;
    private String cityName;
    private int cityId;
    private String labName;
    private String labAddress;
    private String labRegistrationNumber;
    private List<DocumentResponseDto> documentList;
}
