package com.mhealth.admin.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalListResponse {
    private Integer hospital_id;
    private String picture;
    private String clinic_name;
    private String hospital_address;
}
