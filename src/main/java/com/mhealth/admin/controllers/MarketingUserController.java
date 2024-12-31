package com.mhealth.admin.controllers;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.service.DashboardService;
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
                                                  @RequestParam(required = false) String firstName,
                                                  @RequestParam(required = false) Long userId,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String contactNumber,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Request Received For /api/v1/admin/user/marketing/list");
            log.info("Request Parameters: firstName={}, userId={}, status={}, email={}, contactNumber={}, page={}, size={}",
                    firstName, userId, status, email, contactNumber, page, size);



            log.info("Response Send For /api/v1/admin/user/marketing/list");
            return null;
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
