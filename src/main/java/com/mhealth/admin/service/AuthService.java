package com.mhealth.admin.service;

import com.mhealth.admin.config.AuthConfig;
import com.mhealth.admin.config.Constants;
import com.mhealth.admin.config.Utility;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.LoginResponseDto;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.request.LoginRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.AuthKey;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.AuthKeyRepository;
import com.mhealth.admin.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthKeyRepository authKeyRepository;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Utility utility;

    public ResponseEntity<Response> login(LoginRequest request, Locale locale) {
        Response responseDto = new Response();
        String message;
        String statusCode = Constants.SUCCESS_CODE;
        Status status = Status.FAILED;
        LoginResponseDto data = null; // Using DTO instead of Map

        // Fetch user by contact number and type
        Users superAdmin = usersRepository.findByContactNumberAndType(request.getContactNumber(), UserType.Superadmin);

        if (superAdmin == null || !superAdmin.getCountryCode().equalsIgnoreCase(request.getCountryCode())) {
            // User not found or country code mismatch
            message = messageSource.getMessage(Constants.USER_NOT_FOUND, null, locale);
            responseDto.setStatus(Status.FAILED);
            responseDto.setMessage(message);
            responseDto.setCode(Constants.NO_RECORD_FOUND_CODE);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto); // 401 Unauthorized
        }

        if (!utility.md5Hash(request.getPassword()).equals(superAdmin.getPassword())) {
            // Password mismatch
            message = messageSource.getMessage(Constants.INVALID_PASSWORD, null, locale);
            responseDto.setStatus(Status.FAILED);
            responseDto.setMessage(message);
            responseDto.setCode(Constants.INVALID_PASSWORD_CODE); // Use a specific code for invalid password
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto); // 401 Unauthorized
        }

        // Successful login
        String token = authConfig.generateToken(request.getContactNumber(), superAdmin.getUserId());
        boolean isInternational = superAdmin.getIsInternational().equals(YesNo.Yes);

        // Populate the DTO
        data = new LoginResponseDto(
                superAdmin.getUserId().toString(),
                token,
                isInternational
        );

        message = messageSource.getMessage(Constants.USER_LOGIN_IS_SUCCESS, null, locale);
        statusCode = Constants.SUCCESS_CODE;
        status = Status.SUCCESS;

        saveNewSession(superAdmin.getUserId(), token, UUID.randomUUID().toString(), UserType.Doctor);

        responseDto.setStatus(status);
        responseDto.setMessage(message);
        responseDto.setCode(statusCode);
        responseDto.setData(data);

        return ResponseEntity.ok(responseDto);
    }

    public boolean isSessionValid(String username, String authKey) {
        Users user = usersRepository.findByContactNumber(username).orElse(null);
        if(user!=null){
            Optional<AuthKey> session = authKeyRepository.findByUserIdAndLoginType(user.getUserId(), UserType.Patient);
            return session.isPresent() && session.get().getAuthKey().equals(authKey);
        }
        return false;
    }

    public AuthKey saveNewSession(Integer userId, String authKey, String deviceToken, UserType loginType) {
        // Invalidate any existing session for the user and login type
        invalidateOldSessions(userId, loginType);

        // Create and save the new session
        AuthKey newSession = new AuthKey();
        newSession.setUserId(userId);
        newSession.setAuthKey(authKey);
        newSession.setDeviceToken(deviceToken); // Can be null for web sessions
        newSession.setLoginType(loginType);
        newSession.setCreatedDate(new Date());

        return authKeyRepository.save(newSession);
    }

    public boolean invalidateOldSessions(Integer userId, UserType loginType) {
        List<AuthKey> existingSessions = authKeyRepository.findAllByUserIdAndLoginType(userId, loginType);

        if (!existingSessions.isEmpty()) {
            authKeyRepository.deleteAll(existingSessions);
            return true;
        }

        return false;
    }
}
