package com.mhealth.admin.dto.labpricedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LabPriceResponseDto {
    private Integer labPriceId;
    private Map<Integer, String> Category;
    private Map<Integer, String> subCategory;
    private Float labPrice;
    private String labPriceComment;
}
