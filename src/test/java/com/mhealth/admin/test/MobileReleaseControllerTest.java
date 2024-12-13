package com.mhealth.admin.test;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.controllers.MobileReleaseController;
import com.mhealth.admin.dto.enums.DeviceType;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.request.MobileReleaseRequest;
import com.mhealth.admin.model.MobileRelease;
import com.mhealth.admin.repository.MobileReleaseRepository;
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

class MobileReleaseControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/mobile-releases";
    private static final String CREATED_MESSAGE = "Mobile release created successfully";
    private static final String UPDATED_MESSAGE = "Mobile release updated successfully";

    @MockBean
    private MobileReleaseRepository repository;

    @Autowired
    private MobileReleaseController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Create Mobile Release API Tests")
    class CreateMobileReleaseTests {

        @Test
        @DisplayName("Should create a new mobile release successfully")
        void testCreateMobileReleaseSuccess() throws Exception {
            MobileReleaseRequest request = new MobileReleaseRequest(
                    "1.0.0", "Client A", false, false, "New update available", UserType.Clinic, DeviceType.Android);
            MobileRelease mockRelease = new MobileRelease(
                    1, "1.0.0", "Client A", false, false, "New update available", UserType.Clinic,DeviceType.Android,
                    LocalDateTime.now(), LocalDateTime.now());

            when(repository.save(any(MobileRelease.class))).thenReturn(mockRelease);
            when(messageSource.getMessage(Constants.MOBILE_RELEASE_CREATED, null, Locale.ENGLISH)).thenReturn(CREATED_MESSAGE);

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.appVersion").value("1.0.0"))
                    .andExpect(jsonPath("$.data.clientName").value("Client A"));

            verify(repository, times(1)).save(any(MobileRelease.class));
        }
    }

    @Nested
    @DisplayName("Update Mobile Release API Tests")
    class UpdateMobileReleaseTests {

        @Test
        @DisplayName("Should update an existing mobile release successfully")
        void testUpdateMobileReleaseSuccess() throws Exception {
            MobileReleaseRequest request = new MobileReleaseRequest(
                    "1.0.1", "Client A", true, false, "Update is now deprecated", UserType.Doctor, DeviceType.IOS);
            MobileRelease existingRelease = new MobileRelease(
                    1, "1.0.0", "Client A", false, false, "Initial release", UserType.Patient, DeviceType.Android,
                    LocalDateTime.now(), LocalDateTime.now());

            when(repository.findById(1)).thenReturn(Optional.of(existingRelease));
            when(repository.save(any(MobileRelease.class))).thenReturn(existingRelease);
            when(messageSource.getMessage(Constants.MOBILE_RELEASE_UPDATED, null, Locale.ENGLISH)).thenReturn(UPDATED_MESSAGE);

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.appVersion").value("1.0.1"))
                    .andExpect(jsonPath("$.data.isDeprecated").value(true))
                    .andExpect(jsonPath("$.data.deviceType").value("IOS"));

            verify(repository, times(1)).save(any(MobileRelease.class));
        }

        @Test
        @DisplayName("Should return not found if mobile release does not exist")
        void testUpdateMobileReleaseNotFound() throws Exception {
            MobileReleaseRequest request = new MobileReleaseRequest(
                    "1.0.1", "Client A", true, false, "Update is now deprecated", UserType.Doctor, DeviceType.IOS);

            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(MobileRelease.class));
        }
    }
}
