package com.mhealth.admin.service;


import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.ValidateResult;
import com.mhealth.admin.dto.enums.*;
import com.mhealth.admin.dto.request.HospitalManagementRequestDto;
import com.mhealth.admin.dto.request.HospitalManagementUpdateRequestDto;
import com.mhealth.admin.dto.response.*;
import com.mhealth.admin.model.HospitalDetails;
import com.mhealth.admin.model.HospitalMerchantNumber;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.*;
import com.mhealth.admin.sms.SMSApiService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private FileService fileService;

    @Autowired
    private Utility utility;
    public Object getHospitalList(Locale locale, String name, String email, StatusAI status, String contactNumber, Integer sortBy, String sortField, int page, int size) {

        // Validate sortField
        List<String> validSortFields = Arrays.asList("sort", "clinicName", "contactNumber", "hospitalAddress", "email");
        if (!validSortFields.contains(sortField)) {
            sortField = null; // Set to null if the value is invalid
        }

        // Determine sorting direction
        Sort.Direction direct = sortBy.equals(0) ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Set default sorting field if sortField is null
        sortField = (sortField != null) ? sortField : "userId";

        // Create sorting and pageable objects
        Sort sort = Sort.by(direct, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch paginated results
        Page<Users> userPage = usersRepository.findHospitalsWithFilters(
                name != null ? "%" + name + "%" : null,
                email != null ? "%" + email + "%" : null,
                status != null ? status : null,
                contactNumber != null ? "%" + contactNumber + "%" : null,
                pageable
        );

        // Map to DTO
        Page<HospitalManagementResponseDto> responsePage = userPage.map(user -> {
            HospitalManagementResponseDto dto = new HospitalManagementResponseDto();
            dto.setUserId(user.getUserId());
            dto.setClinicName(user.getClinicName());
            dto.setEmail(user.getEmail());
            dto.setContactNumber(user.getCountryCode() + user.getContactNumber());
            dto.setNotificationContactNumber(getNotificationContactNumber(user));
            dto.setMerchantNumber(getMerchantNumber(user));
            dto.setPriority(user.getSort());
            dto.setClinicAddress(user.getHospitalAddress());
            dto.setNotificationLanguage(user.getNotificationLanguage());
            dto.setStatus(user.getStatus() != null ? user.getStatus().toString() : StatusAI.I.toString());
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



    private String getNotificationContactNumber(Users user) {
        Optional<HospitalDetails> details = hospitalDetailsRepository.findByUserId(user.getUserId());
        return details.map(HospitalDetails::getNotificationContactNumber).orElse("");
    }

    private String getMerchantNumber(Users user) {
        Optional<HospitalMerchantNumber> merchantNumber = hospitalMerchantNumberRepository.findByUserId(user.getUserId());
        return merchantNumber.map(HospitalMerchantNumber::getMerchantNumber).orElse("");
    }

    @Transactional
    public Object createHospitalManagement(Locale locale, HospitalManagementRequestDto requestDto, HttpServletRequest request) throws Exception {
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

        // Validate file uploads
        if (requestDto.getProfilePicture() != null) {
            ValidateResult validationResult = fileService.validateFile(locale, requestDto.getProfilePicture(), List.of("jpg", "jpeg", "png"), 1_000_000);
            if (!validationResult.isResult()) {
                response.setCode(Constants.CODE_O);
                response.setMessage(validationResult.getError());
                response.setStatus(Status.FAILED);
                return response;
            }
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
        user.setDoctorClassification(DoctorClassification.general_practitioner.toString());

        String encodedPassword = utility.md5Hash(requestDto.getPassword());
        user.setPassword(encodedPassword);
        user.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);

        if(requestDto.getPriority() != null){
            Users existSort = usersRepository.findBySort(Integer.valueOf(requestDto.getPriority()));
            if(existSort != null){
                response.setCode(Constants.CODE_O);
                response.setMessage(messageSource.getMessage(Messages.PRIORITY_ALREADY_EXISTS, null, locale));
                response.setStatus(Status.FAILED);
                return response;
            }
            user.setSort(Integer.valueOf(requestDto.getPriority()));
        }

        String fileName = null;

        if (requestDto.getProfilePicture() != null) {
            // Extract the file extension
            String extension = fileService.getFileExtension(Objects.requireNonNull(requestDto.getProfilePicture().getOriginalFilename()));

            // Generate a random file name
            fileName = UUID.randomUUID() + "." + extension;

            user.setProfilePicture(fileName);
        }

            user = usersRepository.save(user);

        // Save profile picture if provided
        if (requestDto.getProfilePicture() != null) {
            String filePath = Constants.USER_PROFILE_PICTURE + user.getUserId();

            // Save the file
            fileService.saveFile(requestDto.getProfilePicture(), filePath, fileName);
        }

        if(requestDto.getMerchantNumber() != null){
            HospitalMerchantNumber hospitalMerchantNumber = new HospitalMerchantNumber();
            hospitalMerchantNumber.setMerchantNumber(requestDto.getMerchantNumber());
            hospitalMerchantNumber.setUserId(user.getUserId());
            hospitalMerchantNumberRepository.save(hospitalMerchantNumber);
        }

        if(requestDto.getNotificationContactNumber() != null){
            HospitalDetails hospitalDetails = new HospitalDetails();
            hospitalDetails.setUserId(user.getUserId());
            hospitalDetails.setNotificationContactNumber(requestDto.getNotificationContactNumber());
            hospitalDetailsRepository.save(hospitalDetails);
        }

        // Assign role
        assignRole(user.getUserId(), UserType.Clinic.name());


        // Send SMS
        try {
            locale = Utility.getUserNotificationLanguageLocale(user.getNotificationLanguage(), locale);

            String link = messageSource.getMessage(Messages.HOSPITAL_LINK, new Object[]{request.getServerName()}, locale);

            String smsMessage = messageSource.getMessage(Messages.HOSPITAL_REGISTRATION_CONFIRMATION, new Object[]{
                    user.getClinicName(), link, user.getContactNumber(), requestDto.getPassword()}, locale);
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
    public void assignRole(Integer userId, String roleType) {
        try {
            // Delete existing roles
            authAssignmentRepository.deleteByUserId(String.valueOf(userId));

            // Insert new role
            authAssignmentRepository.insertRole(roleType, String.valueOf(userId), (int) (System.currentTimeMillis() / 1000));

        } catch (Exception ex) {
            log.error("exception occurred while assigning role to the user", ex);
            throw new RuntimeException("failed to assign role to user: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public Object updateHospitalManagement(Locale locale, Integer userId, HospitalManagementUpdateRequestDto requestDto) throws Exception {
        Response response = new Response();

        // Find the user
        Optional<Users> existUser = usersRepository.findByUserIdAndType(userId, UserType.Clinic);
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
        if(StringUtils.isEmpty(requestDto.getEmail())) {
            long emailCount = usersRepository.countByEmailAndUserIdNot(requestDto.getEmail(), userId);
            if (emailCount > 0) {
                response.setCode(Constants.CODE_O);
                response.setMessage(messageSource.getMessage(Messages.EMAIL_ALREADY_EXISTS, null, locale));
                response.setStatus(Status.FAILED);
                return response;
            }
        }
        long contactNumberCount = usersRepository.countByContactNumberAndTypeAndUserIdNot(requestDto.getContactNumber(), UserType.Clinic, userId);

        if (contactNumberCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.CONTACT_NUMBER_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Validate file uploads
        if (requestDto.getProfilePicture() != null) {
            ValidateResult validationResult = fileService.validateFile(locale, requestDto.getProfilePicture(), List.of("jpg", "jpeg", "png"), 1_000_000);
            if (!validationResult.isResult()) {
                response.setCode(Constants.CODE_O);
                response.setMessage(validationResult.getError());
                response.setStatus(Status.FAILED);
                return response;
            }
        }

        // Update the user fields
        existingUser.setClinicName(requestDto.getClinicName());
        existingUser.setEmail(StringUtils.isEmpty(requestDto.getEmail()) ? existingUser.getEmail() : requestDto.getEmail());
        existingUser.setContactNumber(requestDto.getContactNumber());
        existingUser.setHospitalAddress(requestDto.getClinicAddress());
        existingUser.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);

        if(requestDto.getPriority() != null){
            existingUser.setSort(Integer.valueOf(requestDto.getPriority()));
        } else {
            existingUser.setSort(null);
        }

        // Save profile picture if provided
        if (requestDto.getProfilePicture() != null) {
            String filePath = Constants.USER_PROFILE_PICTURE + existingUser.getUserId();

            // delete exist old profile
            if(existingUser.getProfilePicture() != null){
                fileService.deleteFile(filePath, existingUser.getProfilePicture());
            }

            // Extract the file extension
            String extension = fileService.getFileExtension(Objects.requireNonNull(requestDto.getProfilePicture().getOriginalFilename()));

            // Generate a random file name
            String fileName = UUID.randomUUID() + "." + extension;


            // Save the file
            fileService.saveFile(requestDto.getProfilePicture(), filePath, fileName);

            existingUser.setProfilePicture(fileName);
        }

        usersRepository.save(existingUser);


        if(requestDto.getNotificationContactNumber() != null){
            Optional<HospitalDetails> hospitalDetails = hospitalDetailsRepository.findByUserId(existingUser.getUserId());
            if (hospitalDetails.isPresent()) {
                HospitalDetails existingHospitalDetails = hospitalDetails.get();
                existingHospitalDetails.setNotificationContactNumber(requestDto.getNotificationContactNumber());
                hospitalDetailsRepository.save(existingHospitalDetails);
            } else {
                HospitalDetails saveNew = new HospitalDetails();
                saveNew.setUserId(existingUser.getUserId());
                saveNew.setNotificationContactNumber(requestDto.getNotificationContactNumber());
                hospitalDetailsRepository.save(saveNew);
            }
        } else {
            Optional<HospitalDetails> hospitalDetails = hospitalDetailsRepository.findByUserId(existingUser.getUserId());
            if (hospitalDetails.isPresent()) {
                HospitalDetails existingHospitalDetails = hospitalDetails.get();
                hospitalDetailsRepository.delete(existingHospitalDetails);
            }
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
    public Object deleteHospitalManagement(Locale locale, Integer id) throws Exception {
        Response response = new Response();

        // Find the user
        Optional<Users> existingData = usersRepository.findByUserIdAndType(id, UserType.Clinic);
        if (existingData.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingData.get();

        String filePath = Constants.USER_PROFILE_PICTURE + existingUser.getUserId();

        hospitalMerchantNumberRepository.deleteByUserId(existingData.get().getUserId());

        hospitalDetailsRepository.deleteByUserId(existingData.get().getUserId());

        String profileId = null;
        if(existingUser.getProfilePicture() != null){
            profileId = existingUser.getProfilePicture();
        }

        // Delete related records from auth_assignment table
        authAssignmentRepository.deleteByUserId(String.valueOf(id));

        usersRepository.delete(existingUser);

        // delete file and folder
        if(profileId != null){
            fileService.deleteFileOrFolder(filePath);
        }
        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_DELETED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object getHospitalManagement(Locale locale, Integer userId) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingData = usersRepository.findByUserIdAndType(userId, UserType.Clinic);
        if (existingData.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingData.get();

        HospitalManagementResponseDto responseDto = convertToUserResponseDto(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setData(responseDto);
        response.setMessage(messageSource.getMessage(Messages.USER_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;

    }

    private HospitalManagementResponseDto convertToUserResponseDto(Users users) {
        HospitalManagementResponseDto responseDto = new HospitalManagementResponseDto();
        responseDto.setUserId(users.getUserId());
        responseDto.setClinicName(users.getClinicName());
        responseDto.setClinicAddress(users.getHospitalAddress());
        responseDto.setEmail(users.getEmail());
        responseDto.setContactNumber(users.getContactNumber());
        responseDto.setNotificationLanguage(users.getNotificationLanguage());
        responseDto.setPriority(users.getSort());
        responseDto.setStatus(users.getStatus() != null ? users.getStatus().toString() : StatusAI.I.toString());

        responseDto.setProfilePicture(users.getProfilePicture() != null ? Constants.USER_PROFILE_PICTURE + users.getUserId() + "/" + users.getProfilePicture() : null);
        HospitalMerchantNumber hospitalMerchantNumber = hospitalMerchantNumberRepository.findByUserId(users.getUserId()).orElse(null);
        if(hospitalMerchantNumber != null){
            responseDto.setMerchantNumber(hospitalMerchantNumber.getMerchantNumber());        }
        HospitalDetails hospitalDetails = hospitalDetailsRepository.findByUserId(users.getUserId()).orElse(null);
        if(hospitalDetails != null){
            responseDto.setNotificationContactNumber(hospitalDetails.getNotificationContactNumber());
        }
        return responseDto;
    }

}
