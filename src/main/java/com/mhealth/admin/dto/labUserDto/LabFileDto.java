package com.mhealth.admin.dto.labUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LabFileDto {
    private String documentName;
    private MultipartFile document;

    public String validate(){
        StringBuilder validationErrors = new StringBuilder();

        // Check for empty fields
        if (StringUtils.isEmpty(documentName)) {
            validationErrors.append("Document name is required. ");
        }
        if (document == null || document.isEmpty()) {
            validationErrors.append("Multipart file is required. ");
        }
        return validationErrors.toString().isEmpty() ? null : validationErrors.toString().trim();
    }
}
