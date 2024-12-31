package com.mhealth.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SDPAuthResponse {
    private String msg;
    private String token;
    private String refreshToken;
}
