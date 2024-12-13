package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.dto.enums.YesNo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabSubCategoryRequest {
    @NotNull(message = "Category ID is required")
    private Integer catId;

    @NotNull(message = "Subcategory name is required")
    private String subCatName;

    private String subCatNameSl;

    private CategoryStatus subCatStatus;

    private YesNo isHomeConsultantAvailable;
}
