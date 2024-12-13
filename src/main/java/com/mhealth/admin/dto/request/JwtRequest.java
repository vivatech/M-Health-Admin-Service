package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtRequest {
    private String contactNumber;
    private String otp;
    private String isRegistered;

    public Integer userId;
    public String dataBundleOffer;
    public String hasApp;
    public String dataBundleOfferMessage;
}
