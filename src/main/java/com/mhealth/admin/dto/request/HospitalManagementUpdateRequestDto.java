package com.mhealth.admin.dto.request;

import com.mhealth.admin.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalManagementUpdateRequestDto {
    private String clinicName;
    private String contactNumber;
    private String email;
    private String merchantNumber;
    private String notificationContactNumber;
    private String priority;
    private String clinicAddress;
    private String notificationLanguage;
    private MultipartFile profilePicture;

    public String validate() {
        StringBuilder validationErrors = new StringBuilder();

        // Check for empty fields
        if (StringUtils.isEmpty(clinicName))
            validationErrors.append("Clinic name is required. ");

        if (StringUtils.isEmpty(contactNumber))
            validationErrors.append("Contact number is required. ");

        if (StringUtils.isEmpty(merchantNumber))
            validationErrors.append("Merchant number is required. ");

        if (StringUtils.isEmpty(clinicAddress))
            validationErrors.append("Clinic address is required. ");

        // Return validation errors as a single string or null if no errors
        return validationErrors.toString().isEmpty() ? null : validationErrors.toString().trim();
    }
}
