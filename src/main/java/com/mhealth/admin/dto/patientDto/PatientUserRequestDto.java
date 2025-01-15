    package com.mhealth.admin.dto.patientDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientUserRequestDto {
    private MultipartFile profilePicture;
    private String fullName;
    private String email;
    private String contactNumber;
    private Integer countryId;
    private Integer provinceId;
    private Integer cityId;
    private String gender;
    private String notificationLanguage;
    private LocalDate dob;
    private String residentialAddress;
    private Boolean termsAndConditionChecked;

    public String validate() {
        StringBuilder validationErrors = new StringBuilder();

        // Check for empty fields
        if (StringUtils.isEmpty(fullName)) {
            validationErrors.append("Full name is required. ");
        }
        if (dob == null) {
            validationErrors.append("Date of Birth is required. ");
        }
        if (StringUtils.isEmpty(contactNumber)) {
            validationErrors.append("Contact number is required. ");
        } else if (!Pattern.matches("\\d{9}", contactNumber)) {
            // Validate if contact number is exactly 9 digits
            validationErrors.append("Contact number must be exactly 9 digits. ");
        }
        if (StringUtils.isEmpty(residentialAddress)) {
            validationErrors.append("Residential Address is required. ");
        }
        if(countryId == null || countryId <= 0){
            validationErrors.append("Country Id is required. ");
        }
        if(provinceId == null || provinceId <= 0){
            validationErrors.append("State Id is required. ");
        }
        if(cityId == null || cityId <= 0){
            validationErrors.append("City Id is required. ");
        }

        return validationErrors.toString().isEmpty() ? null : validationErrors.toString().trim();
    }
}

