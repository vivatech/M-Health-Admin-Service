package com.mhealth.admin.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.service.GlobalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Global Controller", description = "APIs For Perform Global Operation")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/global/")
public class GlobalController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GlobalService globalService;


    @RequestMapping(value = "/get-cities", method = RequestMethod.GET)
    public ResponseEntity<?> getCities(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                       @RequestParam List<Integer> stateIdList) {
        try {
            log.info("Request Received For /api/v1/admin/global/get-cities");
            log.info("Request Parameters: stateIdList={}", stateIdList);

            Object response = globalService.getCities(locale, stateIdList);

            log.info("Response Sent For /api/v1/admin/global/get-cities: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get-countries", method = RequestMethod.GET)
    public ResponseEntity<?> getCountries(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                          @RequestParam List<Integer> countryCodeList) {
        try {
            log.info("Request Received For /api/v1/admin/global/get-countries");
            log.info("Request Parameters: countryCodeList={}", countryCodeList);

            Object response = globalService.getCountries(locale, countryCodeList);

            log.info("Response Sent For /api/v1/admin/global/get-countries: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get-languages", method = RequestMethod.GET)
    public ResponseEntity<?> getLanguageList(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        try {
            log.info("Request Received For /api/v1/admin/global/get-languages");

            Object response = globalService.getLanguageList(locale);

            log.info("Response Sent For /api/v1/admin/global/get-languages: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get-provinces", method = RequestMethod.GET)
    public ResponseEntity<?> getProvinceList(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                             @RequestParam List<Integer> countryIdList) {
        try {
            log.info("Request Received For /api/v1/admin/global/get-provinces");
            log.info("Request Parameters: countryIdList={}", countryIdList);

            Object response = globalService.getProvinceList(locale, countryIdList);

            log.info("Response Sent For /api/v1/admin/global/get-provinces: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get-hospitals", method = RequestMethod.GET)
    public ResponseEntity<?> getHospitalList(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        try {
            log.info("Request Received For /api/v1/admin/global/get-hospitals");

            Object response = globalService.getHospitalList(locale);

            log.info("Response Sent For /api/v1/admin/global/get-hospitals: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete user profile picture user
     */
    @RequestMapping(value = "/delete-profile-pic", method = RequestMethod.POST)
    public ResponseEntity<?> deleteProfilePicture(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                  @RequestParam Integer userId) {
        try {
            log.info("Request Received For /api/v1/admin/global/delete-profile-pic" + userId);

            Object response = globalService.deleteProfilePicture(locale, userId);

            log.info("Response Sent For /api/v1/admin/global/delete-profile-pic" + userId + ": {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception found in /api/v1/admin/global/delete-profile-pic : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
