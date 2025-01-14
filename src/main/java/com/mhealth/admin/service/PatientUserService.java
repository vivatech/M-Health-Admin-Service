package com.mhealth.admin.service;


import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.patientDto.PatientUserListResponseDto;
import com.mhealth.admin.dto.patientDto.PatientUserRequestDto;
import com.mhealth.admin.dto.patientDto.PatientUserResponseDto;
import com.mhealth.admin.dto.response.MarketingUserResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.exception.PatientUserExceptionHandler;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import com.mhealth.admin.sms.SMSApiService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mhealth.admin.config.Constants.*;

@Slf4j
@Service
public class PatientUserService {
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private GlobalConfigurationRepository globalConfigurationRepository;
    @Autowired
    private CountryRepository countryRepository;

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

    public Object getPatientUserList(Locale locale, String name, String email, String status, String contactNumber, String sortByEmail, String sortByContact, int page, int size) {
        Response response = new Response();
        StringBuilder baseQuery = new StringBuilder("SELECT ")
                .append("u.user_id AS userId, ")
                .append("CONCAT(u.first_name, ' ', u.last_name) AS name, ")
                .append("u.email, ")
                .append("u.country_code, ")
                .append("u.contact_number AS contactNumber, ")
                .append("u.status ")
                .append("FROM mh_users u ")
                .append("WHERE u.type = 'Patient'"); // Base query

        // Dynamically add filters
        if (!StringUtils.isEmpty(name)) {
            baseQuery.append(" AND CONCAT(u.first_name, ' ', u.last_name) LIKE :name");
        }
        if (!StringUtils.isEmpty(email)) {
            baseQuery.append(" AND u.email LIKE :email");
        }
        if (!StringUtils.isEmpty(status)) {
            baseQuery.append(" AND u.status = :status");
        }
        if (!StringUtils.isEmpty(contactNumber)) {
            baseQuery.append(" AND CONCAT(u.country_code, '', u.contact_number) LIKE :contactNumber");
        }

        baseQuery.append(" GROUP BY u.user_id, u.contact_number, u.status");

        String sortOrder = getSortOrder(sortByEmail, sortByContact);

        baseQuery.append(sortOrder);

        // Create query
        Query query = entityManager.createNativeQuery(baseQuery.toString());

        // Set parameters
        if (!StringUtils.isEmpty(name)) {
            query.setParameter("name", "%" + name + "%");
        }
        if (!StringUtils.isEmpty(email)) {
            query.setParameter("email", "%" + email + "%");
        }
        if (!StringUtils.isEmpty(status)) {
            if(!validateStatus(status)){
                response.setCode(Constants.CODE_O);
                response.setData(null);
                response.setMessage("Invalid value for status: " + status + ". Allowed values are 'A' or 'I'.");
                response.setStatus(Status.FAILED);
                return response;
            }
            query.setParameter("status", status);
        }
        if (!StringUtils.isEmpty(contactNumber)) {
            query.setParameter("contactNumber", "%" + contactNumber + "%");
        }
        if(page<=0){
            response.setCode(Constants.CODE_O);
            response.setData(null);
            response.setMessage("Invalid value for page: " + page + ". Allowed values should be greater or equal to 1.");
            response.setStatus(Status.FAILED);
            return response;
        }

        // Pagination
        Pageable pageable = PageRequest.of(page - 1, size);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Fetch results
        List<Object[]> results = query.getResultList();

        // Map results to DTO
        List<PatientUserListResponseDto> responseList = mapResultsToPatientUserListResponseDto(results);

        // Total count query
        String countQuery = "SELECT COUNT(*) FROM (" + baseQuery + ") AS countQuery";
        Query countQ = entityManager.createNativeQuery(countQuery);

        // Set parameters for count query
        if (!StringUtils.isEmpty(name)) {
            countQ.setParameter("name", "%" + name + "%");
        }
        if (!StringUtils.isEmpty(email)) {
            countQ.setParameter("email", "%" + email + "%");
        }
        if (!StringUtils.isEmpty(status)) {
            countQ.setParameter("status", status);
        }
        if (!StringUtils.isEmpty(contactNumber)) {
            countQ.setParameter("contactNumber", "%" + contactNumber + "%");
        }

        long totalCount = ((Number) countQ.getSingleResult()).longValue();

        // Create pageable response
        Page<PatientUserListResponseDto> pageableResponse = new PageImpl<>(responseList, pageable, totalCount);

        // Build response
        Map<String, Object> data = new HashMap<>();
        data.put("userList", pageableResponse.getContent());
        data.put("totalCount", pageableResponse.getTotalElements());

        response.setCode(Constants.CODE_1);
        response.setData(data);
        response.setMessage(messageSource.getMessage(Messages.USER_LIST_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    private String getSortOrder(String sortByEmail, String sortByContact) {
        String sortOrder = " ORDER BY u.user_id DESC";
        // Determine sorting based on sortBy
        if(!StringUtils.isEmpty(sortByEmail)){
            sortOrder = " ORDER BY u.email ";
            if ("0".equals(sortByEmail)) {
                sortOrder += "ASC"; // Ascending order
            } else {
                sortOrder += "DESC"; // Default to descending order
            }
        }
        if(!StringUtils.isEmpty(sortByContact)){
            sortOrder = " ORDER BY u.contact_number ";
            if ("0".equals(sortByContact)) {
                sortOrder += "ASC"; // Ascending order
            } else {
                sortOrder += "DESC"; // Default to descending order
            }
        }
        return sortOrder;
    }

    private boolean validateStatus(String status) {
        for (StatusAI s : StatusAI.values()) {
            if (s.name().equals(status)) {
                return true;
            }
        }
        return false;
    }

    private List<PatientUserListResponseDto> mapResultsToPatientUserListResponseDto(List<Object[]> results) {
        return results.stream().map(row -> {
            Integer userId = (Integer) row[0];
            String name = StringUtils.isEmpty((String) row[1]) ? "" : (String) row[1];
            String email = StringUtils.isEmpty((String) row[2]) ? "" : (String) row[2];
            String countryCode = StringUtils.isEmpty((String) row[3]) ? "" : (String) row[3];
            String contactNumber = (String) row[4];
            String status = (String) row[5];

            return new PatientUserListResponseDto(
                    userId, name.trim(), email, countryCode + "-" + contactNumber, status
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public Object createPatientUser(Locale locale, PatientUserRequestDto requestDto) {
        Response response = new Response();

        // Validate the input
        String validationMessage = requestDto.validate();
        if (validationMessage != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(validationMessage);
            response.setStatus(Status.FAILED);
            return response;
        }
        //validation for terms and condition
        if(requestDto.getTermsAndConditionChecked() != null && !requestDto.getTermsAndConditionChecked()){
            response.setCode(Constants.CODE_O);
            response.setMessage("Terms and Condition should be checked");
            response.setStatus(Status.FAILED);
            return response;
        }

        // Check for duplicate email and contact number
        long emailCount = usersRepository.countByEmail(requestDto.getEmail());
        long contactNumberCount = usersRepository.countByContactNumberAndType(requestDto.getContactNumber(), UserType.Patient);

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

        //profile picture
        String pp = null;
        if (requestDto.getProfilePicture() != null && !requestDto.getProfilePicture().getName().isEmpty()) {
            String fileName = requestDto.getProfilePicture().getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            if (!Arrays.asList("jpg", "jpeg", "png").contains(fileExtension)) {
                response.setCode(Constants.CODE_O);
                response.setMessage(messageSource.getMessage(Messages.PROFILE_PICTURE_NOT_SELECTED, null, locale));
                response.setStatus(Status.SUCCESS);
                return response;
            }
            pp = requestDto.getProfilePicture().getOriginalFilename();
            //TODO :Store the file into directory
        }



        // Create Patient user
        Users patientUser = getUsers(requestDto, pp, locale);

        // Send SMS
        try {
            locale = Utility.getUserNotificationLanguageLocale(patientUser.getNotificationLanguage(), locale);
            GlobalConfiguration value = globalConfigurationRepository.findByKey(Messages.APP_LINK).orElseThrow(
                    ()-> new PatientUserExceptionHandler(KEY_NOT_FOUND)
            );
            String smsMessage = messageSource.getMessage(Messages.REGISTER_PATIENT_USER, new Object[]{patientUser.getFirstName() + " " + patientUser.getLastName(), value.getValue()}, locale);
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

    private Users getUsers(PatientUserRequestDto requestDto, String pp, Locale locale) {
        //first name and last name
        String[] fullName = requestDto.getFullName().trim().split(" ");
        String lastName = "";
        StringBuilder sb = new StringBuilder();
        if(fullName.length > 1){
            for(int i = 1 ; i < fullName.length ; i++){
                sb.append(fullName[i]).append(" ");
            }
            lastName = sb.toString();
        }

        Country c = countryRepository.findById(requestDto.getCountryId()).orElseThrow(
                ()-> new PatientUserExceptionHandler(messageSource.getMessage(Messages.COUNTRY_NOT_FOUND, null, locale)));
        Users patientUser = new Users();
        patientUser.setType(UserType.Patient);
        patientUser.setFirstName(fullName[0]);
        patientUser.setLastName(lastName);
        patientUser.setEmail(requestDto.getEmail());
        patientUser.setProfilePicture(pp);
        patientUser.setCountry(c);
        patientUser.setState(requestDto.getProvinceId());
        patientUser.setCity(requestDto.getCityId());
        patientUser.setContactNumber(requestDto.getContactNumber());
        patientUser.setCountryCode(countryCode);
        patientUser.setGender(requestDto.getGender());
        patientUser.setDob(requestDto.getDob());
        patientUser.setStatus(StatusAI.A);
        patientUser.setIsInternational(YesNo.No);
        patientUser.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        patientUser.setApprovedBy(0);
        patientUser.setIsHpczVerified(YesNo.Yes.name());
        patientUser.setIsHospitalVerified(YesNo.Yes.name());
        patientUser.setIsSuspended(0);
        patientUser.setAttemptCounter((short)0);
        patientUser.setOtpCounter(0);
        patientUser.setHospitalId(0);
        patientUser.setNotificationLanguage(!StringUtils.isEmpty(requestDto.getNotificationLanguage())
                ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);
        return usersRepository.save(patientUser);
    }

    @Transactional
    public Object updatePatientUser(Locale locale, Integer userId, PatientUserRequestDto requestDto) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingPatientUser = usersRepository.findByUserIdAndType(userId, UserType.Patient);
        if (existingPatientUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingPatientUser.get();

        // Validate the input
        String validationMessage = requestDto.validate();
        if (validationMessage != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(validationMessage);
            response.setStatus(Status.FAILED);
            return response;
        }
        //profile picture
        String pp = null;
        if (requestDto.getProfilePicture() != null && !requestDto.getProfilePicture().getName().isEmpty()) {
            String fileName = requestDto.getProfilePicture().getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            if (!Arrays.asList("jpg", "jpeg", "png").contains(fileExtension)) {
                response.setCode(Constants.CODE_O);
                response.setMessage(messageSource.getMessage(Messages.PROFILE_PICTURE_NOT_SELECTED, null, locale));
                response.setStatus(Status.SUCCESS);
                return response;
            }
            pp = requestDto.getProfilePicture().getOriginalFilename();
            //TODO :Store the file into directory
        }

        // Check for duplicate email and contact number
        long emailCount = usersRepository.countByEmailAndUserIdNot(requestDto.getEmail(), userId);
        long contactNumberCount = usersRepository.countByContactNumberAndTypeAndUserIdNot(requestDto.getContactNumber(), UserType.Patient, userId);

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

        //first name and last name
        String[] fullName = requestDto.getFullName().trim().split(" ");
        String lastName = "";
        StringBuilder sb = new StringBuilder();
        if(fullName.length > 1){
            for(int i = 1 ; i < fullName.length ; i++){
                sb.append(fullName[i]).append(" ");
            }
            lastName = sb.toString().trim();
        }

        // Update the user fields
        Country c = countryRepository.findById(requestDto.getCountryId()).orElseThrow(
                ()-> new PatientUserExceptionHandler(messageSource.getMessage(Messages.COUNTRY_NOT_FOUND, null, locale)));

        existingUser.setFirstName(fullName[0]);
        existingUser.setLastName(lastName);
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setContactNumber(requestDto.getContactNumber());
        existingUser.setCountry(c);
        existingUser.setState(requestDto.getProvinceId());
        existingUser.setCity(requestDto.getCityId());
        existingUser.setGender(!StringUtils.isEmpty(requestDto.getGender()) ? requestDto.getGender() : existingUser.getGender());
        existingUser.setResidenceAddress(requestDto.getResidentialAddress());
        existingUser.setProfilePicture(pp);
        existingUser.setDob(requestDto.getDob());

        existingUser.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);

        usersRepository.save(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_UPDATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object updatePatientUserStatus(Locale locale, Integer userId, String status) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingPatientUser = usersRepository.findByUserIdAndType(userId, UserType.Patient);
        if (existingPatientUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingPatientUser.get();

        //check the status filed
        if(!validateStatus(status)){
            response.setCode(Constants.CODE_O);
            response.setMessage("Invalid value for updating status: " + status + ". Allowed values are 'A' or 'I'.");
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

    public Object getPatientUser(Locale locale, Integer userId) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingPatientUser = usersRepository.findByUserIdAndType(userId, UserType.Patient);
        if (existingPatientUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingPatientUser.get();

        // Construct users entity to patient user response dto
        PatientUserResponseDto patientUserResponseDto = convertToPatientUserResponseDto(existingUser, locale);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setData(patientUserResponseDto);
        response.setMessage(messageSource.getMessage(Messages.USER_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    private PatientUserResponseDto convertToPatientUserResponseDto(Users user, Locale locale) {
        PatientUserResponseDto dto = new PatientUserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setCountryCode(user.getCountryCode());
        dto.setContactNumber(user.getContactNumber());
        dto.setCountryId(user.getCountry() == null ? 0 : user.getCountry().getId());
        dto.setCountryName(user.getCountry() == null ? "" : user.getCountry().getName());

        //state
        if(user.getState() != null && user.getState() != 0){
            State state = stateRepository.findById(user.getState())
                    .orElseThrow(()-> new PatientUserExceptionHandler(messageSource.getMessage(Constants.NO_STATE_FOUND, null, locale)));
            dto.setStateId(state.getId());
            dto.setStateName(state.getName());
        }
        //city
        if(user.getCity() != null && user.getCity() != 0){
            City city = cityRepository.findById(user.getCity()).orElse(null);
            if(city != null) {
                dto.setCityId(city.getId());
                dto.setCityName(city.getName());
            }
        }
        dto.setDob(user.getDob());
        dto.setResidentialAddress(user.getResidenceAddress());
        dto.setNotificationLanguage(user.getNotificationLanguage());

        return dto;
    }
}
