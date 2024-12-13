package com.mhealth.admin.test;

import com.mhealth.admin.controllers.HealthTipCategoryMasterController;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.HealthTipCategoryMaster;
import com.mhealth.admin.repository.HealthTipCategoryMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthTipCategoryMasterControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/admin/health-tip-categories";

    @MockBean
    private HealthTipCategoryMasterRepository repository;

    @Autowired
    private HealthTipCategoryMasterController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Add Health Tip Category API Tests")
    class AddCategoryTests {

        @Test
        @DisplayName("Should add a new health tip category successfully")
        void testAddCategorySuccess() throws Exception {
            MockMultipartFile mockPhoto = new MockMultipartFile("photo",
                    "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

            ResultActions result = mockMvc.perform(
                    multipart(BASE_URL)
                            .file(mockPhoto)
                            .param("name", "Fitness")
                            .param("nameSl", "Фитнес")
                            .param("description", "Category for fitness tips")
                            .param("descriptionSl", "Категория для фитнес-советов")
                            .param("status", StatusAI.A.toString())
                            .param("priority", "10")
                            .param("isFeatured", "1")
            );

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(repository, times(1)).save(any(HealthTipCategoryMaster.class));
        }
    }

//    @Nested
//    @DisplayName("Update Health Tip Category API Tests")
//    class UpdateCategoryTests {
//
//        @Test
//        @DisplayName("Should update an existing health tip category successfully")
//        void testUpdateCategorySuccess() throws Exception {
//            MockMultipartFile mockPhoto = new MockMultipartFile("photo",
//                    "updated_image.jpg", MediaType.IMAGE_JPEG_VALUE,
//                    "updated image content".getBytes());
//
//            HealthTipCategoryMaster existingCategory = HealthTipCategoryMaster.builder()
//                    .categoryId(1)
//                    .name("Fitness")
//                    .status(StatusAI.A)
//                    .createdAt(LocalDateTime.now())
//                    .build();
//
//            when(repository.findById(1)).thenReturn(Optional.of(existingCategory));
//
//            ResultActions result = mockMvc.perform(
//                    multipart(BASE_URL + "/1")
//                            .file(mockPhoto)
//                            .param("name", "Wellness")
//                            .param("nameSl", "Здоровье")
//                            .param("description", "Updated description")
//                            .param("descriptionSl", "Обновленное описание")
//                            .param("status", StatusAI.I.toString())
//                            .param("priority", "5")
//                            .param("isFeatured", "0")
//            );
//
//            result.andExpect(status().isOk())
//                    .andExpect(jsonPath("$.status").value("SUCCESS"))
//                    .andExpect(jsonPath("$.code").value("200"));
//
//            verify(repository, times(1)).save(any(HealthTipCategoryMaster.class));
//        }
//
//        @Test
//        @DisplayName("Should return not found if category does not exist")
//        void testUpdateCategoryNotFound() throws Exception {
//            when(repository.findById(1)).thenReturn(Optional.empty());
//
//            MockMultipartFile mockPhoto = new MockMultipartFile("photo", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
//
//            ResultActions result = mockMvc.perform(
//                    multipart(BASE_URL + "/1")
//                            .file(mockPhoto)
//                            .param("name", "Wellness")
//                            .param("nameSl", "Здоровье")
//                            .param("description", "Updated description")
//                            .param("descriptionSl", "Обновленное описание")
//                            .param("status", StatusAI.I.toString())
//                            .param("priority", "5")
//                            .param("isFeatured", "0")
//            );
//
//            result.andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.status").value("FAILED"))
//                    .andExpect(jsonPath("$.code").value("404"));
//
//            verify(repository, never()).save(any(HealthTipCategoryMaster.class));
//        }
//    }

    @Nested
    @DisplayName("Change Health Tip Category Status API Tests")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should change health tip category status successfully")
        void testChangeStatusSuccess() throws Exception {
            HealthTipCategoryMaster existingCategory = HealthTipCategoryMaster.builder()
                    .categoryId(1)
                    .name("Fitness")
                    .status(StatusAI.A)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(repository.findById(1)).thenReturn(Optional.of(existingCategory));

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(repository, times(1)).save(any(HealthTipCategoryMaster.class));
        }

        @Test
        @DisplayName("Should return not found if category does not exist")
        void testChangeStatusNotFound() throws Exception {
            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1/status?status=I", null)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(HealthTipCategoryMaster.class));
        }
    }
}
