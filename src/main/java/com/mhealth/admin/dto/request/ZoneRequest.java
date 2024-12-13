package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneRequest {

    @Size(max = 50, message = "Name cannot exceed 50 characters")
    private String name;

    private String description;

    private StatusAI status;
}