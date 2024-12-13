package com.mhealth.admin.test;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.controllers.LabSubCategoryController;
import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.request.LabSubCategoryRequest;
import com.mhealth.admin.model.LabCategoryMaster;
import com.mhealth.admin.model.LabSubCategoryMaster;
import com.mhealth.admin.repository.LabCategoryMasterRepository;
import com.mhealth.admin.repository.LabSubCategoryMasterRepository;
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

public class LabSubCategoryControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/lab-sub-categories";
    private static final String LAB_SUB_CATEGORY_ADDED_MESSAGE = "Lab subcategory added successfully";
    private static final String LAB_SUB_CATEGORY_UPDATED_MESSAGE = "Lab subcategory updated successfully";
    private static final String STATUS_UPDATED_MESSAGE = "Status updated successfully";
    private static final String LAB_SUB_CATEGORY_EXISTS = "Lab subcategory already exists";
    private static final String LAB_SUB_CATEGORY_NOT_FOUND = "Lab subcategory not found";

    @MockBean
    private LabSubCategoryMasterRepository repository;

    @MockBean
    private LabCategoryMasterRepository labCategoryRepository;

    @Autowired
    private LabSubCategoryController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Add Lab Subcategory API Tests")
    class AddLabSubCategoryTests {

        @Test
        @DisplayName("Should add a new lab subcategory successfully")
        void testAddLabSubCategorySuccess() throws Exception {
            LabSubCategoryRequest request = new LabSubCategoryRequest(
                    1, 
                    "Biochemistry", 
                    "Биохимия", 
                    CategoryStatus.Active, 
                    YesNo.No 
            );

            LabCategoryMaster labCategory = new LabCategoryMaster(
                    1, 
                    "General Tests", 
                    "Общие тесты", 
                    null, 
                    CategoryStatus.Active, 
                    LocalDateTime.now(),
                    LocalDateTime.now() 
            );

            LabSubCategoryMaster mockLabSubCategory = new LabSubCategoryMaster(
                    1,
                    labCategory, 
                    "Biochemistry", 
                    "Биохимия", 
                    CategoryStatus.Active, 
                    YesNo.No, 
                    LocalDateTime.now(), 
                    LocalDateTime.now()
            );

            when(labCategoryRepository.findById(1)).thenReturn(Optional.of(labCategory));
            when(repository.findBySubCatName("Biochemistry")).thenReturn(Optional.empty());
            when(repository.save(any(LabSubCategoryMaster.class))).thenReturn(mockLabSubCategory);
            when(messageSource.getMessage(Constants.LAB_SUB_CATEGORY_ADDED, null, Locale.ENGLISH))
                    .thenReturn(LAB_SUB_CATEGORY_ADDED_MESSAGE);

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(repository, times(1)).save(any(LabSubCategoryMaster.class));
        }

        @Test
        @DisplayName("Should return conflict if lab subcategory already exists")
        void testAddLabSubCategoryConflict() throws Exception {
            LabCategoryMaster labCategory = new LabCategoryMaster(
                    1,
                    "General Tests",
                    "Общие тесты",
                    null,
                    CategoryStatus.Active,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            LabSubCategoryRequest request = new LabSubCategoryRequest(
                    1,
                    "Biochemistry similar",
                    "Биохимия",
                    CategoryStatus.Active,
                    YesNo.No
            );
            LabSubCategoryMaster mockLabSubCategory = new LabSubCategoryMaster(
                    1,
                    labCategory,
                    "Biochemistry similar",
                    "Биохимия",
                    CategoryStatus.Active,
                    YesNo.No,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            when(labCategoryRepository.findById(1)).thenReturn(Optional.of(labCategory));
            when(repository.findBySubCatName("Biochemistry similar")).thenReturn(Optional.of(mockLabSubCategory));

            performPost(BASE_URL, request)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("409"));

            verify(repository, never()).save(any(LabSubCategoryMaster.class));
        }
    }

    @Nested
    @DisplayName("Update Lab Subcategory API Tests")
    class UpdateLabSubCategoryTests {

        @Test
        @DisplayName("Should update an existing lab subcategory successfully")
        void testUpdateLabSubCategorySuccess() throws Exception {
            LabSubCategoryRequest request = new LabSubCategoryRequest(
                    1, 
                    "Microbiology", 
                    "Микробиология", 
                    CategoryStatus.Inactive, 
                    YesNo.Yes 
            );

            LabSubCategoryMaster existingLabSubCategory = new LabSubCategoryMaster(
                    1,
                    new LabCategoryMaster(1, "General Tests", "Общие тесты", null, CategoryStatus.Active, LocalDateTime.now(), LocalDateTime.now()),
                    "Biochemistry",
                    "Биохимия",
                    CategoryStatus.Active,
                    YesNo.No,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            when(repository.findById(1)).thenReturn(Optional.of(existingLabSubCategory));
            when(repository.save(any(LabSubCategoryMaster.class))).thenReturn(existingLabSubCategory);
            when(messageSource.getMessage(Constants.LAB_SUB_CATEGORY_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(LAB_SUB_CATEGORY_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(repository, times(1)).save(any(LabSubCategoryMaster.class));
        }

        @Test
        @DisplayName("Should return not found if lab subcategory does not exist")
        void testUpdateLabSubCategoryNotFound() throws Exception {
            LabSubCategoryRequest request = new LabSubCategoryRequest(
                    1,
                    "Microbiology",
                    "Микробиология",
                    CategoryStatus.Inactive,
                    YesNo.Yes
            );

            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(LabSubCategoryMaster.class));
        }
    }

    @Nested
    @DisplayName("Change Lab Subcategory Status API Tests")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should change lab subcategory status successfully")
        void testChangeStatusSuccess() throws Exception {
            LabSubCategoryMaster existingLabSubCategory = new LabSubCategoryMaster(
                    1,
                    new LabCategoryMaster(1, "General Tests", "Общие тесты",
                    null, CategoryStatus.Active,
                    LocalDateTime.now(), LocalDateTime.now()),
                    "Biochemistry",
                    "Биохимия",
                    CategoryStatus.Active,
                    YesNo.No,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            when(repository.findById(1)).thenReturn(Optional.of(existingLabSubCategory));
            when(messageSource.getMessage(Constants.STATUS_UPDATED, null, Locale.ENGLISH))
                    .thenReturn(STATUS_UPDATED_MESSAGE);

            performPut(BASE_URL + "/1/status?status=Inactive", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"));

            verify(repository, times(1)).save(any(LabSubCategoryMaster.class));
        }

        @Test
        @DisplayName("Should return not found if lab subcategory does not exist")
        void testChangeStatusNotFound() throws Exception {
            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1/status?status=Inactive", null)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(LabSubCategoryMaster.class));
        }
    }
}
