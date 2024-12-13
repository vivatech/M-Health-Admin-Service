package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerifyOtpRequest {
    private String contact_number;
    private String otp;
    private String device_token;
    private String is_registered;
}
