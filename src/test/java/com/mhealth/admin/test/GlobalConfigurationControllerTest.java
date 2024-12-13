package com.mhealth.admin.test;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.controllers.GlobalConfigurationController;
import com.mhealth.admin.dto.request.GlobalConfigurationRequest;
import com.mhealth.admin.model.GlobalConfiguration;
import com.mhealth.admin.repository.GlobalConfigurationRepository;
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

class GlobalConfigurationControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/admin/configurations";
    private static final String CREATED_MESSAGE = "Configuration created successfully";
    private static final String UPDATED_MESSAGE = "Configuration updated successfully";

    @MockBean
    private GlobalConfigurationRepository repository;

    @Autowired
    private GlobalConfigurationController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Create Configuration API Tests")
    class CreateConfigurationTests {

        @Test
        @DisplayName("Should create a new configuration successfully")
        void testCreateConfigurationSuccess() throws Exception {
            GlobalConfigurationRequest request = new GlobalConfigurationRequest("key1", "value1", "desc1", 1);
            GlobalConfiguration mockConfiguration = new GlobalConfiguration(
                    1, "key1", "value1", "desc1", 1);

            when(repository.findByKey("key1")).thenReturn(Optional.empty());
            when(repository.save(any(GlobalConfiguration.class))).thenReturn(mockConfiguration);
            when(messageSource.getMessage(Constants.GLOBAL_CONFIG_CREATED_SUCCESSFULLY, null, Locale.ENGLISH)).thenReturn(CREATED_MESSAGE);

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.key").value("key1"))
                    .andExpect(jsonPath("$.data.value").value("value1"));

            verify(repository, times(1)).save(any(GlobalConfiguration.class));
        }

        @Test
        @DisplayName("Should return conflict if configuration key already exists")
        void testCreateConfigurationConflict() throws Exception {
            GlobalConfigurationRequest request = new GlobalConfigurationRequest("key1", "value1", "desc1", 1);

            when(repository.findByKey("key1")).thenReturn(Optional.of(new GlobalConfiguration()));

            performPost(BASE_URL, request)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(GlobalConfiguration.class));
        }
    }

    @Nested
    @DisplayName("Update Configuration API Tests")
    class UpdateConfigurationTests {

        @Test
        @DisplayName("Should update an existing configuration successfully")
        void testUpdateConfigurationSuccess() throws Exception {
            GlobalConfigurationRequest request = new GlobalConfigurationRequest("key1", "updatedValue", "updatedDesc", 2);
            GlobalConfiguration existingConfig = new GlobalConfiguration(
                    1, "key1", "value1", "desc1", 1);

            when(repository.findById(1)).thenReturn(Optional.of(existingConfig));
            when(repository.save(any(GlobalConfiguration.class))).thenReturn(existingConfig);
            when(messageSource.getMessage(Constants.GLOBAL_CONFIG_UPDATED_SUCCESSFULLY, null, Locale.ENGLISH)).thenReturn(UPDATED_MESSAGE);

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.key").value("key1"))
                    .andExpect(jsonPath("$.data.value").value("updatedValue"));

            verify(repository, times(1)).save(any(GlobalConfiguration.class));
        }

        @Test
        @DisplayName("Should return not found if configuration does not exist")
        void testUpdateConfigurationNotFound() throws Exception {
            GlobalConfigurationRequest request = new GlobalConfigurationRequest("key1", "updatedValue", "updatedDesc", 2);

            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(GlobalConfiguration.class));
        }
    }
}
