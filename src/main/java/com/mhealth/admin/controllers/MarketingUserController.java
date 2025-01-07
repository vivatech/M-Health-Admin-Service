package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.request.MarketingUserRequestDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.MarketingUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Marketing User Controller", description = "APIs For Handling Marketing User Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/user/marketing")
public class MarketingUserController {

    @Autowired
    private MarketingUserService marketingUserService;

    @Autowired
    private ObjectMapper objectMapper;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> getMarketingUserList(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String contactNumber,
                                                  @RequestParam(defaultValue = "1") String sortBy,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/list");
            log.info("Request Parameters: name={}, email={}, status={}, contactNumber={}, sortBy={}, page={}, size={}", name, email, status, contactNumber, sortBy, page, size);

            Object response = marketingUserService.getMarketingUserList(locale, name, email, status,contactNumber, sortBy, page, size);

            log.info("Response Sent For /api/v1/admin/user/marketing/list: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createMarketingUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestBody MarketingUserRequestDto marketingUserRequest) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/create");
            log.info("Request Body: {}", marketingUserRequest);

            Object response = marketingUserService.createMarketingUser(locale, marketingUserRequest);

            log.info("Response Sent For /api/v1/admin/user/marketing/create: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateMarketingUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer userId,
                                                 @RequestBody MarketingUserRequestDto marketingUserRequest) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/update");
            log.info("Request Parameter: userId={}", userId);
            log.info("Request Body: {}", marketingUserRequest);

            Object response = marketingUserService.updateMarketingUser(locale, userId, marketingUserRequest);

            log.info("Response Sent For /api/v1/admin/user/marketing/update: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getMarketingUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                              @PathVariable Integer userId) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/" + userId);

            Object response = marketingUserService.getMarketingUser(locale, userId);

            log.info("Response Sent For /api/v1/admin/user/marketing/" + userId + ": {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
