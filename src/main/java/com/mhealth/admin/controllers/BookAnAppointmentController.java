package com.mhealth.admin.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.BaseResponseDto;
import com.mhealth.admin.dto.dto.SearchDocResponse;
import com.mhealth.admin.dto.dto.SearchDoctorRequest;
import com.mhealth.admin.service.BookAnAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Search list of doctors from given city, clinic id, list of specialization in the form of id's
     * Search list of doctors based upon languages he/she knows
     *
     * @param r
     * @return list of doctors in pagination form
     */
    @RequestMapping(value = "/search-doctor", method = RequestMethod.POST)
    public ResponseEntity<?> searchDoctor(@RequestBody SearchDoctorRequest r,
                                          @RequestHeader(name = "Accept-Language", required = false, defaultValue = "so")
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

            ResponseEntity<List<SearchDocResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<SearchDocResponse>>() {}
            );
            List<SearchDocResponse> doctorList = response.getBody();
//            SearchDocResponse responseDto = new ObjectMapper().readValue((DataInput) response.getBody(), SearchDocResponse.class);
            log.info("Exiting /api/v1/admin/user/appointment and it's ResponseBody is : {}", response.getBody());
            return ResponseEntity.ok().body(doctorList);

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
                                               @RequestHeader(name = "Accept-Language", required = false, defaultValue = "so")
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
}
