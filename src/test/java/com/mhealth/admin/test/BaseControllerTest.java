package com.mhealth.admin.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected MessageSource messageSource;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    protected ResultActions performPost(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)));
    }

    protected ResultActions performPost(String url, Object body,boolean multipart) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content(toJson(body)));
    }

    protected ResultActions performPut(String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)));
    }

    protected ResultActions performPut(String url, Object body,boolean multipart) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content(toJson(body)));
    }

    protected ResultActions performPut(String url) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performPatch(String url, Object body) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)));
    }

    protected ResultActions performPatch(String url) throws Exception {
        return mockMvc.perform(patch(url).contentType(MediaType.APPLICATION_JSON));
    }

    @BeforeEach
    void logTestStart(TestInfo testInfo) {
        log.info("Running {}: {}", testInfo.getDisplayName(), testInfo.getTestClass().map(Class::getSimpleName).orElse("Unknown Class"));
    }

    @AfterEach
    void logTestEnd(TestInfo testInfo) {
        log.info("Completed {}: {}", testInfo.getDisplayName(), testInfo.getTestClass().map(Class::getSimpleName).orElse("Unknown Class"));
    }
}