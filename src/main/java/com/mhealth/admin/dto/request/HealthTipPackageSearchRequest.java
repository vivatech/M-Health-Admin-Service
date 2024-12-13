package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.StatusAI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthTipPackageSearchRequest {
    private String packageName;

    private Integer durationId;

    private StatusAI status;

    private int page = 0;

    private int size = 10;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
