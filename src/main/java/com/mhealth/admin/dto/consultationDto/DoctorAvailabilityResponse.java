package com.mhealth.admin.dto.consultationDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityResponse {
    private LocalDate date;
    private int totalAvailableSlots;
    private int morningSlotCount;
    private int afternoonSlotCount;
    private int eveningSlotCount;
    private Object morningSlot;
    private Object afternoonSlot;
    private Object eveningSlot;
}
