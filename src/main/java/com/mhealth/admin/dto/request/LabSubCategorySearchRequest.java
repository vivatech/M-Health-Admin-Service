package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabSubCategorySearchRequest {
    private String subCatName;

    private Integer catId;

    private CategoryStatus subCatStatus;

    private int page = 0;

    private int size = 10;
}
