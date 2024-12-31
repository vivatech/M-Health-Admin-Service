package com.mhealth.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponseDto<T> {
    private String status_code;
    private String status;
    private String message;
    private Object data;

}

