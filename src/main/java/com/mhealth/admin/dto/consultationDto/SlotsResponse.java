package com.mhealth.admin.dto.consultationDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SlotsResponse {
    private int slotId;
    private String slotTime;
    private String startTime;
    private String endTime;
}
