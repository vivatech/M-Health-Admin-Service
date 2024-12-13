package com.mhealth.admin.test;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.controllers.NurseServiceController;
import com.mhealth.admin.dto.request.NurseServiceRequest;
import com.mhealth.admin.model.NurseService;
import com.mhealth.admin.repository.NurseServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NurseServiceControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/admin/nurse-services";
    private static final String CREATED_MESSAGE = "Nurse service created successfully";
    private static final String UPDATED_MESSAGE = "Nurse service updated successfully";
    private static final String STATUS_UPDATED_MESSAGE = "Nurse service status updated successfully";

    @MockBean
    private NurseServiceRepository repository;

    @Autowired
    private NurseServiceController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Create Nurse Service API Tests")
    class CreateNurseServiceTests {

        @Test
        @DisplayName("Should create a new nurse service successfully")
        void testCreateNurseServiceSuccess() throws Exception {
            NurseServiceRequest request = new NurseServiceRequest(
                    "Nursing Basic", "image_url", 500.0F, "Nursing Basic SL", "Description SL",
                    50.0F, 550.0F, "PERCENTAGE", "ACTIVE", "Description");
            NurseService mockService = new NurseService(
                    1, "Nursing Basic", "image_url", 500.0F, "Nursing Basic SL", "Description SL",
                    50.0F, 550.0F, "PERCENTAGE", "ACTIVE", "Description",
                    LocalDateTime.now(), LocalDateTime.now());

            when(repository.save(any(NurseService.class))).thenReturn(mockService);
            when(messageSource.getMessage(Constants.NURSE_SERVICE_CREATED, null, Locale.ENGLISH)).thenReturn(CREATED_MESSAGE);

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.seviceName").value("Nursing Basic"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));

            verify(repository, times(1)).save(any(NurseService.class));
        }
    }

    @Nested
    @DisplayName("Update Nurse Service API Tests")
    class UpdateNurseServiceTests {

        @Test
        @DisplayName("Should update an existing nurse service successfully")
        void testUpdateNurseServiceSuccess() throws Exception {
            NurseServiceRequest request = new NurseServiceRequest(
                    "Nursing Advanced", "updated_image_url", 700.0F, "Nursing Advanced SL", "Updated Description SL",
                    70.0F, 770.0F, "FIXED", "INACTIVE", "Updated Description");
            NurseService existingService = new NurseService(
                    1, "Nursing Basic", "image_url", 500.0F, "Nursing Basic SL", "Description SL",
                    50.0F, 550.0F, "PERCENTAGE", "ACTIVE", "Description",
                    LocalDateTime.now(), LocalDateTime.now());

            when(repository.findById(1)).thenReturn(Optional.of(existingService));
            when(repository.save(any(NurseService.class))).thenReturn(existingService);
            when(messageSource.getMessage(Constants.NURSE_SERVICE_UPDATED, null, Locale.ENGLISH)).thenReturn(UPDATED_MESSAGE);

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.seviceName").value("Nursing Advanced"))
                    .andExpect(jsonPath("$.data.status").value("INACTIVE"));

            verify(repository, times(1)).save(any(NurseService.class));
        }

        @Test
        @DisplayName("Should return not found if nurse service does not exist")
        void testUpdateNurseServiceNotFound() throws Exception {
            NurseServiceRequest request = new NurseServiceRequest(
                    "Nursing Advanced", "updated_image_url", 700.0F, "Nursing Advanced SL", "Updated Description SL",
                    70.0F, 770.0F, "FIXED", "INACTIVE", "Updated Description");

            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(NurseService.class));
        }
    }

    @Nested
    @DisplayName("Update Nurse Service Status API Tests")
    class UpdateNurseServiceStatusTests {

        @Test
        @DisplayName("Should update nurse service status successfully")
        void testUpdateNurseServiceStatusSuccess() throws Exception {
            NurseService existingService = new NurseService(
                    1, "Nursing Basic", "image_url", 500.0F, "Nursing Basic SL", "Description SL",
                    50.0F, 550.0F, "PERCENTAGE", "ACTIVE", "Description",
                    LocalDateTime.now(), LocalDateTime.now());

            when(repository.findById(1)).thenReturn(Optional.of(existingService));
            when(messageSource.getMessage(Constants.NURSE_SERVICE_STATUS_UPDATED, null, Locale.ENGLISH))
                    .thenReturn("Status updated successfully");

            performPut(BASE_URL + "/1/status?status=INACTIVE")  // Use PUT method here
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.status").value("INACTIVE"));

            verify(repository, times(1)).save(any(NurseService.class));
        }

        @Test
        @DisplayName("Should return not found if nurse service does not exist for status update")
        void testUpdateNurseServiceStatusNotFound() throws Exception {
            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1/status?status=INACTIVE")  // Use PUT method here
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(NurseService.class));
        }
    }
}
