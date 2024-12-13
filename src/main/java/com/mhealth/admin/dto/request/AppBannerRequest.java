package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBannerRequest {
    @NotBlank(message = "Type is required")
    private String type;

    private String iname;

    private String vname;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;
}
