package com.mhealth.admin.report.controller;


import com.mhealth.admin.dto.enums.YesNo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

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
