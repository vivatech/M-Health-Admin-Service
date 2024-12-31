package com.mhealth.admin.dto.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerifyLoginOtp {
    @NotBlank(message = "Contact number is mandatory")
    @NotNull(message = "Contact number is mandatory")
    private String contactNumber;

    @NotBlank(message = "OTP is mandatory")
    @NotNull(message = "OTP is mandatory")
    private String otp;

    @NotBlank(message = "New Password is mandatory")
    @NotNull(message = "New Password is mandatory")
    private String newPassword;
}
