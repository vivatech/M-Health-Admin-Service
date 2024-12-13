package com.mhealth.admin.test;

import com.mhealth.admin.controllers.ConsultationRatingController;
import com.mhealth.admin.dto.enums.ConsultationStatus;
import com.mhealth.admin.model.ConsultationRating;
import com.mhealth.admin.repository.ConsultationRatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConsultationRatingControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/consultation-ratings";

    @MockBean
    private ConsultationRatingRepository repository;

    @Autowired
    private ConsultationRatingController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Change Consultation Rating Status API Tests")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should change consultation rating status successfully")
        void testChangeStatusSuccess() throws Exception {
            ConsultationRating mockRating = ConsultationRating.builder()
                    .id(1)
                    .status(ConsultationStatus.Approve)
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(repository.findById(1)).thenReturn(Optional.of(mockRating));

            performPut(BASE_URL + "/1/status?status=Pending", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(repository, times(1)).save(any(ConsultationRating.class));
        }

        @Test
        @DisplayName("Should return not found if consultation rating does not exist")
        void testChangeStatusNotFound() throws Exception {
            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1/status?status=Pending", null)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(ConsultationRating.class));
        }
    }
}
