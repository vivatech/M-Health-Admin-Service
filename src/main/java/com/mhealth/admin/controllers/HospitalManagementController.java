package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HospitalManagementRequestDto;
import com.mhealth.admin.service.HospitalManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Hospital Management Controller", description = "APIs For Handling Hospital Management Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/user/hospital")
public class HospitalManagementController {

    @Autowired
    private HospitalManagementService hospitalManagementService;

    @Autowired
    private ObjectMapper objectMapper;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> getHospitalList(
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
            log.info("Request Received For /api/v1/admin/user/hospital/list");
            log.info("Request Parameters: name={}, email={}, status={}, contactNumber={}, page={}, size={}", name, email, status, contactNumber, page, size);

            Object response = hospitalManagementService.getHospitalList(locale, name, email, status, contactNumber, sortBy, sortField, page, size);

            log.info("Response Sent For /api/v1/admin/user/hospital/list: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createHospitalManagement(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                      @RequestBody HospitalManagementRequestDto requestDto, HttpServletRequest request) {
        try {
            log.info("Request Received For /api/v1/admin/user/hospital/create");
            log.info("Request Body: {}", requestDto);

            Object response = hospitalManagementService.createHospitalManagement(locale, requestDto, request);

            log.info("Response Sent For /api/v1/admin/user/hospital/create: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateHospitalManagement(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer hospitalId,
                                                 @RequestBody HospitalManagementRequestDto requestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/hospital/update");
            log.info("Request Parameter: hospitalId={}", hospitalId);
            log.info("Request Body: {}", requestDto);

            Object response = hospitalManagementService.updateHospitalManagement(locale, hospitalId, requestDto);

            log.info("Response Sent For /api/v1/admin/user/hospital/update: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{hospitalId}", method = RequestMethod.GET)
    public ResponseEntity<?> getHospitalManagement(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                              @PathVariable Integer hospitalId) {
        try {
            log.info("Request Received For /api/v1/admin/user/hospital/" + hospitalId);

            Object response = hospitalManagementService.getHospitalManagement(locale, hospitalId);

            log.info("Response Sent For /api/v1/admin/user/hospital/" + hospitalId + ": {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public ResponseEntity<?> updateHospitalManagementStatus(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                       @RequestParam Integer hospitalId,
                                                       @RequestParam String status) {
        try {
            log.info("Request Received For /api/v1/admin/user/hospital/update-status");
            log.info("Request Parameter: hospitalId={}, status={}", hospitalId, status);

            Object response = hospitalManagementService.updateHospitalManagementStatus(locale, hospitalId, status);

            log.info("Response Sent For /api/v1/admin/user/hospital/update-status: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteHospitalManagement(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer hospitalId) {
        try {
            log.info("Request Received For /api/v1/admin/user/hospital/delete");
            log.info("Request Parameter: hospitalId={}", hospitalId);

            Object response = hospitalManagementService.deleteHospitalManagement(locale, hospitalId);

            log.info("Response Sent For /api/v1/admin/user/hospital/delete: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
