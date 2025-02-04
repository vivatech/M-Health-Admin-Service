package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.request.MarketingUserRequestDto;
import com.mhealth.admin.service.MarketingUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
                                                  @RequestParam(required = false) String sortField,
                                                  @RequestParam(defaultValue = "1") String sortBy,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/list");
            log.info("Request Parameters: name={}, email={}, status={}, contactNumber={}, sortField={}, sortBy={}, page={}, size={}", name, email, status, contactNumber, sortField, sortBy, page, size);

            Object response = marketingUserService.getMarketingUserList(locale, name, email, status, contactNumber, sortField, sortBy, page, size);

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

    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public ResponseEntity<?> updateMarketingUserStatus(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                       @RequestParam Integer userId,
                                                       @RequestParam String status) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/update-status");
            log.info("Request Parameter: userId={}, status={}", userId, status);

            Object response = marketingUserService.updateMarketingUserStatus(locale, userId, status);

            log.info("Response Sent For /api/v1/admin/user/marketing/update-status: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteMarketingUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer userId) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/delete");
            log.info("Request Parameter: userId={}", userId);

            Object response = marketingUserService.deleteMarketingUser(locale, userId);

            log.info("Response Sent For /api/v1/admin/user/marketing/delete: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public ResponseEntity<?> getMarketingUserReport(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                    @RequestParam Integer marketingUserId,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String email,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                    @RequestParam(required = false) String contactNumber,
                                                    @RequestParam(defaultValue = "1") String sortBy,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/report");
            log.info("Request Parameters: marketingUserId={}, name={}, email={}, startDate={}, endDate={}, contactNumber={}, sortBy={}, page={}, size={}", marketingUserId, name, email, startDate, endDate, contactNumber, sortBy, page, size);

            Object response = marketingUserService.getMarketingUserReport(locale, marketingUserId, name, email, startDate, endDate, contactNumber, sortBy, page, size);

            log.info("Response Sent For /api/v1/admin/user/marketing/report: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
