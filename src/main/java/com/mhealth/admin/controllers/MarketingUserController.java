package com.mhealth.admin.controllers;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.request.MarketingUserRequestDto;
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


    @GetMapping("/list")
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

            log.info("Response Sent For /api/v1/admin/user/marketing/list: {}", response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMarketingUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestBody MarketingUserRequestDto marketingUserRequest) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/create");
            log.info("Request Body: {}", marketingUserRequest);

            Object response = marketingUserService.createMarketingUser(locale, marketingUserRequest);

            log.info("Response Sent For /api/v1/admin/user/marketing/create: {}", response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateMarketingUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                 @RequestParam Integer userId,
                                                 @RequestBody MarketingUserRequestDto marketingUserRequest) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/update");
            log.info("Request Parameter: userId={}", userId);
            log.info("Request Body: {}", marketingUserRequest);

            Object response = marketingUserService.updateMarketingUser(locale, userId, marketingUserRequest);

            log.info("Response Sent For /api/v1/admin/user/marketing/update: {}", response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
