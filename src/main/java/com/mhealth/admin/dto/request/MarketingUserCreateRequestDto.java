package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketingUserCreateRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String notificationLanguage;

    public String validate() {
        StringBuilder validationErrors = new StringBuilder();

        // Check for empty fields
        if (firstName == null || firstName.trim().isEmpty()) {
            validationErrors.append("First name is required. ");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            validationErrors.append("Last name is required. ");
        }
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            validationErrors.append("Contact number is required. ");
        } else if (!Pattern.matches("\\d{9}", contactNumber)) {
            // Validate if contact number is exactly 9 digits
            validationErrors.append("Contact number must be exactly 9 digits. ");
        }
        if (email == null || email.trim().isEmpty()) {
            validationErrors.append("Email is required. ");
        } else if (!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", email)) {
            // Validate email format
            validationErrors.append("Email is not in a valid format. ");
        }

        return validationErrors.toString().isEmpty() ? null : validationErrors.toString().trim();
    }
}

