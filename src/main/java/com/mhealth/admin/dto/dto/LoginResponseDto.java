package com.mhealth.admin.dto.dto;

import com.mhealth.admin.dto.response.PermissionRoleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String doctorId;
    private String token;
    private boolean isInternational;
    private List<String> permissions;

    public LoginResponseDto(String doctorId,String token,boolean isInternational){
        this.doctorId = doctorId;
        this.token = token;
        this.isInternational = isInternational;
    }
}