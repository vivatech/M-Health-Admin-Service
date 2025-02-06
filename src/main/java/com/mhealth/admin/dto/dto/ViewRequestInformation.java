package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViewRequestInformation {
    private String patientName;
    private String contactNumber;
    private String address;
    private List<String> reports;
}
