package com.mhealth.admin.test;

import com.mhealth.admin.controllers.LabCategoryController;
import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.dto.request.LabCategoryRequest;
import com.mhealth.admin.model.LabCategoryMaster;
import com.mhealth.admin.repository.LabCategoryMasterRepository;
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

public class LabCategoryControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/lab-categories";
    private static final String LAB_CATEGORY_ADDED_MESSAGE = "Lab category added successfully";
    private static final String LAB_CATEGORY_UPDATED_MESSAGE = "Lab category updated successfully";
    private static final String STATUS_UPDATED_MESSAGE = "Status updated successfully";

    @MockBean
    private LabCategoryMasterRepository repository;

    @Autowired
    private LabCategoryController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Add Lab Category API Tests")
    class AddLabCategoryTests {

        @Test
        @DisplayName("Should add a new lab category successfully")
        void testAddLabCategorySuccess() throws Exception {
            LabCategoryRequest request = new LabCategoryRequest("Blood Tests 0212", "Sl lang", "image_url", CategoryStatus.Active);
            LabCategoryMaster mockCategory = new LabCategoryMaster();
            mockCategory.setCatId(1);
            mockCategory.setCatName("Blood Tests 0212");
            mockCategory.setCatNameSl("Sl lang");
            mockCategory.setProfilePicture("image_url");
            mockCategory.setCatStatus(CategoryStatus.Active);
            mockCategory.setCatCreatedAt(LocalDateTime.now());
            mockCategory.setCatUpdatedAt(LocalDateTime.now());

            when(repository.findByCatName("Blood Tests 0212")).thenReturn(Optional.empty());
            when(repository.save(any(LabCategoryMaster.class))).thenReturn(mockCategory);

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"));

            verify(repository, times(1)).save(any(LabCategoryMaster.class));
        }

        @Test
        @DisplayName("Should return conflict if lab category already exists")
        void testAddLabCategoryConflict() throws Exception {
            LabCategoryRequest request = new LabCategoryRequest("Blood Tests 0212", "Sl lang", "image_url", CategoryStatus.Active);

            when(repository.findByCatName("Blood Tests 0212")).thenReturn(Optional.of(new LabCategoryMaster()));

            performPost(BASE_URL, request)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value("FAILED"));

            verify(repository, never()).save(any(LabCategoryMaster.class));
        }
    }

    @Nested
    @DisplayName("Update Lab Category API Tests")
    class UpdateLabCategoryTests {

        @Test
        @DisplayName("Should update an existing lab category successfully")
        void testUpdateLabCategorySuccess() throws Exception {
            int categoryId = 1;
            LabCategoryRequest request = new LabCategoryRequest("Updated Category", "Обновленная категория", "updated_image_url", CategoryStatus.Inactive);
            LabCategoryMaster existingCategory = new LabCategoryMaster();
            existingCategory.setCatId(categoryId);
            existingCategory.setCatName("Old Category");
            existingCategory.setCatNameSl("Старый");
            existingCategory.setProfilePicture("old_url");
            existingCategory.setCatStatus(CategoryStatus.Active);
            existingCategory.setCatCreatedAt(LocalDateTime.now());
            existingCategory.setCatUpdatedAt(LocalDateTime.now());

            LabCategoryMaster updatedCategory = new LabCategoryMaster();
            updatedCategory.setCatId(categoryId);
            updatedCategory.setCatName("Updated Category");
            updatedCategory.setCatNameSl("Обновленная категория");
            updatedCategory.setProfilePicture("updated_image_url");
            updatedCategory.setCatStatus(CategoryStatus.Inactive);
            updatedCategory.setCatCreatedAt(LocalDateTime.now());
            updatedCategory.setCatUpdatedAt(LocalDateTime.now());

            when(repository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
            when(repository.save(any(LabCategoryMaster.class))).thenReturn(updatedCategory);

            performPut(BASE_URL + "/" + categoryId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"));

            verify(repository, times(1)).save(any(LabCategoryMaster.class));
        }

        @Test
        @DisplayName("Should return not found if lab category does not exist")
        void testUpdateLabCategoryNotFound() throws Exception {
            int categoryId = 1;
            LabCategoryRequest request = new LabCategoryRequest("Updated Category", "Обновленная категория", "updated_image_url", CategoryStatus.Inactive);

            when(repository.findById(categoryId)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/" + categoryId, request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"));

            verify(repository, never()).save(any(LabCategoryMaster.class));
        }
    }

    @Nested
    @DisplayName("Change Lab Category Status API Tests")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should change lab category status successfully")
        void testChangeStatusSuccess() throws Exception {
            int categoryId = 1;
            CategoryStatus newStatus = CategoryStatus.Inactive;
            LabCategoryMaster existingCategory = new LabCategoryMaster();
            existingCategory.setCatId(categoryId);
            existingCategory.setCatName("Blood Tests 0212");
            existingCategory.setCatNameSl("Sl lang");
            existingCategory.setProfilePicture("image_url");
            existingCategory.setCatStatus(CategoryStatus.Active);
            existingCategory.setCatCreatedAt(LocalDateTime.now());
            existingCategory.setCatUpdatedAt(LocalDateTime.now());

            when(repository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
            when(repository.save(any(LabCategoryMaster.class))).thenReturn(existingCategory);

            performPut(BASE_URL + "/" + categoryId + "/status?status=" + newStatus, null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"));

            verify(repository, times(1)).save(any(LabCategoryMaster.class));
        }

        @Test
        @DisplayName("Should return not found if lab category does not exist")
        void testChangeStatusNotFound() throws Exception {
            int categoryId = 1;
            CategoryStatus newStatus = CategoryStatus.Inactive;

            when(repository.findById(categoryId)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/" + categoryId + "/status?status=" + newStatus, null)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"));

            verify(repository, never()).save(any(LabCategoryMaster.class));
        }
    }
}
