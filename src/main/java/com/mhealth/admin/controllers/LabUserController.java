package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.labUserDto.LabUserRequestDto;
import com.mhealth.admin.dto.labUserDto.LabUserUpdateRequestDto;
import com.mhealth.admin.service.LabUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Lab User Controller", description = "APIs For Handling Lab user Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/user/lab")
public class LabUserController {

    @Autowired
    private LabUserService labUserService;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * Fetches a list of Lab Users based on the provided filters.
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> getPatientUserList(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                  @RequestParam(required = false) String fullName,
                                                  @RequestParam(required = false) String labName,
                                                  @RequestParam(required = false) String labRegistrationNumber,
                                                  @RequestParam(required = false) String contactNumber,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) Integer cityId,
                                                  @RequestParam(required = false, defaultValue = "user_id") String sortField,
                                                  @RequestParam(required = false, defaultValue = "1") String sortBy,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/list");
            log.info("Request Parameters: fullName={}, labName={}, labRegistrationNumber={}, contactNumber={}, status={}, cityId={}, sortField={}, sortBy={}, page={}, size={}"
                    , fullName, labName, labRegistrationNumber, contactNumber, status, cityId, sortField, sortBy, page, size);

            Object response = labUserService.getLabUserList(locale, fullName, labName, labRegistrationNumber, contactNumber, status, cityId, sortField, sortBy, page, size);

            log.info("Response Sent For /api/v1/admin/user/lab/list: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception found in /api/v1/admin/user/lab/list : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Creating new Lab User
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createLabUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                           @ModelAttribute LabUserRequestDto labUserRequestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/create");
            log.info("Request Body: {}", labUserRequestDto);

            Object response = labUserService.createLabUser(locale, labUserRequestDto);

            log.info("Response Sent For /api/v1/admin/user/lab/lab: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception /api/v1/admin/user/lab/lab: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Updating Lab User
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateLabUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                           @RequestParam Integer userId,
                                           @ModelAttribute LabUserUpdateRequestDto labUserUpdateRequestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/update");
            log.info("Request Parameter: userId={}", userId);
            log.info("Request Body: {}", labUserUpdateRequestDto);

            Object response = labUserService.updateLabUser(locale, userId, labUserUpdateRequestDto);

            log.info("Response Sent For /api/v1/admin/user/lab/update: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception found in /api/v1/admin/user/lab/update : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Updating Status of Lab User
     * Status should be either 'A' or 'I'
     */
    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public ResponseEntity<?> updateLabUserStatus(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer userId,
                                                 @RequestParam String status) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/update-status");
            log.info("Request Parameter: userId={}, status={}", userId, status);

            Object response = labUserService.updateLabUserStatus(locale, userId, status);

            log.info("Response Sent For /api/v1/admin/user/lab/update-status: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception in /api/v1/admin/user/lab/update-status: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch user by userId
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getLabUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                        @PathVariable Integer userId) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/" + userId);

            Object response = labUserService.getLabUser(locale, userId);

            log.info("Response Sent For /api/v1/admin/user/lab/" + userId + ": {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception found in /api/v1/admin/user/lab/ : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete lab document
     */
    @RequestMapping(value = "/delete-lab-document", method = RequestMethod.POST)
    public ResponseEntity<?> deleteLabDocument(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                              @RequestParam Integer userId,
                                              @RequestParam Integer documentId) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/delete-lab-document");
            log.info("Request Parameters : userId={}, documentId={}", userId, documentId);

            Object response = labUserService.deleteLabDocument(locale, userId, documentId);

            log.info("Response Sent For /api/v1/admin/user/lab/delete-lab-document, Params :" + userId + "and " + documentId + " : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception found in /api/v1/admin/user/lab/delete-lab-document : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete lab user
     */
    @RequestMapping(value = "/delete-user", method = RequestMethod.POST)
    public ResponseEntity<?> deleteLabUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                           @RequestParam Integer userId) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/delete-user");
            log.info("Request Parameters : userId={}", userId);

            Object response = labUserService.deleteLabUser(locale, userId);

            log.info("Response Sent For /api/v1/admin/user/lab/delete-user, Params :" + userId + " : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception found in /api/v1/admin/user/lab/delete-user : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
