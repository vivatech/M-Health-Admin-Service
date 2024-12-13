package com.mhealth.admin.test;

import com.mhealth.admin.controllers.HealthTipPackageController;
import com.mhealth.admin.dto.enums.PackageType;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipPackageRequest;
import com.mhealth.admin.model.HealthTipDuration;
import com.mhealth.admin.model.HealthTipPackage;
import com.mhealth.admin.repository.HealthTipDurationRepository;
import com.mhealth.admin.repository.HealthTipPackageRepository;
import com.mhealth.admin.service.HealthTipPackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthTipPackageControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/admin/health-tip-packages";
    private static final String PACKAGE_ADDED_MESSAGE = "Health tip package added successfully";
    private static final String PACKAGE_UPDATED_MESSAGE = "Health tip package updated successfully";
    private static final String STATUS_UPDATED_MESSAGE = "Status updated successfully";

    @MockBean
    private HealthTipPackageRepository packageRepository;

    @MockBean
    private HealthTipDurationRepository durationRepository;

    @Autowired
    private HealthTipPackageService service;

    @Autowired
    private HealthTipPackageController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Add Health Tip Package API Tests")
    class AddHealthTipPackageTests {

        @Test
        @DisplayName("Should add a new health tip package successfully")
        void testAddHealthTipPackageSuccess() throws Exception {
            HealthTipDuration mockDuration = new HealthTipDuration();
            mockDuration.setDurationId(1);

            HealthTipPackageRequest request = new HealthTipPackageRequest();
            request.setDurationId(1);
            request.setPackageName("Fitness Tips");
            request.setPackageNameSl("Фитнес Советы");
            request.setPackagePrice(100.0F);
            request.setPackagePriceVideo(150.0F);
            request.setType(PackageType.Paid);
            request.setStatus(StatusAI.A);

            when(durationRepository.findById(1)).thenReturn(Optional.of(mockDuration));

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(packageRepository, times(1)).save(any(HealthTipPackage.class));
        }
    }

    @Nested
    @DisplayName("Update Health Tip Package API Tests")
    class UpdateHealthTipPackageTests {

        @Test
        @DisplayName("Should update an existing health tip package successfully")
        void testUpdateHealthTipPackageSuccess() throws Exception {
            HealthTipDuration mockDuration = new HealthTipDuration();
            mockDuration.setDurationId(1);

            HealthTipPackage existingPackage = new HealthTipPackage();
            existingPackage.setPackageId(1);
            existingPackage.setPackageName("Old Name");

            HealthTipPackageRequest request = new HealthTipPackageRequest();
            request.setPackageName("Updated Name");
            request.setDurationId(1);
            request.setStatus(StatusAI.I);

            when(packageRepository.findById(1)).thenReturn(Optional.of(existingPackage));
            when(durationRepository.findById(1)).thenReturn(Optional.of(mockDuration));

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(packageRepository, times(1)).save(any(HealthTipPackage.class));
        }

        @Test
        @DisplayName("Should return 404 when the health tip package is not found")
        void testUpdateHealthTipPackageNotFound() throws Exception {
            HealthTipPackageRequest request = new HealthTipPackageRequest();
            request.setPackageName("Non-Existent Package");
            request.setDurationId(1);
            request.setStatus(StatusAI.I);

            when(packageRepository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(packageRepository, never()).save(any(HealthTipPackage.class));
        }
    }


    @Nested
    @DisplayName("Change Health Tip Package Status API Tests")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should change health tip package status successfully")
        void testChangeStatusSuccess() throws Exception {
            HealthTipPackage existingPackage = new HealthTipPackage();
            existingPackage.setPackageId(1);
            existingPackage.setStatus(StatusAI.A);

            when(packageRepository.findById(1)).thenReturn(Optional.of(existingPackage));

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(packageRepository, times(1)).save(any(HealthTipPackage.class));
        }
    }
}