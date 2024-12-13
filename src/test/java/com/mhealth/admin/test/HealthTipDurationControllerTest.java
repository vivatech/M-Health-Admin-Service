package com.mhealth.admin.test;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.controllers.HealthTipDurationController;
import com.mhealth.admin.dto.enums.DurationType;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipDurationRequest;
import com.mhealth.admin.model.HealthTipDuration;
import com.mhealth.admin.repository.HealthTipDurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Locale;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthTipDurationControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/admin/healthtip-durations";
    private static final String DURATION_CREATED_MESSAGE = "Duration created successfully";
    private static final String DURATION_UPDATED_MESSAGE = "Duration updated successfully";
    private static final String STATUS_UPDATED_MESSAGE = "Status updated successfully";

    @MockBean
    private HealthTipDurationRepository repository;

    @Autowired
    private HealthTipDurationController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Update Duration API Tests")
    class UpdateDurationTests {

        @Test
        @DisplayName("Should update an existing health tip duration successfully")
        void testUpdateDurationSuccess() throws Exception {
            HealthTipDurationRequest request = new HealthTipDurationRequest(
                    "2 Months", DurationType.Monthly, 2, StatusAI.I);
            HealthTipDuration existingDuration = HealthTipDuration.builder()
                    .durationId(1)
                    .durationName("1 Month")
                    .status(StatusAI.A)
                    .build();

            when(repository.findById(1)).thenReturn(Optional.of(existingDuration));
            when(repository.save(any(HealthTipDuration.class))).thenReturn(existingDuration);
            when(messageSource.getMessage(Constants.HEALTH_TIP_DURATION_DURATION_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(DURATION_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(repository, times(1)).save(any(HealthTipDuration.class));
        }

        @Test
        @DisplayName("Should return not found if duration does not exist")
        void testUpdateDurationNotFound() throws Exception {
            HealthTipDurationRequest request = new HealthTipDurationRequest(
                    "2 Months", DurationType.Monthly, 2, StatusAI.I);

            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(HealthTipDuration.class));
        }
    }

    @Nested
    @DisplayName("Change Status API Tests")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should change the status of a health tip duration successfully")
        void testChangeStatusSuccess() throws Exception {
            HealthTipDuration existingDuration = HealthTipDuration.builder()
                    .durationId(1)
                    .durationName("1 Month")
                    .status(StatusAI.A)
                    .build();

            when(repository.findById(1)).thenReturn(Optional.of(existingDuration));
            when(messageSource.getMessage(Constants.HEALTH_TIP_DURATION_STATUS_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(STATUS_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(repository, times(1)).save(any(HealthTipDuration.class));
        }

        @Test
        @DisplayName("Should return not found if duration does not exist")
        void testChangeStatusNotFound() throws Exception {
            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(HealthTipDuration.class));
        }
    }
}
