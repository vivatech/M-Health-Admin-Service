package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Contact number is mandatory")
    @NotNull(message = "Contact number is mandatory")
    private String contactNumber;

    @NotNull(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    private String password;

    @NotBlank(message = "Country code is mandatory")
    @NotNull(message = "Country code is mandatory")
    private String countryCode;
}