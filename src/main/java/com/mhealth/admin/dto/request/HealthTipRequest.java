package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthTipRequest {

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Description is required")
    private String description;

    private MultipartFile photo;

    private MultipartFile video;

    private MultipartFile videoThumb;

    @NotNull(message = "Status is required")
    private StatusAI status;
}