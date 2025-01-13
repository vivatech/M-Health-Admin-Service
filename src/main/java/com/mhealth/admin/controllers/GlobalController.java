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
@Tag(name = "Location Controller", description = "APIs For Getting LOcation Details")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/global/")
public class GlobalController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GlobalService globalService;


    @RequestMapping(value = "/get-cities", method = RequestMethod.GET)
    public ResponseEntity<?> getCities(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        try {
            log.info("Request Received For /api/v1/admin/global/get-cities");

            Object response = globalService.getCities(locale);

            log.info("Response Sent For /api/v1/admin/global/get-cities: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get-countries", method = RequestMethod.GET)
    public ResponseEntity<?> getCountries(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                  @RequestParam(required = false) List<Integer> countryIdList,
                                                  @RequestParam(required = false) List<Integer> phoneCodeList) {
        try {
            log.info("Request Received For /api/v1/admin/global/get-countries");
            log.info("Request Parameters: countryIdList={}, phoneCodeList={}", countryIdList, phoneCodeList);

            Object response = globalService.getCountries(locale, countryIdList, phoneCodeList);

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
}
