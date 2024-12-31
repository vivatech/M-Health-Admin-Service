package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.PackageType;
import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthTipPackageRequest {

    @NotBlank(message = "Package name is required.")
    @NotNull(message = "Package name is required.")
    private String packageName;

    private String packageNameSl;

    @NotNull(message = "Duration ID is required.")
    private Integer durationId;

    @Positive(message = "Package price must be positive.")
    @NotNull(message = "Package price must be positive.")
    private Float packagePrice;

    @Positive(message = "Package video price must be positive.")
    private Float packagePriceVideo;

    @NotNull(message = "Package type is required.")
    private PackageType type;

    @NotNull(message = "Status is required.")
    private StatusAI status;

    @NotNull(message = "Category ID is required.")
    private Integer categoryId;
}
