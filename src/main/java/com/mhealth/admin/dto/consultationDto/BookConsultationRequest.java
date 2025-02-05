package com.mhealth.admin.dto.consultationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookConsultationRequest {
    private Integer patientId;
    private Integer doctorId;
    private Integer slotId;
}
