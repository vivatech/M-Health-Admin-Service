package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.consultationDto.CreateAndEditPatientRequest;
import com.mhealth.admin.dto.consultationDto.SearchPatientRequest;
import com.mhealth.admin.dto.request.RescheduleRequest;
import com.mhealth.admin.dto.request.ViewConsultationRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.dto.response.ViewConsultationResponse;
import com.mhealth.admin.model.Consultation;
import com.mhealth.admin.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Slf4j
@Tag(name = "Consultation Controller", description = "APIs for Consultations statistics and details")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/consultation")
public class ConsultationController {
    @Autowired
    private ConsultationService consultationService;
    @Autowired
    private ObjectMapper objectMapper;

    /*
        List of patient
     */
    @RequestMapping(value = "/search-patient", method = RequestMethod.POST)
    public ResponseEntity<?> searchPatient(@Valid @RequestBody SearchPatientRequest request,
                                          @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        try{
            log.info("Request Received For /api/v1/admin/consultation");
            log.info("Request Body: {}", request);

            Object response = consultationService.searchPatient(request,locale);
            log.info("Response Sent For /api/v1/admin/consultation: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            log.error("Exception found in /api/v1/admin/consultation: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*
        Create And Edit Patient
     */
    @PostMapping
    @Operation(method = "POST",description = "Create patient api")
    public ResponseEntity<Response> createPatient(@Valid @ModelAttribute CreateAndEditPatientRequest request,
                                                  @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        return consultationService.createPatient(request,locale);
    }

    /*
        Getting Patient response
     */
    @GetMapping("/{patientId}")
    @Operation(method = "GET",description = "Get details of individual patient by patient Id")
    public ResponseEntity<Response> getPatient(@PathVariable Integer patientId,
                                               @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        return consultationService.getPatient(patientId, locale);
    }
    /*
        Updating Patient
     */
    @PutMapping("/{id}")
    @Operation(method = "PUT",description = "Update patient")
    public ResponseEntity<Response> updatePatient(@PathVariable Integer id,
                                                  @ModelAttribute CreateAndEditPatientRequest request,
                                                  @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        return consultationService.updatePatient(id, request, locale);
    }

    @PostMapping("/reschedule-time")
    @Operation(method = "POST",description = "Reschedule patient consultation time by ADMIN")
    public ResponseEntity<Response> reScheduleTime(@Valid @RequestBody RescheduleRequest request,
                                                  @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        return consultationService.rescheduleAppointment(request, locale);
    }

    @PostMapping("/open-consultation")
    @Operation(method = "POST",description = "Open consultation of Doctors")
    public ResponseEntity<PaginationResponse<ViewConsultationResponse>> openConsultation(@Valid @RequestBody ViewConsultationRequest request,
                                                                                         @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        return consultationService.openActiveConsultation(request, locale);
    }
}
