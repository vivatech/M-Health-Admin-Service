package com.mhealth.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorPaymentResponse {
    private Integer caseId;
    private Integer doctorId;
    private String doctorName;
    private Integer patientId;
    private String patientName;
    private Integer clinicId;
    private String clinicName;
    private LocalDateTime consultationDate;
    private Float amount;
    private Float commission;
    private Float doctorAmount;
    private Float paymentAmount;
    private String consultationStatus;
    private String paymentStatus;

}
