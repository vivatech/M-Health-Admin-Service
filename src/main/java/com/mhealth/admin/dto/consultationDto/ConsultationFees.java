package com.mhealth.admin.dto.consultationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationFees {
    private Float visit;
    private Float call;
}