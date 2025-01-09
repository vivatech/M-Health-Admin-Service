package com.mhealth.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class C2BReversalCallbackRequestDto {
    private Map<String, Object> Result;
}

