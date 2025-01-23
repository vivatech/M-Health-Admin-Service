package com.mhealth.admin.dto.request;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.enums.Classification;
import com.mhealth.admin.dto.enums.DoctorClassification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUserRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String password;
    private String confirmPassword;
    private Integer countryId;
    private Integer provinceId;
    private Integer cityId;
    private Float experience;
    private String aboutMe;
    private String doctorClassification;
    private String countryCode;
    private String hospitalAddress;
    private String hasDoctorVideo;
    private String residenceAddress;
    private String extraActivities;
    private String classification;
    private Integer hospitalId;
    private String gender;
    private String universityName;
    private Integer passingYear;
    private String merchantNumber;
    private Float visitAdminCommission;
    private Float callAdminCommission;
    private Float visitConsultationFee;
    private Float callConsultationFee;
    private Float visitFinalConsultationFee;
    private Float callFinalConsultationFee;
    private String notificationLanguage;
    private List<Integer> languagesFluency;
    private List<Integer> specializations;
    private MultipartFile profilePicture;
    private List<Map<String, Object>> documents;


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
        } else if (countryCode.equals(Constants.LOCAL_COUNTRY_CODE) && !contactNumber.matches("\\d{9}")) {
            validationErrors.append("Contact number must be 9 digits. ");
        } else if (countryCode.equals(Constants.INDIA_COUNTRY_CODE) && !contactNumber.matches("\\d{10}")) {
            validationErrors.append("Contact number must be 10 digits. ");
        }
        if (password == null || password.trim().isEmpty()) {
            validationErrors.append("Password is required. ");
        } else if (password.length() < 8 || password.length() > 15) {
            validationErrors.append("Password must be between 8 and 15 characters. ");
        } else if (!Pattern.matches("^(?=.*\\d)(?=.*[$@$!%*#?&])(?=.*[A-Z])[A-Za-z\\d$@$!%*#?&]{8,}$", password)) {
            validationErrors.append("Password must have at least one number, one special character, and one capital letter. ");
        } else if (!password.equals(confirmPassword)) {
            validationErrors.append("Password and confirm password must be exactly same. ");
        }

        if (countryId == null) {
            validationErrors.append("Country ID is required. ");
        }
        if (provinceId == null) {
            validationErrors.append("Province ID is required. ");
        }
        if (cityId == null) {
            validationErrors.append("City ID is required. ");
        }
        if (experience == null) {
            validationErrors.append("Experience is required. ");
        }
        if (gender == null || gender.trim().isEmpty()) {
            validationErrors.append("Gender is required. ");
        } else if (!List.of(Constants.MALE, Constants.FEMALE).contains(gender)) {
            validationErrors.append("Gender must be either Male or Female. ");
        }
        if (universityName == null || universityName.trim().isEmpty()) {
            validationErrors.append("University name is required. ");
        }
        if (passingYear == null) {
            validationErrors.append("Passing year is required. ");
        }
        if (notificationLanguage == null || notificationLanguage.trim().isEmpty()) {
            validationErrors.append("Notification language is required. ");
        } else if (!List.of(Constants.DEFAULT_LANGUAGE, Constants.ENGLISH_LANGUAGE).contains(notificationLanguage)) {
            validationErrors.append("Notification language must be either sl or en. ");
        }
        if (languagesFluency == null || languagesFluency.isEmpty()) {
            validationErrors.append("Languages fluency is required. ");
        }
        if (countryCode == null || countryCode.trim().isEmpty()) {
            validationErrors.append("Country code is required. ");
        }
        if (hasDoctorVideo == null || hasDoctorVideo.trim().isEmpty()) {
            validationErrors.append("Doctor availability is required. ");
        } else if (!List.of(Constants.VIDEO, Constants.VISIT, Constants.BOTH).contains(hasDoctorVideo)) {
            validationErrors.append("Doctor availability must be video or visit or both only. ");
        }

        // Conditional validation
        if (doctorClassification == null || doctorClassification.trim().isEmpty()) {
            validationErrors.append("Doctor classification is required. ");
        } else {
            try {
                DoctorClassification doctorClassificationEnum = DoctorClassification.valueOf(doctorClassification);
                if (DoctorClassification.specialist.equals(doctorClassificationEnum) &&
                        (specializations == null || specializations.isEmpty())) {
                    validationErrors.append("Specializations are required for specialist classification. ");
                }
            } catch (IllegalArgumentException e) {
                validationErrors.append("Doctor classification must be either general_practitioner or specialist. ");
            }
        }


        if (classification == null || classification.trim().isEmpty()) {
            validationErrors.append("Classification is required. ");
        } else {
            try {
                Classification classificationEnum = Classification.valueOf(classification);

                if (Classification.from_hospital.equals(classificationEnum)) {
                    if (hospitalId == null || hospitalId == 0) {
                        validationErrors.append("Hospital ID is required for 'from_hospital' classification. ");
                    }
                    if (hospitalAddress == null || hospitalAddress.trim().isEmpty()) {
                        validationErrors.append("Clinic address is required for 'from_hospital' classification. ");
                    }
                } else if (Classification.individual.equals(classificationEnum)) {
                    if (residenceAddress == null || residenceAddress.trim().isEmpty()) {
                        validationErrors.append("Residence address is required for 'individual' classification. ");
                    } else if (countryCode.equals(Constants.LOCAL_COUNTRY_CODE)) {
                        if (merchantNumber == null || merchantNumber.trim().isEmpty()) {
                            validationErrors.append("Merchant number is required for 'individual' classification. ");
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                validationErrors.append("Classification must be either from_hospital or individual. ");
            }
        }

        // Return validation errors as a single string or null if no errors
        return validationErrors.toString().isEmpty() ? null : validationErrors.toString().trim();
    }

}

