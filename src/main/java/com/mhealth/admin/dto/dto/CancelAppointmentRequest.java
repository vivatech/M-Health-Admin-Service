package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CancelAppointmentRequest {
    private Integer userId;
    private Integer caseId;
    private String message;

    public String validate(){
        StringBuilder sb = new StringBuilder();
        if(userId == null || userId <= 0){
            sb.append("User Id is required!");
        }
        if(caseId == null || caseId <= 0){
            sb.append("Case Id is required!");
        }
        if(StringUtils.isEmpty(message)){
            sb.append("Message is required!");
        }

        return sb.toString().isEmpty() ? null : sb.toString();
    }
}
