package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.DeviceType;
import com.mhealth.admin.dto.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileReleaseRequest {
    @NotBlank(message = "App version is mandatory")
    @NotNull(message = "App version is mandatory")
    private String appVersion;

    private String clientName;

    @NotNull(message = "isDeprecated is mandatory")
    private Boolean isDeprecated;

    @NotNull(message = "isTerminated is mandatory")
    private Boolean isTerminated;

    private String message;

    @NotNull(message = "User type is mandatory")
    private UserType userType;

    @NotNull(message = "Device type is mandatory")
    private DeviceType deviceType;
}
