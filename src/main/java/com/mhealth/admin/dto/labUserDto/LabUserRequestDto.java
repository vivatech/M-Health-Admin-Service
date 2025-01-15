package com.mhealth.admin.dto.labUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabUserRequestDto {
    private MultipartFile profilePicture;
    private String labName;
    private String fullName;
    private String email;
    private String contactNumber;
    private String password;
    private String confirmPassword;
    private String labRegistrationNumber;
    private Integer countryId;
    private Integer provinceId;
    private Integer cityId;
    private String labAddress;
    private List<LabFileDto> documentList;

    public String validate() {
        StringBuilder validationErrors = new StringBuilder();

        // Check for empty fields
        if (StringUtils.isEmpty(fullName)) {
            validationErrors.append("Full name is required. ");
        }
        if (StringUtils.isEmpty(labName)) {
            validationErrors.append("Lab Name is required. ");
        }
        if (StringUtils.isEmpty(contactNumber)) {
            validationErrors.append("Contact number is required. ");
        } else if (!Pattern.matches("\\d{9}", contactNumber)) {
            // Validate if contact number is exactly 9 digits
            validationErrors.append("Contact number must be exactly 9 digits. ");
        }
        if (StringUtils.isEmpty(password)) {
            validationErrors.append("Password is required. ");
        }
        if (StringUtils.isEmpty(confirmPassword)) {
            validationErrors.append("Confirm password is required. ");
        }
        if (!StringUtils.isEmpty(confirmPassword) && !StringUtils.isEmpty(password) && (!confirmPassword.equals(password))) {
            validationErrors.append("Confirm password and Password should be same. ");
        }
        if (StringUtils.isEmpty(labRegistrationNumber)) {
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
        if (StringUtils.isEmpty(labAddress)) {
            validationErrors.append("Lab address is required. ");
        }

        return validationErrors.toString().isEmpty() ? null : validationErrors.toString().trim();
    }
}
