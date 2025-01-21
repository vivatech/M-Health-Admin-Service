package com.mhealth.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketingUserResponseDto {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String notificationLanguage;
}

