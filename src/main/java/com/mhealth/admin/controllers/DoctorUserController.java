package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.request.DoctorUserRequestDto;
import com.mhealth.admin.dto.request.SetDoctorAvailabilityRequestDto;
import com.mhealth.admin.dto.request.DoctorUserUpdateRequestDto;
import com.mhealth.admin.service.DoctorUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Doctor User Controller", description = "APIs For Handling Doctor User Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/user/doctor")
public class DoctorUserController {

    @Autowired
    private DoctorUserService doctorUserService;

    @Autowired
    private ObjectMapper objectMapper;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> getDoctorUserList(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String contactNumber,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String isInternational,
                                                  @RequestParam(required = false) String sortField,
                                                  @RequestParam(defaultValue = "1") String sortBy,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Request Received For /api/v1/admin/user/doctor/list");
            log.info("Request Parameters: name={}, email={}, contactNumber={}, status={}, isInternational={}, sortField={}, sortBy={}, page={}, size={}", name, email, contactNumber, status, isInternational, sortField, sortBy, page, size);

            Object response = doctorUserService.getDoctorsUserList(locale, name, email, contactNumber, status, isInternational, sortField, sortBy, page, size);

            log.info("Response Sent For /api/v1/admin/user/doctor/list: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createDoctorUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @ModelAttribute DoctorUserRequestDto doctorUserRequestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/doctor/create");
            log.info("Request Body: {}", doctorUserRequestDto);

            Object response = doctorUserService.createDoctorUser(locale, doctorUserRequestDto);

            log.info("Response Sent For /api/v1/admin/user/doctor/create: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateDoctorUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer userId,
                                                 @ModelAttribute DoctorUserUpdateRequestDto doctorUserUpdateRequestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/doctor/update");
            log.info("Request Parameter: userId={}", userId);
            log.info("Request Body: {}", doctorUserUpdateRequestDto);

            Object response = doctorUserService.updateDoctorUser(locale, userId, doctorUserUpdateRequestDto);

            log.info("Response Sent For /api/v1/admin/user/doctor/update: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public ResponseEntity<?> updateDoctorUserStatus(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                    @RequestParam Integer userId,
                                                    @RequestParam String status) {
        try {
            log.info("Request Received For /api/v1/admin/user/doctor/update-status");
            log.info("Request Parameter: userId={}, status={}", userId, status);

            Object response = doctorUserService.updateDoctorUserStatus(locale, userId, status);

            log.info("Response Sent For /api/v1/admin/user/doctor/update-status: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getDoctorUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                           @PathVariable Integer userId) {
        try {
            log.info("Request Received For /api/v1/admin/user/doctor/" + userId);

            Object response = doctorUserService.getDoctorUser(locale, userId);

            log.info("Response Sent For /api/v1/admin/user/doctor/" + userId + ": {}", objectMapper.writeValueAsString(response));

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get doctor availability by doctor id
     */
    @RequestMapping(value = "/get-availability", method = RequestMethod.GET)
    public ResponseEntity<?> setDoctorAvailability(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                   @RequestParam Integer doctorId) {

        try {
            log.info("Request Received For /api/v1/admin/user/doctor/get-availability");
            log.info("Request param: doctorId={}", doctorId);

            Object response = doctorUserService.getDoctorAvailability(locale, doctorId);

            log.info("Response Sent For /api/v1/admin/user/doctor/get-availability: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Set doctor availability
     */
    @RequestMapping(value = "/set-availability", method = RequestMethod.POST)
    public ResponseEntity<?> setDoctorAvailability(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                   @RequestBody SetDoctorAvailabilityRequestDto doctorUserRequestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/doctor/set-availability");
            log.info("Request Body: {}", doctorUserRequestDto);

            Object response = doctorUserService.setDoctorAvailability(locale, doctorUserRequestDto);

            log.info("Response Sent For /api/v1/admin/user/doctor/set-availability: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
