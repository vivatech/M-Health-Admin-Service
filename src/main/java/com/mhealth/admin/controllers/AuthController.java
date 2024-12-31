package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.request.LoginRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = "Dashboard Controller", description = "APIs for Dashboard statistics and details")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(method = "POST",description = "Login api")
    public ResponseEntity<Response> login(@RequestBody @Validated LoginRequest request,
                                          @RequestHeader(name = "X-localization",
                                                  required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return authService.login(request,locale);
    }
}
