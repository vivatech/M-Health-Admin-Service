package com.mhealth.admin.test;

import com.mhealth.admin.controllers.EmailTemplateController;
import com.mhealth.admin.dto.request.EmailTemplateRequest;
import com.mhealth.admin.model.EmailTemplate;
import com.mhealth.admin.repository.EmailTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailTemplateControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/email-templates";
    private static final String CREATED_MESSAGE = "Email template created successfully";
    private static final String UPDATED_MESSAGE = "Email template updated successfully";

    @MockBean
    private EmailTemplateRepository repository;

    @Autowired
    private EmailTemplateController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Create Email Template API Tests")
    class CreateEmailTemplateTests {

        @Test
        @DisplayName("Should create a new email template successfully")
        void testCreateEmailTemplateSuccess() throws Exception {
            EmailTemplateRequest request = new EmailTemplateRequest("key1", "value1", "subject1", "content1");
            EmailTemplate mockTemplate = new EmailTemplate(
                    null, "key1", "value1", "subject1", "content1", new Date(), null);

            when(repository.findByKey("key1")).thenReturn(Optional.empty()); // No conflict
            when(repository.save(any(EmailTemplate.class))).thenReturn(mockTemplate);
            when(messageSource.getMessage("EMAIL_TEMPLATE_CREATED", null, Locale.ENGLISH)).thenReturn(CREATED_MESSAGE);

            performPost(BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.key").value("key1"))
                    .andExpect(jsonPath("$.data.subject").value("subject1"));

            verify(repository, times(1)).save(any(EmailTemplate.class));
        }

        @Test
        @DisplayName("Should return conflict if email template exists")
        void testCreateEmailTemplateConflict() throws Exception {
            EmailTemplateRequest request = new EmailTemplateRequest("key1", "value1", "subject1", "content1");

            when(repository.findByKey("key1")).thenReturn(Optional.of(new EmailTemplate()));

            performPost(BASE_URL, request)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("409"));

            verify(repository, never()).save(any(EmailTemplate.class));
        }
    }

    @Nested
    @DisplayName("Update Email Template API Tests")
    class UpdateEmailTemplateTests {

        @Test
        @DisplayName("Should update an existing email template successfully")
        void testUpdateEmailTemplateSuccess() throws Exception {
            EmailTemplateRequest request = new EmailTemplateRequest("key1", "updatedValue", "updatedSubject", "updatedContent");
            EmailTemplate existingTemplate = new EmailTemplate(
                    1, "key1", "value1", "subject1", "content1", new Date(), new Date());

            when(repository.findById(1)).thenReturn(Optional.of(existingTemplate)); // Template exists
            when(repository.save(any(EmailTemplate.class))).thenReturn(existingTemplate);
            when(messageSource.getMessage("EMAIL_TEMPLATE_UPDATED", null, Locale.ENGLISH)).thenReturn(UPDATED_MESSAGE);

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.key").value("key1"))
                    .andExpect(jsonPath("$.data.subject").value("updatedSubject"));

            verify(repository, times(1)).save(any(EmailTemplate.class));
        }

        @Test
        @DisplayName("Should return not found if email template does not exist")
        void testUpdateEmailTemplateNotFound() throws Exception {
            EmailTemplateRequest request = new EmailTemplateRequest("key1", "updatedValue", "updatedSubject", "updatedContent");

            when(repository.findById(1)).thenReturn(Optional.empty());

            performPut(BASE_URL + "/1", request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.code").value("404"));

            verify(repository, never()).save(any(EmailTemplate.class));
        }
    }
}

