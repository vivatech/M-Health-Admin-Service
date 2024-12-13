package com.mhealth.admin.dto.availabiltyDoctorDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityRequest {
    private String user_id;
    private String doctor_id;
    private LocalDate date;
    private String consult_type;
}
