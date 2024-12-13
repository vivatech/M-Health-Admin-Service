package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRefundRequest {
    private String patientName;
    private String labName;
    @NotNull(message = "Page number is required")
    private Integer page=0;
    @NotNull(message = "page size is required")
    private Integer size=10;
}
