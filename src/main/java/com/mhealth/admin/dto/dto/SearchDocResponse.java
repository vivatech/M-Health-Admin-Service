package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDocResponse {
    private int id;
    private String name;
    private int cases;
    private String aboutMe;
    private String experience;
    private String profilePicture;
    private float rating;
    private float maxFees;
    private int review;
    private int hospitalId;
    private String hospitalName;
    private List<String> speciality;
    private List<Integer> specializationIds;
    private List<String> language;
    private ConsultationFees consultationFees;
    private boolean isAvailableToday = false;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ConsultationFees {
        private Float visit;
        private Float call;
    }
}

