package com.mhealth.admin.dto.labpricedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LabPriceListDto {
    private Integer labPriceId;
    private String categoryName;
    private String subCategoryName;
    private String labPrice;
    private String labPriceComment;
}
