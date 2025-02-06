package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServicePriceResponseDto {
    private String catName;
    private String subCatName;
    private Float labPrice;
    private String labComment;
}
