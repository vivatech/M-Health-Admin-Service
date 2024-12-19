package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplateRequest {
    @NotBlank(message = "Key is mandatory")
    @NotNull(message = "Key is mandatory")
    private String key;

    @NotBlank(message = "Value is mandatory")
    @NotNull(message = "Value is mandatory")
    private String value;

    private String subject;

    private String content;
}
