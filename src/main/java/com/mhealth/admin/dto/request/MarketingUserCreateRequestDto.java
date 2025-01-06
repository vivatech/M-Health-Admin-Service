package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketingUserCreateRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String notificationLanguage;
}

