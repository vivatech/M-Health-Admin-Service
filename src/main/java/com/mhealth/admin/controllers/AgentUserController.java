package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.AgentUserRequestDto;
import com.mhealth.admin.service.AgentUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Agent User Controller", description = "APIs For Handling Agent User Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/user/agent")
public class AgentUserController {

    @Autowired
    private AgentUserService agentUserService;

    @Autowired
    private ObjectMapper objectMapper;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> getAgentList(
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) StatusAI status,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) Integer sortBy,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "0") int page, // Adjusted default value
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            log.info("Request Received For /api/v1/admin/user/agent/list");
            log.info("Request Parameters: name={}, email={}, status={}, contactNumber={}, page={}, size={}", name, email, status, contactNumber, page, size);

            Object response = agentUserService.getAgentList(locale, name, email, status, contactNumber, sortBy, sortField, page, size);

            log.info("Response Sent For /api/v1/admin/user/agent/list: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createAgent(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                         @RequestBody AgentUserRequestDto requestDto, HttpServletRequest request) {
        try {
            log.info("Request Received For /api/v1/admin/user/agent/create");
            log.info("Request Body: {}", requestDto);

            Object response = agentUserService.createAgentUser(locale, requestDto, request);

            log.info("Response Sent For /api/v1/admin/user/agent/create: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateAgentUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                             @RequestParam Integer agentId,
                                             @RequestBody AgentUserRequestDto requestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/agent/update");
            log.info("Request Parameter: agentId={}", agentId);
            log.info("Request Body: {}", requestDto);

            Object response = agentUserService.updateAgentUser(locale, agentId, requestDto);

            log.info("Response Sent For /api/v1/admin/user/agent/update: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{agentId}", method = RequestMethod.GET)
    public ResponseEntity<?> getAgentUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                              @PathVariable Integer agentId) {
        try {
            log.info("Request Received For /api/v1/admin/user/agent/" + agentId);

            Object response = agentUserService.getAgentUserById(locale, agentId);

            log.info("Response Sent For /api/v1/admin/user/agent/" + agentId + ": {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public ResponseEntity<?> updateAgentUserStatus(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                       @RequestParam Integer agentId,
                                                       @RequestParam String status) {
        try {
            log.info("Request Received For /api/v1/admin/user/agent/update-status");
            log.info("Request Parameter: agentId={}, status={}", agentId, status);

            Object response = agentUserService.updateAgentUserStatus(locale, agentId, status);

            log.info("Response Sent For /api/v1/admin/user/agent/update-status: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAgentUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer agentId) {
        try {
            log.info("Request Received For /api/v1/admin/user/agent/delete");
            log.info("Request Parameter: agentId={}", agentId);

            Object response = agentUserService.deleteAgentUser(locale, agentId);

            log.info("Response Sent For /api/v1/admin/user/agent/delete: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
