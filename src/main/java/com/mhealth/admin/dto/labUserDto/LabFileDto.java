package com.mhealth.admin.dto.labUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LabFileDto {
    private String documentName;
    private MultipartFile document;
}
