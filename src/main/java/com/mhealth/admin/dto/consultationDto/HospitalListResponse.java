package com.mhealth.admin.dto.consultationDto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalListResponse {
    private Integer hospitalId;
    private String picture;
    private String clinicName;
    private String hospitalAddress;
    private String latitude;
    private String longitude;
}
