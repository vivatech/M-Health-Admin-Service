package com.mhealth.admin.test;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.controllers.DegreeController;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.DegreeRequest;
import com.mhealth.admin.model.Degree;
import com.mhealth.admin.repository.DegreeRepository;
import com.mhealth.admin.service.DegreeService;
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

public class DegreeControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/degrees";
    private static final String DEGREE_ADDED_MESSAGE = "Degree added successfully";
    private static final String DEGREE_UPDATED_MESSAGE = "Degree updated successfully";
    private static final String STATUS_UPDATED_MESSAGE = "Status updated successfully";

    @MockBean
    private DegreeRepository repository;

    @Autowired
    private DegreeService service;

    @Autowired
    private DegreeController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Add Degree API Tests")
    class AddDegreeTests {

        @Test
        @DisplayName("Should add a new degree successfully")
        void testAddDegreeSuccess() throws Exception {
            DegreeRequest request = new DegreeRequest(
                    "Doctorate", "PhD in Computer Science", StatusAI.A);
            Degree mockDegree = Degree.builder()
                    .degreeId(1)
                    .name("Doctorate")
                    .description("PhD in Computer Science")
                    .status(StatusAI.A)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(repository.save(any(Degree.class))).thenReturn(mockDegree);
            when(messageSource.getMessage(Constants.DEGREE_ADDED, null, Locale.ENGLISH))
                    .thenReturn(DEGREE_ADDED_MESSAGE);

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.message").value(DEGREE_ADDED_MESSAGE));

            verify(repository, times(1)).save(any(Degree.class));
        }
    }

    @Nested
    @DisplayName("Update Degree API Tests")
    class UpdateDegreeTests {

        @Test
        @DisplayName("Should update an existing degree successfully")
        void testUpdateDegreeSuccess() throws Exception {
            DegreeRequest request = new DegreeRequest(
                    "Master's Degree", "MSc in Software Engineering", StatusAI.I);
            Degree existingDegree = Degree.builder()
                    .degreeId(1)
                    .name("Doctorate")
                    .description("Old description")
                    .status(StatusAI.A)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(repository.findById(1)).thenReturn(Optional.of(existingDegree));
            when(repository.save(any(Degree.class))).thenReturn(existingDegree);
            when(messageSource.getMessage(Constants.DEGREE_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(DEGREE_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.message").value(DEGREE_UPDATED_MESSAGE));

            verify(repository, times(1)).save(any(Degree.class));
        }
    }

    @Nested
    @DisplayName("Change Degree Status API Tests")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should change degree status successfully")
        void testChangeStatusSuccess() throws Exception {
            Degree existingDegree = Degree.builder()
                    .degreeId(1)
                    .name("Doctorate")
                    .status(StatusAI.A)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(repository.findById(1)).thenReturn(Optional.of(existingDegree));
            when(repository.save(any(Degree.class))).thenReturn(existingDegree);
            when(messageSource.getMessage(Constants.STATUS_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(STATUS_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.message").value(STATUS_UPDATED_MESSAGE));

            verify(repository, times(1)).save(any(Degree.class));
        }
    }
}