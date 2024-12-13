package com.mhealth.admin.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdersSearchRequest {
    private String patientName;
    private String doctorName;
    private String consultationDate;
    private int page = 0;
    private int size = 10;
}
