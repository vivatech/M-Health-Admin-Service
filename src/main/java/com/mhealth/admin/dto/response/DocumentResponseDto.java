package com.mhealth.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentResponseDto {
    private Integer userId;
    private Integer documentId;
    private String filePath;
    private String fileName;
}
