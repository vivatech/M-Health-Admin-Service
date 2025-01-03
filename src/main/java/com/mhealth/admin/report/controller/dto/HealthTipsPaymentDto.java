package com.mhealth.admin.report.controller.dto;


import com.mhealth.admin.dto.enums.YesNo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HealthTipsPaymentDto {

    private  String userName;
    private  String packageName;
    private  String userType;
    private  Float packagePrice;
    private YesNo isExpired;
    private  YesNo isCancelled;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
}
