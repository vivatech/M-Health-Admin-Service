package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorPaymentRequest {
    private Integer caseId;
    private String patientName;
    private String doctorName;
    private Date consultationDate;
    private Integer page;
    private Integer size;
}
