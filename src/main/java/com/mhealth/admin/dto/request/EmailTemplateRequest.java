package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplateRequest {
    @NotBlank(message = "Key is mandatory")
    private String key;

    @NotBlank(message = "Value is mandatory")
    private String value;

    private String subject;

    private String content;
}
