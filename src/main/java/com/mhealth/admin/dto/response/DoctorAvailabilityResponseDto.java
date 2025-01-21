package com.mhealth.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorAvailabilityResponseDto {
    private Integer slotId;
    private String slotTime;
    private Integer slotTypeId;
    private Time slotStartTime;
}
