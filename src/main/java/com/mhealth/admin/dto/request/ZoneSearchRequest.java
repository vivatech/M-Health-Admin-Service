package com.mhealth.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneSearchRequest {
    private String name;
    private String status;
    private int page;
    private int size;
}
