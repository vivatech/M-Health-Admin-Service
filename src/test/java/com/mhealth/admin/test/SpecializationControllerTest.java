package com.mhealth.admin.test;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.controllers.SpecializationController;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.SpecializationRequest;
import com.mhealth.admin.model.Specialization;
import com.mhealth.admin.repository.SpecializationRepository;
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

public class SpecializationControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/admin/specializations";
    private static final String SPECIALIZATION_ADDED_MESSAGE = "Specialization added successfully";
    private static final String SPECIALIZATION_UPDATED_MESSAGE = "Specialization updated successfully";
    private static final String STATUS_UPDATED_MESSAGE = "Status updated successfully";

    @MockBean
    private SpecializationRepository repository;

    @Autowired
    private SpecializationController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Add Specialization API Tests")
    class AddSpecializationTests {

        @Test
        @DisplayName("Should add a new specialization successfully")
        void testAddSpecializationSuccess() throws Exception {
            SpecializationRequest request = new SpecializationRequest(
                    "Cardiology", "Кардиология", "image_url", "Specialization in Cardiology",
                    "Специализация в кардиологии", StatusAI.A, 1);
            Specialization mockSpecialization = Specialization.builder()
                    .id(1)
                    .name("Cardiology")
                    .nameSl("Кардиология")
                    .photo("image_url")
                    .description("Specialization in Cardiology")
                    .descriptionSl("Специализация в кардиологии")
                    .status(StatusAI.A)
                    .isFeatured(1)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(repository.findByName("Cardiology")).thenReturn(Optional.empty());
            when(repository.save(any(Specialization.class))).thenReturn(mockSpecialization);
            when(messageSource.getMessage(Constants.SPECIALIZATION_ADDED, null, Locale.ENGLISH))
                    .thenReturn(SPECIALIZATION_ADDED_MESSAGE);

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.message").value(SPECIALIZATION_ADDED_MESSAGE));

            verify(repository, times(1)).save(any(Specialization.class));
        }

        @Test
        @DisplayName("Should return conflict if specialization already exists")
        void testAddSpecializationConflict() throws Exception {
            SpecializationRequest request = new SpecializationRequest(
                    "Cardiology", "Кардиология", "image_url", "Specialization in Cardiology",
                    "Специализация в кардиологии", StatusAI.A, 1);

            when(repository.findByName("Cardiology")).thenReturn(Optional.of(new Specialization()));

            performPost(BASE_URL, request)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("409"));

            verify(repository, never()).save(any(Specialization.class));
        }
    }

    @Nested
    @DisplayName("Update Specialization API Tests")
    class UpdateSpecializationTests {

        @Test
        @DisplayName("Should update an existing specialization successfully")
        void testUpdateSpecializationSuccess() throws Exception {
            SpecializationRequest request = new SpecializationRequest(
                    "Neurology", "Неврология", "updated_image_url", "Specialization in Neurology",
                    "Специализация в неврологии", StatusAI.I, 0);
            Specialization existingSpecialization = Specialization.builder()
                    .id(1)
                    .name("Cardiology")
                    .status(StatusAI.I)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(repository.findById(1)).thenReturn(Optional.of(existingSpecialization));
            when(repository.save(any(Specialization.class))).thenReturn(existingSpecialization);
            when(messageSource.getMessage(Constants.SPECIALIZATION_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(SPECIALIZATION_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.message").value(SPECIALIZATION_UPDATED_MESSAGE));

            verify(repository, times(1)).save(any(Specialization.class));
        }

        @Test
        @DisplayName("Should return not found if specialization does not exist")
        void testUpdateSpecializationNotFound() throws Exception {
            SpecializationRequest request = new SpecializationRequest(
                    "Neurology", "Неврология", "updated_image_url", "Specialization in Neurology",
                    "Специализация в неврологии", StatusAI.I, 0);

            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(Specialization.class));
        }
    }

    @Nested
    @DisplayName("Change Specialization Status API Tests")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should change specialization status successfully")
        void testChangeStatusSuccess() throws Exception {
            Specialization existingSpecialization = Specialization.builder()
                    .id(1)
                    .name("Cardiology")
                    .status(StatusAI.A)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(repository.findById(1)).thenReturn(Optional.of(existingSpecialization));
            when(messageSource.getMessage(Constants.STATUS_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(STATUS_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.message").value(STATUS_UPDATED_MESSAGE));

            verify(repository, times(1)).save(any(Specialization.class));
        }

        @Test
        @DisplayName("Should return not found if specialization does not exist")
        void testChangeStatusNotFound() throws Exception {
            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(Specialization.class));
        }
    }
}

