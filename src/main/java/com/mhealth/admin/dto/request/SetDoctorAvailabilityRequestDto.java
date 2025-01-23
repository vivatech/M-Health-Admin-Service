package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SetDoctorAvailabilityRequestDto {
    private Integer doctorId;
    private Map<String, List<String>> slots;
}
