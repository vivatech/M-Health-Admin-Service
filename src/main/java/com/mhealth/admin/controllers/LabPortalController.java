package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.dto.UpdateReportRequestDto;
import com.mhealth.admin.service.LabPortalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Lab portal Controller", description = "APIs For Handling lab portal Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/lab")
public class LabPortalController {
    @Autowired
    private LabPortalService labPortalService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * This will result only 10 latest reports requested by patient
     */
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ResponseEntity<?> getDashboardApi(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                             @RequestParam Integer labId) {
        try {
            log.info("Entering into /api/v1/lab/dashboard");
            log.info("Request Param : labId={}", labId);
            Object response = labPortalService.getDashBoard(labId, locale);
            log.info("Exiting from /api/v1/lab/dashboard and Response : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get List services provided by lab and it's price
     */
    @RequestMapping(value = "/service-price", method = RequestMethod.GET)
    public ResponseEntity<?> servicePrice(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                          @RequestParam Integer labId,
                                          @RequestParam(required = false) Integer catId,
                                          @RequestParam(required = false) Integer subCatId,
                                          @RequestParam(required = false, defaultValue = "labPriceId") String sortField,
                                          @RequestParam(required = false, defaultValue = "desc") String sortBy,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Entering into /api/v1/lab/service-price");
            log.info("Request Param : labId={}, catId={}, subCatId={}, page={}, size={}, sortField={}, sortBy={}", labId, catId, subCatId, page, size, sortField, sortBy);
            Object response = labPortalService.servicePrice(labId, catId, subCatId, page, size, sortField, sortBy, locale);
            log.info("Exiting from /api/v1/lab/service-price and Response : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get report-request list
     */
    @RequestMapping(value = "/report-request", method = RequestMethod.GET)
    public ResponseEntity<?> getReportRequest(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                              @RequestParam Integer labId,
                                              @RequestParam(required = false) Integer catId,
                                              @RequestParam(required = false) Integer subCatId,
                                              @RequestParam(required = false) String patientName,
                                              @RequestParam(required = false) String doctorName,
                                              @RequestParam(required = false) LocalDate createdDate,
                                              @RequestParam(required = false, defaultValue = "id") String sortField,
                                              @RequestParam(required = false, defaultValue = "desc") String sortBy,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Entering into /api/v1/lab/report-request");
            log.info("Request Param : labId={}, catId={}, subCatId={}, patientName={}, doctorName={}, createdDate={}, page={}, size={}, sortField={}, sortBy={}",
                    labId, catId, subCatId, patientName, doctorName, createdDate, page, size, sortField, sortBy);
            Object response = labPortalService.getReportRequest(labId, catId, subCatId, patientName, doctorName, createdDate, page, size, sortField, sortBy, locale);
            log.info("Exiting from /api/v1/lab/report-request and Response : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete Report Document
     */
    @RequestMapping(value = "/delete-lab-report", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteLabReport(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                             @RequestParam Integer labId,
                                             @RequestParam Integer documentId) {
        try {
            log.info("Entering into /api/v1/lab/delete-lab-report");
            log.info("Request Param : labId={}, documentId={}", labId, documentId);
            Object response = labPortalService.deleteLabReport(labId, documentId, locale);
            log.info("Exiting from /api/v1/lab/delete-lab-report and Response : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Upload Report Document
     */
    @RequestMapping(value = "/update-report", method = RequestMethod.PUT)
    public ResponseEntity<?> updateReportRequest(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer labId,
                                                 @RequestParam Integer reportId,
                                                 @ModelAttribute UpdateReportRequestDto request) {
        try {
            log.info("Entering into /api/v1/lab/update-report");
            log.info("Request Param : labId={}, reportId={}", labId, reportId);
            log.info("Request Model attribute : {}", request);
            Object response = labPortalService.updateReportRequest(labId, reportId, request, locale);
            log.info("Exiting from /api/v1/lab/update-report and Response : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
