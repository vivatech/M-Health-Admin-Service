package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.SlotStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotTypeRequest {
    @NotBlank(message = "Type is mandatory")
    private String type;

    @NotBlank(message = "Value is mandatory")
    private String value;

    @NotNull(message = "Status is mandatory")
    private SlotStatus status;
}
