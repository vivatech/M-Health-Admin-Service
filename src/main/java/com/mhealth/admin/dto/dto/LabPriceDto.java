package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabPriceDto {
    private Integer id;
    private String categoryName;
    private String subCategoryName;
    private Float labPrice;
    private String labPriceComment;
    private Integer categoryId;
    private Integer subCategoryId;
}
