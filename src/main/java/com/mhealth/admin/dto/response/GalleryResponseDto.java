package com.mhealth.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GalleryResponseDto {
    private Integer id;
    private String name;
    private String fileLocation;
}
