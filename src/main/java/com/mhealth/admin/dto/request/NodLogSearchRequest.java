package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodLogSearchRequest {
    @NotNull(message = "Page is required")
    private Long page;

    @NotNull(message = "Size is required")
    private Long size;

    private String searchId;
    private String patientName;
}