package com.mhealth.admin.service;


import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.ValidateResult;
import com.mhealth.admin.dto.enums.*;
import com.mhealth.admin.dto.labUserDto.*;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.DocumentResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.*;
import com.mhealth.admin.model.State;
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

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mhealth.admin.constants.Messages.GENERAL_PRACTITIONER;

@Slf4j
@Service
public class LabUserService {
    @Autowired
    private AuthAssignmentRepository authAssignmentRepository;
    @Autowired
    private DoctorDocumentRepository doctorDocumentRepository;
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
    @Autowired
    private FileService fileService;

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
        if (!sortByValues.contains(sortField)) {
            sortField = "user_id";
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
            if (!validateStatus(status)) {
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
        if (page <= 0) {
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

            if (cityId != null && cityId > 0) {
                City city = cityRepository.findById(cityId).orElse(null);
                if (city != null) cityName = city.getName();
            }

            return new LabUserListResponseDto(
                    userId, labName.trim(), fullName, labRegistrationNumber, countryCode + contactNumber, status, cityName
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public Object createLabUser(Locale locale, LabUserRequestDto requestDto) throws Exception {
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
        if(!StringUtils.isEmpty(requestDto.getEmail())) {
            long emailCount = usersRepository.countByEmail(requestDto.getEmail());
            if (emailCount > 0) {
                response.setCode(Constants.CODE_O);
                response.setMessage(messageSource.getMessage(Messages.EMAIL_ALREADY_EXISTS, null, locale));
                response.setStatus(Status.FAILED);
                return response;
            }
        }
        long contactNumberCount = usersRepository.countByContactNumberAndType(requestDto.getContactNumber(), UserType.Lab);

        if (contactNumberCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.CONTACT_NUMBER_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        //profile picture
        String pp = null;
        if (requestDto.getProfilePicture() != null) {
            ValidateResult validationResult = fileService.validateFile(locale, requestDto.getProfilePicture(), List.of("jpg", "jpeg", "png"), 1_000_000);
            if (!validationResult.isResult()) {
                response.setCode(Constants.CODE_O);
                response.setMessage(validationResult.getError());
                response.setStatus(Status.FAILED);
                return response;
            }
        }
        //Document
        if (requestDto.getDocumentList() != null && !requestDto.getDocumentList().isEmpty()) {
            for (LabFileDto dto : requestDto.getDocumentList()) {
                if (dto.validate() != null) {
                    response.setCode(Constants.CODE_O);
                    response.setMessage(dto.validate());
                    response.setStatus(Status.FAILED);
                    return response;
                }
                ValidateResult validationResult = fileService.validateFile(locale, dto.getDocument(), List.of("pdf"), 5_000_000);
                if (!validationResult.isResult()) {
                    response.setCode(Constants.CODE_O);
                    response.setMessage(validationResult.getError());
                    response.setStatus(Status.FAILED);
                    return response;
                }
            }
        }

        // Create Lab user
        Users labUser = getUsers(requestDto, pp, locale, null);

        // Assign role
        assignRole(labUser.getUserId(), UserType.Lab.name());

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

    private Users getUsers(LabUserRequestDto requestDto, String pp, Locale locale, Users users) throws IOException {
        //first name and last name
        String[] fullName = requestDto.getFullName().trim().split(" ");
        String lastName = "";
        StringBuilder sb = new StringBuilder();
        if (fullName.length > 1) {
            for (int i = 1; i < fullName.length; i++) {
                sb.append(fullName[i]).append(" ");
            }
            lastName = sb.toString();
        }


        Country c = countryRepository.findById(requestDto.getCountryId()).orElse(null);

        Users labUser = new Users();

        labUser.setType(UserType.Lab);
        labUser.setFirstName(fullName[0]);
        labUser.setLastName(lastName);
        labUser.setEmail(StringUtils.isEmpty(requestDto.getEmail()) ? null : requestDto.getEmail());
        labUser.setProfilePicture(pp);
        labUser.setCountry(c);
        labUser.setState(requestDto.getProvinceId());
        labUser.setCity(requestDto.getCityId());
        labUser.setContactNumber(requestDto.getContactNumber());
        labUser.setCountryCode(countryCode);
        labUser.setProfessionalIdentificationNumber(requestDto.getLabRegistrationNumber());
        labUser.setClinicName(!StringUtils.isEmpty(requestDto.getLabName()) ? requestDto.getLabName() : null);
        labUser.setHospitalAddress(requestDto.getLabAddress());
        labUser.setStatus(StatusAI.A);
        labUser.setIsInternational(YesNo.No);
        labUser.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        labUser.setApprovedBy(0);
        labUser.setIsHpczVerified(YesNo.Yes.name());
        labUser.setIsHospitalVerified(YesNo.Yes.name());
        labUser.setIsSuspended(0);
        labUser.setAttemptCounter((short) 0);
        labUser.setOtpCounter(0);
        labUser.setHospitalId(0);
        labUser.setNotificationLanguage(Constants.DEFAULT_LANGUAGE);
        labUser.setDoctorClassification(GENERAL_PRACTITIONER);
        labUser.setClassification(Classification.from_hospital);
        labUser.setIsInternational(YesNo.No);
        usersRepository.save(labUser);

        //profile picture
        if (requestDto.getProfilePicture() != null) {
            String filePath = Constants.USER_PROFILE_PICTURE + labUser.getUserId();
            String fileName = requestDto.getProfilePicture().getOriginalFilename();

            // Save the file
            fileService.saveFile(requestDto.getProfilePicture(), filePath, fileName);

            labUser.setProfilePicture(fileName);
        }

        //Document
        if (requestDto.getDocumentList() != null && !requestDto.getDocumentList().isEmpty()) {
            for (LabFileDto dto : requestDto.getDocumentList()) {
                String filePath = Constants.DOCTOR_DOCUMENT_PATH + labUser.getUserId();

                // Extract the file extension
                String extension = fileService.getFileExtension(Objects.requireNonNull(dto.getDocument().getOriginalFilename()));

                // Generate a random file name
                String fileName = UUID.randomUUID() + "." + extension;

                // Save the file
                fileService.saveFile(dto.getDocument(), filePath, fileName);

                //Insert new entry into Doctor Document table
                DoctorDocument doc = new DoctorDocument();
                doc.setUserId(labUser.getUserId());
                doc.setCreatedAt(LocalDateTime.now());
                doc.setDocumentName(StringUtils.isEmpty(dto.getDocumentName()) ? "" : dto.getDocumentName());
                doc.setDocumentFileName(fileName);
                doc.setUpdatedAt(LocalDateTime.now());
                doc.setStatus(DocumentStatus.Active);

                doctorDocumentRepository.save(doc);
            }
        }

        return usersRepository.save(labUser);
    }

    private Users updateUser(LabUserUpdateRequestDto requestDto, Locale locale, Users labUser) throws IOException {
        //first name and last name
        String[] fullName = requestDto.getFullName().trim().split(" ");
        String lastName = "";
        StringBuilder sb = new StringBuilder();
        if (fullName.length > 1) {
            for (int i = 1; i < fullName.length; i++) {
                sb.append(fullName[i]).append(" ");
            }
            lastName = sb.toString();
        }

        labUser.setFirstName(fullName[0]);
        labUser.setLastName(lastName);
        labUser.setEmail(StringUtils.isEmpty(requestDto.getEmail()) ? labUser.getEmail() : requestDto.getEmail());
        Country c = countryRepository.findById(requestDto.getCountryId()).orElse(null);
        labUser.setCountry(c);
        labUser.setState(requestDto.getProvinceId());
        labUser.setCity(requestDto.getCityId());
        labUser.setContactNumber(requestDto.getContactNumber());
        labUser.setProfessionalIdentificationNumber(requestDto.getLabRegistrationNumber());
        labUser.setClinicName(requestDto.getLabName());
        labUser.setHospitalAddress(requestDto.getLabAddress());
        labUser.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        usersRepository.save(labUser);

        //profile picture
        if (requestDto.getProfilePicture() != null) {
            String filePath = Constants.USER_PROFILE_PICTURE + labUser.getUserId();
            String fileName = requestDto.getProfilePicture().getOriginalFilename();

            // Save the file
            fileService.saveFile(requestDto.getProfilePicture(), filePath, fileName);

            labUser.setProfilePicture(fileName);
        }

        //Document
        if (requestDto.getDocumentList() != null && !requestDto.getDocumentList().isEmpty()) {
            for (LabFileDto dto : requestDto.getDocumentList()) {

                String filePath = Constants.DOCTOR_DOCUMENT_PATH + labUser.getUserId();

                // Extract the file extension
                String extension = fileService.getFileExtension(Objects.requireNonNull(dto.getDocument().getOriginalFilename()));

                // Generate a random file name
                String fileName = UUID.randomUUID() + "." + extension;

                // Save the file
                fileService.saveFile(dto.getDocument(), filePath, fileName);

                //Insert new entry into Doctor Document table
                DoctorDocument doc = new DoctorDocument();
                doc.setUserId(labUser.getUserId());
                doc.setCreatedAt(LocalDateTime.now());
                doc.setDocumentName(StringUtils.isEmpty(dto.getDocumentName()) ? "" : dto.getDocumentName());
                doc.setDocumentFileName(fileName);
                doc.setUpdatedAt(LocalDateTime.now());
                doc.setStatus(DocumentStatus.Active);

                doctorDocumentRepository.save(doc);
            }
        }

        return usersRepository.save(labUser);
    }

    @Transactional
    public Object updateLabUser(Locale locale, Integer userId, LabUserUpdateRequestDto requestDto) throws Exception {
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

        //profile picture validation
        if (requestDto.getProfilePicture() != null) {
            ValidateResult validationResult = fileService.validateFile(locale, requestDto.getProfilePicture(), List.of("jpg", "jpeg", "png"), 1_000_000);
            if (!validationResult.isResult()) {
                response.setCode(Constants.CODE_O);
                response.setMessage(validationResult.getError());
                response.setStatus(Status.FAILED);
                return response;
            }
        }
        //Document validation
        if (requestDto.getDocumentList() != null && !requestDto.getDocumentList().isEmpty()) {
            for (LabFileDto dto : requestDto.getDocumentList()) {
                if (dto.validate() != null) {
                    response.setCode(Constants.CODE_O);
                    response.setMessage(dto.validate());
                    response.setStatus(Status.FAILED);
                    return response;
                }
                ValidateResult validationResult = fileService.validateFile(locale, dto.getDocument(), List.of("pdf"), 5_000_000);
                if (!validationResult.isResult()) {
                    response.setCode(Constants.CODE_O);
                    response.setMessage(validationResult.getError().equals(messageSource.getMessage(Messages.SELECT_PROFILE_PICTURE, null, locale)) ? "Please select PDF file" : validationResult.getError());
                    response.setStatus(Status.FAILED);
                    return response;
                }
            }
        }

        // Check for duplicate email and contact number
        if (StringUtils.isEmpty(requestDto.getEmail())) {
            long emailCount = usersRepository.countByEmailAndUserIdNot(requestDto.getEmail(), userId);
            if (emailCount > 0) {
                response.setCode(Constants.CODE_O);
                response.setMessage(messageSource.getMessage(Messages.EMAIL_ALREADY_EXISTS, null, locale));
                response.setStatus(Status.FAILED);
                return response;
            }
        }
        long contactNumberCount = usersRepository.countByContactNumberAndTypeAndUserIdNot(requestDto.getContactNumber(), UserType.Lab, userId);
        if (contactNumberCount > 0) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.CONTACT_NUMBER_ALREADY_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users labUser = updateUser(requestDto, locale, existingUser);

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
        if (!validateStatus(status)) {
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
        dto.setLabAddress(user.getHospitalAddress());
        dto.setLabRegistrationNumber(user.getProfessionalIdentificationNumber());

        //state
        if (user.getState() != null && user.getState() != 0) {
            State state = stateRepository.findById(user.getState()).orElse(null);
            if (state != null) {
                dto.setStateId(state.getId());
                dto.setStateName(state.getName());
            }
        }
        //city
        if (user.getCity() != null && user.getCity() != 0) {
            City city = cityRepository.findById(user.getCity()).orElse(null);
            if (city != null) {
                dto.setCityId(city.getId());
                dto.setCityName(city.getName());
            }
        }
        //Profile picture
        if (!StringUtils.isEmpty(user.getProfilePicture())) {
            dto.setProfilePicture(Constants.USER_PROFILE_PICTURE + user.getUserId() + "/" + user.getProfilePicture());
        }

        //Document
        List<DoctorDocument> labDocuments = doctorDocumentRepository.findByUserIdAndStatus(user.getUserId(), DocumentStatus.Active);
        if (!labDocuments.isEmpty()) {
            dto.setDocumentList(mapLabDocumentsToDocumentResponseDtoList(labDocuments));
        }

        return dto;
    }

    private List<DocumentResponseDto> mapLabDocumentsToDocumentResponseDtoList(List<DoctorDocument> labDocuments) {
        return labDocuments.stream().map(row -> new DocumentResponseDto(
                row.getUserId(),
                row.getDocumentId(),
                StringUtils.isEmpty(row.getDocumentFileName())
                        ? null
                        : Constants.DOCTOR_DOCUMENT_PATH + row.getUserId() + "/" + row.getDocumentFileName()
        )).collect(Collectors.toList());
    }

    public Object deleteLabDocument(Locale locale, Integer userId, Integer documentId) throws IOException {
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

        //Delete file from directory
        DoctorDocument doc = doctorDocumentRepository.findByUserIdAndDocumentId(labUser.getUserId(), documentId);
        if (doc == null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.DOCUMENT_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }
        String directory = Constants.DOCTOR_DOCUMENT_PATH + labUser.getUserId();
        fileService.deleteFile(directory, doc.getDocumentFileName());

        //delete from table
        doctorDocumentRepository.delete(doc);

        response.setCode(Constants.CODE_1);
        response.setMessage("Document Deleted successfully");
        response.setStatus(Status.SUCCESS);
        return response;
    }

    public Object deleteLabUser(Locale locale, Integer userId) {
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

        usersRepository.delete(labUser);

        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_DELETED, null, locale));
        response.setStatus(Status.SUCCESS);
        return response;
    }

}
