package com.mhealth.admin.service;


import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.Classification;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.request.AgentUserRequestDto;
import com.mhealth.admin.dto.response.AgentUserResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.*;
import com.mhealth.admin.sms.SMSApiService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class AgentUserService {

    @Value("${m-health.country.code}")
    private String countryCode;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Utility utility;

    public Object getAgentList(Locale locale, String name, String email, StatusAI status, String contactNumber, Integer sortBy, String sortField, int page, int size) {
        // Validate sortField
        List<String> validSortFields = Arrays.asList("name", "contactNumber", "email");
        if (!validSortFields.contains(sortField)) {
            sortField = null; // Set to null if the value is invalid
        }

        // Determine sorting direction
        Sort.Direction direct = sortBy.equals(0) ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Handle custom sorting for "name" (firstName + lastName)
        Sort sort;
        if ("name".equals(sortField)) {
            sort = Sort.by(direct, "firstName").and(Sort.by(direct, "lastName"));
        } else {
            sortField = (sortField != null) ? sortField : "userId"; // Default sorting field
            sort = Sort.by(direct, sortField);
        }

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch paginated results
        Page<Users> userPage = usersRepository.findAgentWithFilters(
                name != null ? "%" + name + "%" : null,
                email != null ? "%" + email + "%" : null,
                status != null ? status : null,
                contactNumber != null ? "%" + contactNumber + "%" : null,
                pageable
        );

        // Map to DTO
        Page<AgentUserResponseDto> responsePage = userPage.map(user -> {
            AgentUserResponseDto dto = new AgentUserResponseDto();
            dto.setUserId(user.getUserId());
            dto.setName(user.getFirstName() + " " + user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setContactNumber(user.getCountryCode() + user.getContactNumber());
            dto.setNotificationLanguage(user.getNotificationLanguage());
            dto.setStatus(user.getStatus().toString());
            return dto;
        });

        // Build response
        Map<String, Object> data = new HashMap<>();
        data.put("data", responsePage.getContent());
        data.put("totalCount", responsePage.getTotalElements());

        Response response = new Response();
        response.setCode(Constants.CODE_1);
        response.setData(data);
        response.setMessage(messageSource.getMessage(Messages.USER_LIST_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    @Transactional
    public Object createAgentUser(Locale locale, AgentUserRequestDto requestDto, HttpServletRequest request) throws Exception {
        Response response = new Response();

        // Validate the input
        String validationMessage = requestDto.validate();
        if (validationMessage != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(validationMessage);
            response.setStatus(Status.FAILED);
            return response;
        }

        // Check for duplicate email and contact number
        long emailCount = usersRepository.countByEmail(requestDto.getEmail());
        long contactNumberCount = usersRepository.countByContactNumberAndType(requestDto.getContactNumber(), UserType.Agentuser);

        if (emailCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.EMAIL_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        } else if (contactNumberCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.CONTACT_NUMBER_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Create agent
        Users user = new Users();
        user.setType(UserType.Agentuser);
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        user.setContactNumber(requestDto.getContactNumber());
        user.setCountryCode(requestDto.getCountryCode() !=  null ? requestDto.getCountryCode() : countryCode);
        user.setIsInternational(YesNo.No);
        user.setStatus(StatusAI.I);
        user.setClassification(Classification.from_hospital);
        user.setDoctorClassification(Classification.general_practitioner.toString());
        user.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);

        String encodedPassword = utility.md5Hash(requestDto.getPassword());
        user.setPassword(encodedPassword);

        usersRepository.save(user);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_CREATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    @Transactional
    public Object updateAgentUser(Locale locale, Integer userId, AgentUserRequestDto requestDto) throws Exception {
        Response response = new Response();

        // Find the user
        Optional<Users> existUser = usersRepository.findByUserIdAndType(userId, UserType.Agentuser);
        if (existUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existUser.get();

        // Validate the input
        String validationMessage = requestDto.validate();
        if (validationMessage != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(validationMessage);
            response.setStatus(Status.FAILED);
            return response;
        }

        // Check for duplicate email and contact number
        long emailCount = usersRepository.countByEmailAndUserIdNot(requestDto.getEmail(), userId);
        long contactNumberCount = usersRepository.countByContactNumberAndTypeAndUserIdNot(requestDto.getContactNumber(), UserType.Agentuser, userId);

        if (emailCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.EMAIL_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        } else if (contactNumberCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.CONTACT_NUMBER_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Update the user fields
        existingUser.setFirstName(requestDto.getFirstName());
        existingUser.setLastName(requestDto.getLastName());
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setContactNumber(requestDto.getContactNumber());
        existingUser.setCountryCode(requestDto.getCountryCode() != null ? requestDto.getCountryCode() : countryCode);
        existingUser.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);

        usersRepository.save(existingUser);

        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_UPDATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object updateAgentUserStatus(Locale locale, Integer userId, String status) {
        Response response = new Response();

        // Find the user
        Optional<Users> user = usersRepository.findByUserIdAndType(userId, UserType.Agentuser);
        if (user.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = user.get();

        // Validate the status
        if (!validateStatus(status)) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.INCORRECT_USER_STATUS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Update the user's status field
        existingUser.setStatus(StatusAI.valueOf(status));

        usersRepository.save(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_UPDATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    private boolean validateStatus(String status) {
        boolean containsStatus = false;
        for (StatusAI statusAI : StatusAI.values()) {
            if (statusAI.name().equals(status)) {
                containsStatus = true;
                break;
            }
        }
        return containsStatus;
    }

    @Transactional
    public Object deleteAgentUser(Locale locale, Integer id) throws Exception {
        Response response = new Response();

        // Find the user
        Optional<Users> existingAgentUser = usersRepository.findByUserIdAndType(id, UserType.Agentuser);
        if (existingAgentUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingAgentUser.get();

        usersRepository.delete(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_DELETED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object getAgentUserById(Locale locale, Integer userId) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingAgentUser = usersRepository.findByUserIdAndType(userId, UserType.Agentuser);
        if (existingAgentUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingAgentUser.get();

        AgentUserResponseDto responseDto = convertToResponseDto(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setData(responseDto);
        response.setMessage(messageSource.getMessage(Messages.USER_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;

    }

    private AgentUserResponseDto convertToResponseDto(Users users) {
        AgentUserResponseDto responseDto = new AgentUserResponseDto();
        responseDto.setUserId(users.getUserId());
        responseDto.setName(users.getFirstName() + " " + users.getLastName());
        responseDto.setEmail(users.getEmail());
        responseDto.setContactNumber(users.getContactNumber());
        responseDto.setNotificationLanguage(users.getNotificationLanguage());
        responseDto.setStatus(users.getStatus().toString());
        return responseDto;
    }

}
