package com.mhealth.admin.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;

import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.BaseResponseDto;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.CancelAppointmentRequest;
import com.mhealth.admin.dto.dto.DoctorAvailabilityRequest;
import com.mhealth.admin.dto.dto.SearchDocResponse;
import com.mhealth.admin.dto.dto.SearchDoctorRequest;
import com.mhealth.admin.dto.request.RescheduleRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.BookAnAppointmentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.DataInput;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

@RestController
@Slf4j
@Tag(name = "Book an appointment Controller", description = "APIs For Handling appointment/consultation Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/user/appointment")
public class BookAnAppointmentController {
    @Autowired
    private BookAnAppointmentService bookAnAppointmentService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MessageSource messageSource;

    /**
     * Search list of doctors from given city, clinic id, list of specialization in the form of id's
     * Search list of doctors based upon languages he/she knows
     * @return list of doctors in pagination form
     */
    @RequestMapping(value = "/search-doctor", method = RequestMethod.POST)
    public ResponseEntity<?> searchDoctor(@RequestBody SearchDoctorRequest r,
                                          @RequestHeader(name = "X-localization", required = false, defaultValue = "so")
                                          Locale locale) {
        try {
            log.info("Entry in /api/v1/admin/user/appointment");
            log.info("Request Body : {}", r);
            String url = "https://baanobackend.vivatechrnd.com/patient/book-appointment/search-doctor";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept-Language", "en");

            // Combine headers and body into an HttpEntity
            HttpEntity<SearchDoctorRequest> entity = new HttpEntity<>(r, headers);

            // Initialize RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<BaseResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    BaseResponseDto.class
            );

//            SearchDocResponse responseDto = new ObjectMapper().readValue((DataInput) response.getBody(), SearchDocResponse.class);
            log.info("Exiting /api/v1/admin/user/appointment and it's ResponseBody is : {}", response.getBody());
            return ResponseEntity.ok().body(response.getBody());

        } catch (Exception e) {
            log.error("Exception : {}", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Doctor latest available slots lists from current date
     */
    @RequestMapping(value = "/doctor-availability-latest-list", method = RequestMethod.POST)
    public ResponseEntity<?> searchDoctor(@RequestBody DoctorAvailabilityRequest request,
                                          @RequestHeader(name = "X-localization", required = false, defaultValue = "so")
                                          Locale locale) {
        try {
            log.info("Entry in /api/v1/admin/user/appointment/doctor-availability-latest-list");
            log.info("Request Body : {}", request);
            String url = "https://baanobackend.vivatechrnd.com/patient/book-appointment/doctor-availability-latest-list";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept-Language", locale.toString());

            // Combine headers and body into an HttpEntity
            HttpEntity<DoctorAvailabilityRequest> entity = new HttpEntity<>(request, headers);

            // Initialize RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<BaseResponseDto> response = restTemplate.exchange(url, HttpMethod.POST, entity, BaseResponseDto.class);
            log.info("Exiting /api/v1/admin/user/appointment/doctor-availability-latest-list and it's ResponseBody is : {}", response.getBody());
            return ResponseEntity.ok().body(response.getBody());

        } catch (Exception e) {
            log.error("Exception : {}", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * View Doctor profile
     */
    @RequestMapping(value = "/view-doctor-profile", method = RequestMethod.GET)
    public ResponseEntity<?> viewDoctorProfile(@RequestParam Integer doctorId,
                                               @RequestHeader(name = "X-localization", required = false, defaultValue = "so")
                                               Locale locale) {
        try {
            log.info("Entry in /api/v1/admin/user/appointment/view-doctor-profile");
            log.info("Request Param : doctorId={}", doctorId);
            Object response = bookAnAppointmentService.viewDoctorProfile(doctorId, locale);
            log.info("Exiting /api/v1/admin/user/appointment/view-doctor-profile and it's ResponseBody is : {}", new ObjectMapper().writeValueAsString(response));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Exception :{}", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Availability sort
     */
    @RequestMapping(value = "/sort-by-availability", method = RequestMethod.GET)
    public ResponseEntity<?> sortByAvailability(@RequestHeader(name = "X-localization", required = false, defaultValue = "so")
                                                Locale locale) {
        try {
            log.info("Entry in /api/v1/admin/user/appointment/sort-by-availability");
            Object response = bookAnAppointmentService.sortByAvailability(locale);
            log.info("Exiting /api/v1/admin/user/appointment/sort-by-availability and it's ResponseBody is : {}", new ObjectMapper().writeValueAsString(response));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Exception :{}", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get language list
     */
    @RequestMapping(value = "/get-language", method = RequestMethod.GET)
    public ResponseEntity<?> getLanguage(@RequestHeader(name = "Accept-Language", required = false, defaultValue = "so") Locale locale) {
        try {
            log.info("Entry in /api/v1/admin/user/appointment/get-language");
            Object response = bookAnAppointmentService.getLanguage(locale);
            log.info("Exiting /api/v1/admin/user/appointment/get-language and it's ResponseBody is : {}", new ObjectMapper().writeValueAsString(response));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Exception :{}", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get language list
     */
    @RequestMapping(value = "/sort-by", method = RequestMethod.GET)
    public ResponseEntity<?> getSortBy(@RequestHeader(name = "Accept-Language", required = false, defaultValue = "so") Locale locale) {
        try {
            log.info("Entry in /api/v1/admin/user/appointment/sort-by");
            Object response = bookAnAppointmentService.getSortBy(locale);
            log.info("Exiting /api/v1/admin/user/appointment/sort-by and it's ResponseBody is : {}", new ObjectMapper().writeValueAsString(response));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Exception :{}", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get language list
     */
    @RequestMapping(value = "/payment-method", method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentMethod(@RequestHeader(name = "Accept-Language", required = false, defaultValue = "so") Locale locale) {
        try {
            log.info("Entry in /api/v1/admin/user/appointment/payment-method");
            Object response = bookAnAppointmentService.getPaymentMethod(locale);
            log.info("Exiting /api/v1/admin/user/appointment/payment-method and it's ResponseBody is : {}", new ObjectMapper().writeValueAsString(response));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Exception :{}", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Cancel Appointment
     */
    @RequestMapping(value = "/cancel-appointment", method = RequestMethod.POST)
    public ResponseEntity<?> cancelAppointment(@RequestBody CancelAppointmentRequest request,
                                               @RequestHeader(name = "Accept-Language", required = false, defaultValue = "so")
                                               Locale locale,
                                               @RequestHeader(name = "Authorization") String authorization) {
        try {
            log.info("Entry in /api/v1/admin/user/appointment/cancel-appointment");
            log.info("Request Body : {}", request);
            Object response = bookAnAppointmentService.cancelAppointment(request, locale);
            log.info("Exiting /api/v1/admin/user/appointment/cancel-appointment and it's ResponseBody is : {}", response);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Exception : {}", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
