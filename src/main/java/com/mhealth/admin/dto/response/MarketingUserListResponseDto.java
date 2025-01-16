package com.mhealth.admin.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketingUserListResponseDto {
    private Integer userId;
    private String name;
    private String email;
    private String promoCode;
    private Long totalRegistration;
    private Long totalConsultation;
    private String contactNumber;
    private String status;
}

