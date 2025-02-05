package com.mhealth.admin.payment;

import com.mhealth.admin.dto.enums.ConsultationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.mhealth.admin.config.Constants.DEFAULT_COUNTRY;
import static com.mhealth.admin.constants.Constants.DEFAULT_LANGUAGE;
import static com.mhealth.admin.constants.Constants.Dollar_Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationPaymentDto {
    @NotNull(message = "Patient ID is required")
    private Integer patientId; //mandatory

    @NotNull(message = "Doctor ID is required")
    private Integer doctorId;
    @NotNull(message = "Slot ID is required")
    private Integer slotId; //for checking whether consultation already present or not
    @NotNull(message = "Consultation Date is required")
    private LocalDate consultationDate;
    @NotNull(message = "Consult Type is required")
    private ConsultType consultType; //either video or clinic visit
    @NotNull(message = "Consultation Type is required")
    private ConsultationType consultationType; //either Paid or Free
    @NotNull(message = "Who is submitting the consultation request?")
    private Integer submittedBy;

    private Double amount;
    private Double currencyAmount;
    private String currency = Dollar_Currency;
    private String country = DEFAULT_COUNTRY;

    private String paymentMsisdn;

    public enum ConsultType {
        CLINIC_VISIT,
        VIDEO
    }
}
