package com.mhealth.admin.dto.dto;

import com.mhealth.admin.dto.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePermissionResponseData {
    private Integer id;

    private UserType roleType;

    private List<String> permissions;
}
