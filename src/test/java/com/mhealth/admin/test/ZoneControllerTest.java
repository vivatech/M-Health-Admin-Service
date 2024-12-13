package com.mhealth.admin.test;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.controllers.ZoneController;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.ZoneRequest;
import com.mhealth.admin.model.Zone;
import com.mhealth.admin.repository.ZoneRepository;
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

public class ZoneControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/admin/zones";
    private static final String ZONE_ADDED_MESSAGE = "Zone added successfully";
    private static final String ZONE_UPDATED_MESSAGE = "Zone updated successfully";
    private static final String STATUS_UPDATED_MESSAGE = "Status updated successfully";

    @MockBean
    private ZoneRepository repository;

    @Autowired
    private ZoneController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Add Zone API Tests")
    class AddZoneTests {

        @Test
        @DisplayName("Should add a new zone successfully")
        void testAddZoneSuccess() throws Exception {
            ZoneRequest request = new ZoneRequest("Zone 1", "Description of Zone 1", StatusAI.A);
            Zone mockZone = new Zone(1, "Zone 1", "Description of Zone 1", StatusAI.A, null);

            when(repository.findByName(request.getName())).thenReturn(Optional.empty());
            when(repository.save(any(Zone.class))).thenReturn(mockZone);
            when(messageSource.getMessage(Constants.ZONE_ADDED, null, Locale.ENGLISH))
                    .thenReturn(ZONE_ADDED_MESSAGE);

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.message").value(ZONE_ADDED_MESSAGE));

            verify(repository, times(1)).save(any(Zone.class));
        }

        @Test
        @DisplayName("Should return conflict if zone already exists")
        void testAddZoneConflict() throws Exception {
            ZoneRequest request = new ZoneRequest("Zone 1", "Description of Zone 1", StatusAI.A);

            when(repository.findByName(request.getName())).thenReturn(Optional.of(new Zone()));

            performPost(BASE_URL, request)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("409"));

            verify(repository, never()).save(any(Zone.class));
        }
    }

    @Nested
    @DisplayName("Update Zone API Tests")
    class UpdateZoneTests {

        @Test
        @DisplayName("Should update an existing zone successfully")
        void testUpdateZoneSuccess() throws Exception {
            ZoneRequest request = new ZoneRequest("Updated Zone", "Updated Description", StatusAI.A);
            Zone existingZone = new Zone(1, "Zone 1", "Description of Zone 1", StatusAI.A, null);
            Zone updatedZone = new Zone(1, "Updated Zone", "Updated Description", StatusAI.A, null);

            when(repository.findById(1)).thenReturn(Optional.of(existingZone));
            when(repository.save(any(Zone.class))).thenReturn(updatedZone);
            when(messageSource.getMessage(Constants.ZONE_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(ZONE_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.message").value(ZONE_UPDATED_MESSAGE));

            verify(repository, times(1)).save(any(Zone.class));
        }

        @Test
        @DisplayName("Should return not found if zone does not exist")
        void testUpdateZoneNotFound() throws Exception {
            ZoneRequest request = new ZoneRequest("Updated Zone", "Updated Description", StatusAI.A);

            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(Zone.class));
        }
    }

    @Nested
    @DisplayName("Change Zone Status API Tests")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should change zone status successfully")
        void testChangeStatusSuccess() throws Exception {
            Zone existingZone = new Zone(1, "Zone 1", "Description of Zone 1", StatusAI.A, null);

            when(repository.findById(1)).thenReturn(Optional.of(existingZone));
            when(messageSource.getMessage(Constants.STATUS_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(STATUS_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.message").value(STATUS_UPDATED_MESSAGE));

            verify(repository, times(1)).save(any(Zone.class));
        }

        @Test
        @DisplayName("Should return not found if zone does not exist")
        void testChangeStatusNotFound() throws Exception {
            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(Zone.class));
        }
    }
}
