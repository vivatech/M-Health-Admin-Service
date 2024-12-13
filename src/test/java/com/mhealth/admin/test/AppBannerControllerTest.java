package com.mhealth.admin.test;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.controllers.AppBannerController;
import com.mhealth.admin.dto.request.AppBannerRequest;
import com.mhealth.admin.model.AppBanner;
import com.mhealth.admin.repository.AppBannerRepository;
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

class AppBannerControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/app-banners";

    @MockBean
    private AppBannerRepository repository;

    @Autowired
    private AppBannerController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Create App Banner API Tests")
    class CreateAppBannerTests {

        @Test
        @DisplayName("Should create a new app banner successfully")
        void testCreateAppBannerSuccess() throws Exception {
            AppBannerRequest request = new AppBannerRequest("type1", "iname1", "vname1", 1);
            AppBanner mockBanner = new AppBanner(1, "type1", "iname1", "vname1", 1, LocalDateTime.now(), LocalDateTime.now());

            when(repository.save(any(AppBanner.class))).thenReturn(mockBanner);
            when(messageSource.getMessage(Constants.APP_BANNER_CREATED_SUCCESSFULLY, null, Locale.ENGLISH))
                    .thenReturn("App Banner created successfully");

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.type").value("type1"))
                    .andExpect(jsonPath("$.data.iname").value("iname1"));

            verify(repository, times(1)).save(any(AppBanner.class));
        }
    }

    @Nested
    @DisplayName("Update App Banner API Tests")
    class UpdateAppBannerTests {

        @Test
        @DisplayName("Should update an existing app banner successfully")
        void testUpdateAppBannerSuccess() throws Exception {
            AppBannerRequest request = new AppBannerRequest("newType", "newIname", "newVname", 1);
            AppBanner existingBanner = new AppBanner(1, "oldType", "oldIname", "oldVname", 1, LocalDateTime.now(), LocalDateTime.now());

            when(repository.findById(1)).thenReturn(Optional.of(existingBanner));
            when(repository.save(any(AppBanner.class))).thenReturn(existingBanner);
            when(messageSource.getMessage(Constants.APP_BANNER_UPDATED_SUCCESSFULLY, null, Locale.ENGLISH))
                    .thenReturn("App Banner updated successfully");

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.type").value("newType"))
                    .andExpect(jsonPath("$.data.iname").value("newIname"));

            verify(repository, times(1)).save(any(AppBanner.class));
        }

        @Test
        @DisplayName("Should return error when updating a non-existing app banner")
        void testUpdateAppBannerNotFound() throws Exception {
            AppBannerRequest request = new AppBannerRequest("newType", "newIname", "newVname", 1);

            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"));

            verify(repository, times(0)).save(any(AppBanner.class));
        }
    }
}
