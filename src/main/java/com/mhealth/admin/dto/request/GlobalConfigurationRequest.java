package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalConfigurationRequest {

    @NotBlank(message = "Key is required and cannot be blank")
    @NotNull(message = "Key is required and cannot be blank")
    private String key;

    @NotBlank(message = "Value is required and cannot be blank")
    @NotNull(message = "Value is required and cannot be blank")
    private String value;

    private String description;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
}
