package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentUserRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String countryCode;
    private String password;
    private String notificationLanguage;


    public String validate() {
        StringBuilder validationErrors = new StringBuilder();

        validateRequiredField("First Name", firstName, validationErrors);
        validateRequiredField("Last Name", lastName, validationErrors);
        validateContactNumber(contactNumber, validationErrors);
        validateEmail(email, validationErrors);
        validatePassword(password, validationErrors);

        return validationErrors.isEmpty() ? null : validationErrors.toString().trim();
    }

    private void validateRequiredField(String fieldName, String value, StringBuilder validationErrors) {
        if (value == null || value.trim().isEmpty()) {
            validationErrors.append(fieldName).append(" is required. ");
        }
    }

    private void validateContactNumber(String value, StringBuilder validationErrors) {
        if (value == null || value.trim().isEmpty()) {
            validationErrors.append("Contact number").append(" is required. ");
        } else if (!Pattern.matches("\\d{9}", value)) {
            validationErrors.append("Contact number").append(" must be exactly 9 digits. ");
        }
    }

    private void validateEmail(String value, StringBuilder validationErrors) {
        if (value != null || !value.trim().isEmpty()) {
            if (!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", value)) {
                validationErrors.append("Email is not in a valid format. ");
            }
        }
    }

    private void validatePassword(String value, StringBuilder validationErrors) {
        if (value == null || value.trim().isEmpty()) {
            validationErrors.append("Password is required. ");
        } else if (value.length() < 8 || value.length() > 15) {
            validationErrors.append("Password must be between 8 and 15 characters. ");
        } else if (!Pattern.matches("^(?=.*\\d)(?=.*[$@$!%*#?&])(?=.*[A-Z])[A-Za-z\\d$@$!%*#?&]{8,}$", value)) {
            validationErrors.append("Password must have at least one number, one special character, and one capital letter. ");
        }
    }

}
