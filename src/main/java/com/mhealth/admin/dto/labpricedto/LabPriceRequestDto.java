package com.mhealth.admin.dto.labpricedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LabPriceRequestDto {
    private Integer labPriceId;
    private Integer categoryId;
    private Integer subCategoryId;
    private Float labPrice;
    private String labPriceComment;

    public String validate(){
        StringBuilder validationErrors = new StringBuilder();

        if(categoryId == null){
            validationErrors.append("Category id is required. ");
        }

        if(subCategoryId == null){
            validationErrors.append("Sub Category id is required. ");
        }

        if(labPrice == null){
            validationErrors.append("Lab Price is required. ");
        }
        // Return validation errors as a single string or null if no errors
        return validationErrors.toString().isEmpty() ? null : validationErrors.toString().trim();
    }
}
