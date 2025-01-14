package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalManagementRequestDto {
    private String clinicName;
    private String email;
    private String contactNumber;
    private String notificationContactNumber;
    private String merchantNumber;
    private String priority;
    private String password;
    private String clinicAddress;
    private String notificationLanguage;

    public String validate() {
        StringBuilder validationErrors = new StringBuilder();

        validateRequiredField("Clinic name", clinicName, validationErrors);
        validateContactNumber("Contact number", contactNumber, validationErrors);
        validateOptionalContactNumber("Notification contact number", notificationContactNumber, validationErrors);
        validateRequiredField("Merchant number", merchantNumber, validationErrors);
        validateContactNumber("Merchant number", merchantNumber, validationErrors);
        validateEmail(email, validationErrors);
        validatePassword(password, validationErrors);
        validateAddress("Clinic address", clinicAddress, validationErrors);
        validateOptionalPriority("Priority", priority, validationErrors);

        return validationErrors.isEmpty() ? null : validationErrors.toString().trim();
    }

    private void validateRequiredField(String fieldName, String value, StringBuilder validationErrors) {
        if (value == null || value.trim().isEmpty()) {
            validationErrors.append(fieldName).append(" is required. ");
        }
    }

    private void validateContactNumber(String fieldName, String value, StringBuilder validationErrors) {
        if (value == null || value.trim().isEmpty()) {
            validationErrors.append(fieldName).append(" is required. ");
        } else if (!Pattern.matches("\\d{9}", value)) {
            validationErrors.append(fieldName).append(" must be exactly 9 digits. ");
        }
    }

    private void validateOptionalContactNumber(String fieldName, String value, StringBuilder validationErrors) {
        if (value != null && !value.trim().isEmpty() && !Pattern.matches("\\d{9}", value)) {
            validationErrors.append(fieldName).append(" must be exactly 9 digits. ");
        }
    }

    private void validateOptionalPriority(String fieldName, String value, StringBuilder validationErrors) {
        if (value != null) {
            if (!value.matches("\\d+")) {
                validationErrors.append(fieldName).append(" must contain only numbers. ");
            }
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

    private void validateAddress(String fieldName, String value, StringBuilder validationErrors) {
        if (value == null || value.trim().isEmpty()) {
            validationErrors.append(fieldName).append(" is required. ");
        } else if (!value.matches("^[a-zA-Z0-9.,\":;'\\-_(){}!@#$%^&*=+|?\\s]*$")) {
            validationErrors.append(fieldName).append(" contains invalid characters. Only letters, numbers, spaces, commas, periods, and specific special characters are allowed. ");
        }
    }


}
