package com.mhealth.admin.service;


import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.Classification;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.request.HospitalManagementRequestDto;
import com.mhealth.admin.dto.response.*;
import com.mhealth.admin.model.HospitalDetails;
import com.mhealth.admin.model.HospitalMerchantNumber;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.*;
import com.mhealth.admin.sms.SMSApiService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HospitalManagementService {

    @Value("${m-health.country.code}")
    private String countryCode;

    @Value("${m-health.country}")
    private String country;

    @Value("${app.sms.sent}")
    private boolean smsSent;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersPromoCodeRepository usersPromoCodeRepository;

    @Autowired
    private AuthAssignmentRepository authAssignmentRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SMSApiService smsApiService;

    @Autowired
    private HospitalDetailsRepository hospitalDetailsRepository;

    @Autowired
    private HospitalMerchantNumberRepository hospitalMerchantNumberRepository;

    @Autowired
    private Utility utility;

    public Object getHospitalList(Locale locale, String name, String email, String status, String contactNumber, String sortBy, int page, int size) {

        // Prepare Pageable
        Pageable pageable = PageRequest.of(page, size);

        // Build filter conditions
        List<String> filters = new ArrayList<>();
        if (name != null && !name.isEmpty()) filters.add("clinic_name LIKE %:name%");
        if (email != null && !email.isEmpty()) filters.add("email LIKE %:email%");
        if (status != null && !status.isEmpty()) filters.add("status = :status");
        if (contactNumber != null && !contactNumber.isEmpty()) filters.add("contact_number LIKE %:contactNumber%");

        // Generate the WHERE clause based on filters
        String whereClause = String.join(" AND ", filters);

        // Construct query dynamically
        String query = "SELECT u FROM Users u WHERE u.type = 'Clinic' ";
        if (!filters.isEmpty()) {
            query += "AND (" + whereClause + ")";
        }

        // Apply sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            query += " ORDER BY u." + sortBy;
        }

        // Execute the query with pagination
        Page<Users> userPage = usersRepository.findUsersWithFilters(query, pageable, name, email, status, contactNumber);

        // Map results to DTO
        List<HospitalManagementResponseDto> responseList = userPage.getContent().stream()
                .map(user -> {
                    HospitalManagementResponseDto dto = new HospitalManagementResponseDto();
                    dto.setUserId(user.getUserId());
                    dto.setClinicName(user.getClinicName());
                    dto.setEmail(user.getEmail());
                    dto.setContactNumber(user.getContactNumber());
                    dto.setNotificationContactNumber(getNotificationContactNumber(user));
                    dto.setMerchantNumber(getMerchantNumber(user));
                    dto.setPriority(user.getSort());
                    dto.setClinicAddress(user.getHospitalAddress());
                    dto.setNotificationLanguage(user.getNotificationLanguage());
                    dto.setStatus(Status.valueOf(user.getStatus().name()));  // Assuming Status is an Enum
                    return dto;
                })
                .collect(Collectors.toList());

        // Create pageable response
        Page<HospitalManagementResponseDto> pageableResponse = new PageImpl<>(responseList, pageable, userPage.getTotalElements());

        // Build response
        Map<String, Object> data = new HashMap<>();
        data.put("data", pageableResponse.getContent());
        data.put("totalCount", pageableResponse.getTotalElements());

        Response response = new Response();
        response.setCode(Constants.CODE_1);
        response.setData(data);
        response.setMessage(messageSource.getMessage(Messages.USER_LIST_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    private String getNotificationContactNumber(Users user) {
        Optional<HospitalDetails> details = hospitalDetailsRepository.findByUserId(user.getUserId());
        return details.map(HospitalDetails::getNotificationContactNumber).orElse("");
    }

    private String getMerchantNumber(Users user) {
        Optional<HospitalMerchantNumber> merchantNumber = hospitalMerchantNumberRepository.findByUserId(user.getUserId());
        return merchantNumber.map(HospitalMerchantNumber::getMerchantNumber).orElse("");
    }

    @Transactional
    public Object createHospitalManagement(Locale locale, HospitalManagementRequestDto requestDto) {
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
        long contactNumberCount = usersRepository.countByContactNumberAndType(requestDto.getContactNumber(), UserType.Clinic);

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


        // Create hospital
        Users user = new Users();
        user.setType(UserType.Clinic);
        user.setClinicName(requestDto.getClinicName());
        user.setEmail(requestDto.getEmail());
        user.setContactNumber(requestDto.getContactNumber());
        user.setHospitalAddress(requestDto.getClinicAddress());
        user.setCountryCode(countryCode);
        user.setIsInternational(YesNo.No);
        user.setStatus(StatusAI.I);
        user.setClassification(Classification.from_hospital);
        String encodedPassword = utility.md5Hash(requestDto.getPassword());
        user.setPassword(encodedPassword);
        user.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);

        if(requestDto.getPriority() != null){
            Users existSort = usersRepository.findBySort(requestDto.getPriority());
            if(existSort != null){
                response.setCode(Constants.CODE_O);
                response.setMessage(messageSource.getMessage(Messages.PRIORITY_ALREADY_EXISTS, null, locale));
                response.setStatus(Status.FAILED);
                return response;
            }
            user.setSort(requestDto.getPriority());
        }
        user = usersRepository.save(user);

        HospitalMerchantNumber hospitalMerchantNumber = new HospitalMerchantNumber();
        hospitalMerchantNumber.setMerchantNumber(requestDto.getMerchantNumber());
        hospitalMerchantNumber.setUserId(user.getUserId());
        hospitalMerchantNumberRepository.save(hospitalMerchantNumber);

        HospitalDetails hospitalDetails = new HospitalDetails();
        hospitalDetails.setUserId(user.getUserId());
        hospitalDetails.setNotificationContactNumber(requestDto.getNotificationContactNumber());
        hospitalDetailsRepository.save(hospitalDetails);

        // Send SMS
        try {
            locale = Utility.getUserNotificationLanguageLocale(user.getNotificationLanguage(), locale);

            String link = messageSource.getMessage(Messages.HOSPITAL_LINK, new Object[]{"Base URL"}, locale);

            String smsMessage = messageSource.getMessage(Messages.HOSPITAL_REGISTRATION_CONFIRMATION, new Object[]{
                    user.getClinicName(), link, user.getContactNumber(), user.getPassword()}, locale);
            String smsNumber = "+" + countryCode + requestDto.getContactNumber();
            if(smsSent){
                smsApiService.sendMessage(smsNumber, smsMessage, country);
            }
        } catch (Exception ex) {
            log.error("exception occurred while sending the sms", ex);
        }

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_CREATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    @Transactional
    public Object updateHospitalManagement(Locale locale, Integer userId, HospitalManagementRequestDto requestDto) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingMarketingUser = usersRepository.findByUserIdAndType(userId, UserType.Clinic);
        if (existingMarketingUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingMarketingUser.get();

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
        long contactNumberCount = usersRepository.countByContactNumberAndTypeAndUserIdNot(requestDto.getContactNumber(), UserType.Clinic, userId);

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
        existingUser.setClinicName(requestDto.getClinicName());
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setContactNumber(requestDto.getContactNumber());
        existingUser.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);

        if(requestDto.getPriority() != null){
            if(!Objects.equals(existingUser.getSort(), requestDto.getPriority())){
                Users existSort = usersRepository.findBySort(requestDto.getPriority());
                if(existSort != null){
                    response.setCode(Constants.CODE_O);
                    response.setMessage(messageSource.getMessage(Messages.PRIORITY_ALREADY_EXISTS, null, locale));
                    response.setStatus(Status.FAILED);
                    return response;
                }
            }
            existingUser.setSort(requestDto.getPriority());
        }
        usersRepository.save(existingUser);

        Optional<HospitalDetails> hospitalDetails = hospitalDetailsRepository.findByUserId(existingUser.getUserId());
        if (hospitalDetails.isPresent()) {
            HospitalDetails existingHospitalDetails = hospitalDetails.get();
            existingHospitalDetails.setNotificationContactNumber(requestDto.getNotificationContactNumber());
            hospitalDetailsRepository.save(existingHospitalDetails);
        }

        Optional<HospitalMerchantNumber> hospitalMerchantNumber = hospitalMerchantNumberRepository.findByUserId(existingUser.getUserId());
        if (hospitalMerchantNumber.isPresent()) {
            HospitalMerchantNumber existingHospitalMerchantNumber = hospitalMerchantNumber.get();
            existingHospitalMerchantNumber.setMerchantNumber(requestDto.getMerchantNumber());
            hospitalMerchantNumberRepository.save(existingHospitalMerchantNumber);
        }

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_UPDATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object updateHospitalManagementStatus(Locale locale, Integer userId, String status) {
        Response response = new Response();

        // Find the user
        Optional<Users> user = usersRepository.findByUserIdAndType(userId, UserType.Clinic);
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
    public Object deleteHospitalManagement(Locale locale, Integer id) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingMarketingUser = usersRepository.findByUserIdAndType(id, UserType.Clinic);
        if (existingMarketingUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingMarketingUser.get();

        hospitalMerchantNumberRepository.deleteByUserId(existingMarketingUser.get().getUserId());

        hospitalDetailsRepository.deleteByUserId(existingMarketingUser.get().getUserId());

        usersRepository.delete(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_DELETED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }
}
