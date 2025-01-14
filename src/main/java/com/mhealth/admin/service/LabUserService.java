package com.mhealth.admin.service;


import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.enums.Classification;
import com.mhealth.admin.dto.labUserDto.LabUserListResponseDto;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.labUserDto.LabUserRequestDto;
import com.mhealth.admin.dto.labUserDto.LabUserResponseDto;
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

import static com.mhealth.admin.constants.Messages.GENERAL_PRACTITIONER;

@Slf4j
@Service
public class LabUserService {
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
    private MessageSource messageSource;

    @Autowired
    private SMSApiService smsApiService;
    @Autowired
    private Utility utility;

    public static final List<String> sortByValues = List.of("user_id", "contact_number", "clinic_name", "first_name", "professional_identification_number");

    public Object getLabUserList(
            Locale locale,
            String fullName,
            String labName,
            String labRegistrationNumber,
            String contactNumber, String status,
            Integer cityId,
            String sortField,
            String sortBy,
            int page,
            int size) {
        Response response = new Response();
        if(!sortByValues.contains(sortField)){
            response.setCode(Constants.CODE_O);
            response.setData(null);
            response.setMessage("Invalid value for sortField: "
                    + sortField + ". Allowed values are " + sortByValues);
            response.setStatus(Status.FAILED);
            return response;
        }
        StringBuilder baseQuery = new StringBuilder("SELECT ")
                .append("u.user_id AS userId, ")
                .append("u.clinic_name AS labName, ")
                .append("CONCAT(u.first_name, ' ', u.last_name) AS fullName, ")
                .append("u.professional_identification_number AS labRegistrationNumber, ")
                .append("u.country_code AS countryCode, ")
                .append("u.contact_number AS contactNumber, ")
                .append("u.status, ")
                .append("u.city_id AS cityId ")
                .append("FROM mh_users u ")
                .append("WHERE u.type = 'Lab'"); // Base query

        // Dynamically add filters
        if (!StringUtils.isEmpty(fullName)) {
            baseQuery.append(" AND CONCAT(u.first_name, ' ', u.last_name) LIKE :fullName");
        }
        if (!StringUtils.isEmpty(labName)) {
            baseQuery.append(" AND u.clinic_name LIKE :labName");
        }
        if (!StringUtils.isEmpty(labRegistrationNumber)) {
            baseQuery.append(" AND u.professional_identification_number LIKE :labRegistrationNumber");
        }
        if (!StringUtils.isEmpty(status)) {
            baseQuery.append(" AND u.status = :status");
        }
        if (!StringUtils.isEmpty(contactNumber)) {
            baseQuery.append(" AND CONCAT(u.country_code, '', u.contact_number) LIKE :contactNumber");
        }
        if (cityId != null && cityId > 0) {
            baseQuery.append(" AND u.city_id = " + cityId);
        }

        baseQuery.append(" GROUP BY u.user_id, u.contact_number, u.status ");

        baseQuery.append(" ORDER BY u." + sortField + " " + (sortBy.equals("0") ? "ASC" : "DESC"));

        // Create query
        Query query = entityManager.createNativeQuery(baseQuery.toString());

        // Set parameters
        if (!StringUtils.isEmpty(fullName)) {
            query.setParameter("fullName", "%" + fullName + "%");
        }
        if (!StringUtils.isEmpty(labName)) {
            query.setParameter("labName", "%" + labName + "%");
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
        if (!StringUtils.isEmpty(labRegistrationNumber)) {
            query.setParameter("labRegistrationNumber", "%" + labRegistrationNumber + "%");
        }
        if(page <= 0){
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
        List<LabUserListResponseDto> responseList = mapResultsToLabUserListResponseDto(results);

        // Total count query
        String countQuery = "SELECT COUNT(*) FROM (" + baseQuery + ") AS countQuery";
        Query countQ = entityManager.createNativeQuery(countQuery);

        // Set parameters for count query
        if (!StringUtils.isEmpty(fullName)) {
            countQ.setParameter("fullName", "%" + fullName + "%");
        }
        if (!StringUtils.isEmpty(labName)) {
            countQ.setParameter("labName", "%" + labName + "%");
        }
        if (!StringUtils.isEmpty(status)) {
            countQ.setParameter("status", status);
        }
        if (!StringUtils.isEmpty(contactNumber)) {
            countQ.setParameter("contactNumber", "%" + contactNumber + "%");
        }
        if (!StringUtils.isEmpty(labRegistrationNumber)) {
            countQ.setParameter("labRegistrationNumber", "%" + labRegistrationNumber + "%");
        }

        long totalCount = ((Number) countQ.getSingleResult()).longValue();

        // Create pageable response
        Page<LabUserListResponseDto> pageableResponse = new PageImpl<>(responseList, pageable, totalCount);

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

    private boolean validateStatus(String status) {
        for (StatusAI s : StatusAI.values()) {
            if (s.name().equals(status)) {
                return true;
            }
        }
        return false;
    }

    private List<LabUserListResponseDto> mapResultsToLabUserListResponseDto(List<Object[]> results) {
        return results.stream().map(row -> {
            Integer userId = (Integer) row[0];
            String labName = StringUtils.isEmpty((String) row[1]) ? "" : (String) row[1];
            String fullName = StringUtils.isEmpty((String) row[2]) ? "" : (String) row[2];
            String labRegistrationNumber = StringUtils.isEmpty((String) row[3]) ? "" : (String) row[3];
            String countryCode = StringUtils.isEmpty((String) row[4]) ? "" : (String) row[4];
            String contactNumber = (String) row[5];
            String status = (String) row[6];
            Integer cityId = (Integer) row[7];
            String cityName = "";

            if(cityId != null && cityId > 0){
                City city = cityRepository.findById(cityId).orElseThrow(() -> new PatientUserExceptionHandler("City Id not Found"));
                cityName = city.getName();
            }

            return new LabUserListResponseDto(
                    userId, labName.trim(), fullName, labRegistrationNumber, countryCode +""+ contactNumber, status, cityName
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public Object createLabUser(Locale locale, LabUserRequestDto requestDto) {
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
        long contactNumberCount = usersRepository.countByContactNumberAndType(requestDto.getContactNumber(), UserType.Lab);

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
        //Document
        if(requestDto.getDocumentList() != null && !requestDto.getDocumentList().isEmpty()){
            //TODO : store file in directory
            //TODO : creation of entity in mh_doctor_document
        }

        // Create Lab user
        Users labUser = getUsers(requestDto, pp, locale);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_CREATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    private Users getUsers(LabUserRequestDto requestDto, String pp, Locale locale) {
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
        Users labUser = new Users();
        labUser.setType(UserType.Lab);
        labUser.setFirstName(fullName[0]);
        labUser.setLastName(lastName);
        labUser.setEmail(requestDto.getEmail());
        labUser.setProfilePicture(pp);
        labUser.setCountry(c);
        labUser.setState(requestDto.getProvinceId());
        labUser.setCity(requestDto.getCityId());
        labUser.setContactNumber(requestDto.getContactNumber());
        labUser.setCountryCode(countryCode);
        labUser.setProfessionalIdentificationNumber(requestDto.getLabRegistrationNumber());
        labUser.setHospitalAddress(requestDto.getLabAddress());
        labUser.setStatus(StatusAI.A);
        labUser.setIsInternational(YesNo.No);
        labUser.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        labUser.setApprovedBy(0);
        labUser.setIsHpczVerified(YesNo.Yes.name());
        labUser.setIsHospitalVerified(YesNo.Yes.name());
        labUser.setIsSuspended(0);
        labUser.setAttemptCounter((short)0);
        labUser.setOtpCounter(0);
        labUser.setHospitalId(0);
        labUser.setNotificationLanguage(Constants.DEFAULT_LANGUAGE);
        labUser.setDoctorClassification(GENERAL_PRACTITIONER);
        labUser.setClassification(Classification.from_hospital);
        labUser.setIsInternational(YesNo.No);
        return usersRepository.save(labUser);
    }

    @Transactional
    public Object updateLabUser(Locale locale, Integer userId, LabUserRequestDto requestDto) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingLabUser = usersRepository.findByUserIdAndType(userId, UserType.Lab);
        if (existingLabUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingLabUser.get();

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
        //Document
        if(requestDto.getDocumentList() != null && !requestDto.getDocumentList().isEmpty()){
            //TODO : store the file into directory
            //TODO : create the new entity into mh_doctor_document table
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
        existingUser.setEmail(StringUtils.isEmpty(requestDto.getEmail()) ? null : requestDto.getEmail());
        existingUser.setContactNumber(requestDto.getContactNumber());
        existingUser.setPassword(utility.md5Hash(requestDto.getPassword()));
        existingUser.setCountry(c);
        existingUser.setState(requestDto.getProvinceId());
        existingUser.setCity(requestDto.getCityId());
        existingUser.setClinicName(requestDto.getLabName());
        existingUser.setHospitalAddress(requestDto.getLabAddress());
        existingUser.setProfilePicture(pp);
        existingUser.setProfessionalIdentificationNumber(requestDto.getLabRegistrationNumber());

        usersRepository.save(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_UPDATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object updateLabUserStatus(Locale locale, Integer userId, String status) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingLabUser = usersRepository.findByUserIdAndType(userId, UserType.Lab);
        if (existingLabUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingLabUser.get();

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

    public Object getLabUser(Locale locale, Integer userId) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingLabUser = usersRepository.findByUserIdAndType(userId, UserType.Lab);
        if (existingLabUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingLabUser.get();

        // Construct users entity to Lab user response dto
        LabUserResponseDto patientUserResponseDto = convertToLabUserResponseDto(existingUser, locale);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setData(patientUserResponseDto);
        response.setMessage(messageSource.getMessage(Messages.USER_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    private LabUserResponseDto convertToLabUserResponseDto(Users user, Locale locale) {
        LabUserResponseDto dto = new LabUserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setLabName(user.getClinicName());
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
        dto.setLabAddress(user.getHospitalAddress());
        dto.setLabRegistrationNumber(user.getProfessionalIdentificationNumber());

        return dto;
    }

    public Object deleteLabDocument(Locale locale, Integer userId, Integer documentId) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingLabUser = usersRepository.findByUserIdAndType(userId, UserType.Lab);
        if (existingLabUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users labUser = existingLabUser.get();

        //TODO : Delete entry from Doctor document
        return null;
    }
}
