package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.patientDto.PatientUserRequestDto;
import com.mhealth.admin.service.PatientUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Patient Controller", description = "APIs For Handling Patient user Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/user/patient")
public class PatientController {

    @Autowired
    private PatientUserService patientUserService;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * Fetches a list of Patient Users based on the provided filters.
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> getPatientUserList(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String contactNumber,
                                                  @RequestParam(required = false) String sortByEmailAddress,
                                                  @RequestParam(required = false) String sortByContactNumber,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Request Received For /api/v1/admin/user/patient/list");
            log.info("Request Parameters: name={}, email={}, status={}, contactNumber={}, sortByEmailAddress={}, sortByContactNumber={}, page={}, size={}", name, email, status, contactNumber, sortByEmailAddress, sortByContactNumber, page, size);

            Object response = patientUserService.getPatientUserList(locale, name, email, status,contactNumber, sortByEmailAddress, sortByContactNumber, page, size);

            log.info("Response Sent For /api/v1/admin/user/patient/list: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception found in /api/v1/admin/user/patient/list : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Creating new Patient User
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createPatientUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @ModelAttribute PatientUserRequestDto patientUserRequestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/patient/create");
            log.info("Request Body: {}", patientUserRequestDto);

            Object response = patientUserService.createPatientUser(locale, patientUserRequestDto);

            log.info("Response Sent For /api/v1/admin/user/patient/create: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Updating Patient User
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updatePatientUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer userId,
                                                 @ModelAttribute PatientUserRequestDto patientUserRequestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/patient/update");
            log.info("Request Parameter: userId={}", userId);
            log.info("Request Body: {}", patientUserRequestDto);

            Object response = patientUserService.updatePatientUser(locale, userId, patientUserRequestDto);

            log.info("Response Sent For /api/v1/admin/user/patient/update: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception found in /api/v1/admin/user/patient/update : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Updating Status of Patient User
     * Status should be either 'A' or 'I'
     */
    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public ResponseEntity<?> updatePatientUserStatus(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                       @RequestParam Integer userId,
                                                       @RequestParam String status) {
        try {
            log.info("Request Received For /api/v1/admin/user/patient/update-status");
            log.info("Request Parameter: userId={}, status={}", userId, status);

            Object response = patientUserService.updatePatientUserStatus(locale, userId, status);

            log.info("Response Sent For /api/v1/admin/user/patient/update-status: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch user by userId
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getMarketingUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                              @PathVariable Integer userId) {
        try {
            log.info("Request Received For /api/v1/admin/user/patient/" + userId);

            Object response = patientUserService.getPatientUser(locale, userId);

            log.info("Response Sent For /api/v1/admin/user/patient/" + userId + ": {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception found in /api/v1/admin/user/patient/ : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
