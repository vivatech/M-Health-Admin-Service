package com.mhealth.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.ValidateResult;
import com.mhealth.admin.dto.enums.*;
import com.mhealth.admin.dto.request.DoctorUserRequestDto;
import com.mhealth.admin.dto.request.SetDoctorAvailabilityRequestDto;
import com.mhealth.admin.dto.response.DoctorAvailabilityResponseDto;
import com.mhealth.admin.dto.request.DoctorUserResponseDto;
import com.mhealth.admin.dto.request.DoctorUserUpdateRequestDto;
import com.mhealth.admin.dto.response.DoctorUserListResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import com.mhealth.admin.sms.SMSApiService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static com.mhealth.admin.constants.Messages.DOCTOR_AVAILABILITY_FOUND;
import static com.mhealth.admin.constants.Messages.RECORD_NOT_FOUND;
import java.math.BigDecimal;
import java.math.RoundingMode;


@Slf4j
@Service
public class DoctorUserService {
    @Autowired
    private SlotMasterRepository slotMasterRepository;
    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Autowired
    private GlobalService globalService;

    @Autowired
    private SpecializationService specializationService;

    @Autowired
    private FileService fileService;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private SlotTypeRepository slotTypeRepository;

    @Autowired
    private GlobalConfigurationRepository globalConfigurationRepository;

    @Autowired
    private ChargesRepository chargesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private HospitalMerchantNumberRepository hospitalMerchantNumberRepository;

    @Autowired
    private SpecializationRepository specializationRepository;

    @Autowired
    private DoctorSpecializationRepository doctorSpecializationRepository;

    @Autowired
    private AuthAssignmentRepository authAssignmentRepository;

    @Autowired
    private DoctorDocumentRepository doctorDocumentRepository;

    @Autowired
    private Utility utility;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SMSApiService smsApiService;

    @Autowired
    private EntityManager entityManager;

    @Value("${app.sms.sent}")
    private boolean smsSent;

    @Value("${m-health.country}")
    private String country;

    @Value("${m-health.country.code}")
    private String countryCode;

    @Value("${m-health.project.name}")
    private String projectName;


    public Object getDoctorsUserList(Locale locale, String name, String email, String contactNumber, String status, String isInternational, String sortField, String sortBy, int page, int size) {
        // Define valid sort fields
        Set<String> validSortFields = new HashSet<>(Arrays.asList("name", "clinicName", "isInternational", "contactNumber", "email"));

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ")
                .append("u.user_id AS userId, ")
                .append("CONCAT(u.first_name, ' ', u.last_name) AS name, ")
                .append("u.clinic_name AS clinicName, ")
                .append("u.is_international AS isInternational, ")
                .append("(SELECT GROUP_CONCAT(s.name) FROM mh_doctor_specialization ds JOIN mh_specialisation s ON ds.specialization_id = s.id WHERE ds.user_id = u.user_id) AS specializations, ")
                .append("(SELECT JSON_ARRAYAGG( ")
                .append("    JSON_OBJECT( ")
                .append("        'feeType', c.fee_type, ")
                .append("        'finalConsultationFees', c.final_consultation_fees ")
                .append("    ) ")
                .append(") FROM mh_charges c WHERE c.user_id = u.user_id) AS charges, ")
                .append("CONCAT(u.country_code, u.contact_number) AS contactNumber, ")
                .append("u.email AS email, ")
                .append("u.status AS status ")
                .append("FROM mh_users u WHERE u.type = 'Doctor' "); // Base query with WHERE clause

        // Add search filters
        if (name != null && !name.isEmpty()) {
            queryBuilder.append("AND CONCAT(u.first_name, ' ', u.last_name) LIKE :name ");
        }
        if (email != null && !email.isEmpty()) {
            queryBuilder.append("AND u.email LIKE :email ");
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            queryBuilder.append("AND CONCAT(u.country_code, u.contact_number) LIKE :contactNumber ");
        }
        if (status != null && !status.isEmpty()) {
            queryBuilder.append("AND u.status = :status ");
        }
        if (isInternational != null && !isInternational.isEmpty()) {
            queryBuilder.append("AND u.is_international = :isInternational ");
        }

        // Add sorting field
        if (sortField != null && !sortField.isEmpty() && validSortFields.contains(sortField)) {
            queryBuilder.append("ORDER BY ")
                    .append(sortField);
        } else {
            queryBuilder.append("ORDER BY userId");
        }

        // Add sorting direction based on sortBy (ASC or DESC)
        if (sortBy != null && !sortBy.isEmpty()) {
            if (Objects.equals(sortBy, "1")) {
                queryBuilder.append(" DESC ");
            } else {
                queryBuilder.append(" ASC ");
            }
        } else {
            queryBuilder.append(" DESC "); // Default to DESC if sortBy is not provided
        }

        // Add pagination
        queryBuilder.append("LIMIT ")
                .append((page - 1) * size)
                .append(", ")
                .append(size);

        String finalQuery = queryBuilder.toString();

        // Create the query
        Query nativeQuery = entityManager.createNativeQuery(finalQuery);

        // Set parameters for search fields
        if (name != null && !name.isEmpty()) {
            nativeQuery.setParameter("name", "%" + name + "%");
        }
        if (email != null && !email.isEmpty()) {
            nativeQuery.setParameter("email", "%" + email + "%");
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            nativeQuery.setParameter("contactNumber", "%" + contactNumber + "%");
        }
        if (status != null && !status.isEmpty()) {
            nativeQuery.setParameter("status", status);
        }
        if (isInternational != null && !isInternational.isEmpty()) {
            nativeQuery.setParameter("isInternational", isInternational);
        }

        // Execute the query and fetch results
        List<Object[]> results = nativeQuery.getResultList();

        // Map results to DTO
        List<DoctorUserListResponseDto> responseList = results.stream()
                .map(this::mapToDoctorUserListResponseDto)
                .toList();


        // Count query for pagination
        String countQuery = "SELECT COUNT(u.user_id) FROM mh_users u WHERE u.type = 'Doctor' ";
        if (name != null && !name.isEmpty()) {
            countQuery += "AND CONCAT(u.first_name, ' ', u.last_name) LIKE :name ";
        }
        if (email != null && !email.isEmpty()) {
            countQuery += "AND u.email LIKE :email ";
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            countQuery += "AND CONCAT(u.country_code, u.contact_number) LIKE :contactNumber ";
        }
        if (status != null && !status.isEmpty()) {
            countQuery += "AND u.status = :status ";
        }
        if (isInternational != null && !isInternational.isEmpty()) {
            countQuery += "AND u.is_international = :isInternational ";
        }

        Query countNativeQuery = entityManager.createNativeQuery(countQuery);
        if (name != null && !name.isEmpty()) {
            countNativeQuery.setParameter("name", "%" + name + "%");
        }
        if (email != null && !email.isEmpty()) {
            countNativeQuery.setParameter("email", "%" + email + "%");
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            countNativeQuery.setParameter("contactNumber", "%" + contactNumber + "%");
        }
        if (status != null && !status.isEmpty()) {
            countNativeQuery.setParameter("status", status);
        }
        if (isInternational != null && !isInternational.isEmpty()) {
            countNativeQuery.setParameter("isInternational", isInternational);
        }

        Long totalDoctors = ((Number) countNativeQuery.getSingleResult()).longValue();

        // Create response with data and pagination
        Map<String, Object> data = new HashMap<>();
        data.put("userList", responseList);
        data.put("totalCount", totalDoctors);

        Response response = new Response();
        response.setCode(Constants.CODE_1);
        response.setData(data);
        response.setMessage(messageSource.getMessage(Messages.USER_LIST_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    private DoctorUserListResponseDto mapToDoctorUserListResponseDto(Object[] row) {
        // Parse charges JSON
        String chargesJson = (String) row[5]; // The charges column, which is a JSON string
        List<Map<String, Object>> charges = new ArrayList<>();

        if (chargesJson != null && !chargesJson.isEmpty()) {
            // Assuming chargesJson is a valid JSON array string
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> chargeList = objectMapper.readValue(chargesJson, List.class);

                for (Map<String, Object> chargeMap : chargeList) {
                    String feeType = (String) chargeMap.get("feeType");
                    Double finalConsultationFees = (Double) chargeMap.get("finalConsultationFees");
                    Map<String, Object> charge = new HashMap<>();
                    charge.put("feeType", feeType);
                    charge.put("finalConsultationFees", new BigDecimal(finalConsultationFees).setScale(2, RoundingMode.HALF_UP));
                    charges.add(charge);
                }
            } catch (JsonProcessingException e) {
               log.error("exception occurs while parsing charges for userId: {}",row[0], e);
            }
        }

        // Handle null or missing values for specializations and clinicName
        String clinicName = (row[2] != null) ? (String) row[2] : "";
        String specializations = (row[4] != null) ? (String) row[4] : DoctorClassification.general_practitioner.name();

        return new DoctorUserListResponseDto(
                (Integer) row[0], // userId
                (String) row[1],  // doctorName
                clinicName,  // clinicName
                (String) row[3],  // isInternational
                Arrays.asList(specializations.split(",")), // specializations
                charges, // charges
                (String) row[6],  // contactNumber
                (String) row[7],  // email
                (String) row[8]   // status
        );
    }



    @Transactional
    public Object createDoctorUser(Locale locale, DoctorUserRequestDto requestDto) throws Exception {
        Response response = new Response();

        // Validate the input
        String validationMessage = requestDto.validate();
        if (validationMessage != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(validationMessage);
            response.setStatus(Status.FAILED);
            return response;
        }

        // Validate  profile picture
        if (requestDto.getProfilePicture() != null) {
            ValidateResult validationResult = fileService.validateFile(locale, requestDto.getProfilePicture(), List.of("jpg", "jpeg", "png"), 1_000_000);
            if (!validationResult.isResult()) {
                response.setCode(Constants.CODE_O);
                response.setMessage(validationResult.getError());
                response.setStatus(Status.FAILED);
                return response;
            }
        }

        // Validate doctor documents
        if (requestDto.getDocuments() != null) {
            for (Map<String, Object> documentMap : requestDto.getDocuments()) {
                MultipartFile document = (MultipartFile) documentMap.get("file"); // The actual file
                ValidateResult validationResult = fileService.validateFile(locale, document, List.of("pdf"), 2_200_000);
                if (!validationResult.isResult()) {
                    response.setCode(Constants.CODE_O);
                    response.setMessage(validationResult.getError());
                    response.setStatus(Status.FAILED);
                    return response;
                }
            }
        }

        // Check for duplicate email and contact number
        long emailCount = 0;
        if (requestDto.getEmail() != null && !requestDto.getEmail().trim().isEmpty()) {
            emailCount = usersRepository.countByEmail(requestDto.getEmail());
        }
        long contactNumberCount = usersRepository.countByContactNumberAndType(requestDto.getContactNumber(), UserType.Doctor);

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

        // Get selected languages & specialization
        List<Integer> selectedLanguages = getSelectedLanguageFluency(requestDto.getLanguagesFluency(), locale);
        List<Integer> selectedSpecializations = getSelectedSpecialization(requestDto.getSpecializations(), locale);

        // Get selected country
        Country country = countryRepository.findById(requestDto.getCountryId()).orElse(null);

        // Get default slot
        Integer slotTypeId = slotTypeRepository.findDefaultSlot(SlotStatus.active.name()).orElse(null);

        // Create and persist entities
        Users user = new Users();
        user.setSlotTypeId(slotTypeId);
        user.setType(UserType.Doctor);
        user.setFirstName(cleanAndAddPrefix(requestDto.getFirstName()));
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        user.setContactNumber(requestDto.getContactNumber());
        user.setPassword(utility.md5Hash(requestDto.getPassword()));
        user.setCountry(country);
        user.setDoctorClassification(requestDto.getDoctorClassification());
        user.setCountryCode(requestDto.getCountryCode());
        user.setState(requestDto.getProvinceId());
        user.setCity(requestDto.getCityId());
        user.setHospitalAddress(requestDto.getHospitalAddress());
        user.setHasDoctorVideo(requestDto.getHasDoctorVideo());
        user.setResidenceAddress(requestDto.getResidenceAddress());
        user.setExperience(requestDto.getExperience());
        user.setExtraActivities(requestDto.getExtraActivities());
        user.setAboutMe(requestDto.getAboutMe());
        user.setLanguageFluency(String.join(",", selectedLanguages.stream().map(String::valueOf).toList()));
        user.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);
        user.setHospitalId(requestDto.getClassification().equals(String.valueOf(Classification.from_hospital)) ? requestDto.getHospitalId() : 0);
        user.setGender(requestDto.getGender());
        user.setPassingYear(requestDto.getPassingYear());
        user.setUniversityName(requestDto.getUniversityName());
        user.setIsInternational(requestDto.getCountryCode().equals(countryCode) ? YesNo.No : YesNo.Yes);
        user.setClassification(Classification.valueOf(requestDto.getClassification()));

        // Save the user
        user = usersRepository.save(user);

        // Save profile picture if provided
        if (requestDto.getProfilePicture() != null) {
            String filePath = Constants.USER_PROFILE_PICTURE + user.getUserId();

            // Extract the file extension
            String extension = fileService.getFileExtension(Objects.requireNonNull(requestDto.getProfilePicture().getOriginalFilename()));

            // Generate a random file name
            String fileName = UUID.randomUUID() + "." + extension;

            // Save the file
            fileService.saveFile(requestDto.getProfilePicture(), filePath, fileName);

            user.setProfilePicture(fileName);

        }

        // Save the user
        user = usersRepository.save(user);

        // Process & save doctor documents
        processAndSaveDoctorDocuments(requestDto.getDocuments(), user.getUserId());

        // Process & save hospital merchant number
        processAndSaveHospitalMerchantNumber(requestDto.getClassification(), requestDto.getCountryCode(), user.getUserId(), requestDto.getMerchantNumber());

        // Process & save charges
        processAndSaveCharges(requestDto, user.getUserId());

        // Process & save doctor specializations
        processAndSaveDoctorSpecialization(selectedSpecializations, user);

        // Assign role
        assignRole(user.getUserId(), UserType.Doctor.name());

        // Send SMS
        try {
            locale = Utility.getUserNotificationLanguageLocale(user.getNotificationLanguage(), locale);
            String smsMessage = messageSource.getMessage(Messages.REGISTER_DOCTOR_USER, new Object[]{user.getFirstName() + " " + user.getLastName(), projectName}, locale);
            String smsNumber = "+" + countryCode + requestDto.getContactNumber();
            if(smsSent){
                smsApiService.sendMessage(smsNumber, smsMessage, this.country);
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
    public Object updateDoctorUser(Locale locale, Integer userId, DoctorUserUpdateRequestDto requestDto) throws Exception {
        Response response = new Response();

        // Find the user
        Optional<Users> existingDoctorUser = usersRepository.findByUserIdAndType(userId, UserType.Doctor);
        if (existingDoctorUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingDoctorUser.get();

        // Validate the input
        String validationMessage = requestDto.validate();
        if (validationMessage != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(validationMessage);
            response.setStatus(Status.FAILED);
            return response;
        }

        // Validate  profile picture
        if (requestDto.getProfilePicture() != null) {
            ValidateResult validationResult = fileService.validateFile(locale, requestDto.getProfilePicture(), List.of("jpg", "jpeg", "png"), 1_000_000);
            if (!validationResult.isResult()) {
                response.setCode(Constants.CODE_O);
                response.setMessage(validationResult.getError());
                response.setStatus(Status.FAILED);
                return response;
            }
        }

        // Validate doctor documents
        if (requestDto.getDocuments() != null) {
            for (Map<String, Object> documentMap : requestDto.getDocuments()) {
                MultipartFile document = (MultipartFile) documentMap.get("file"); // The actual file
                ValidateResult validationResult = fileService.validateFile(locale, document, List.of("pdf"), 2_200_000);
                if (!validationResult.isResult()) {
                    response.setCode(Constants.CODE_O);
                    response.setMessage(validationResult.getError());
                    response.setStatus(Status.FAILED);
                    return response;
                }
            }
        }

        // Check for duplicate email and contact number
        long emailCount = 0;
        if (requestDto.getEmail() != null && !requestDto.getEmail().trim().isEmpty()) {
            emailCount = usersRepository.countByEmailAndUserIdNot(requestDto.getEmail(), userId);
        }
        long contactNumberCount = usersRepository.countByContactNumberAndTypeAndUserIdNot(requestDto.getContactNumber(), UserType.Doctor, userId);

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

        // Get selected languages & specialization
        List<Integer> selectedLanguages = getSelectedLanguageFluency(requestDto.getLanguagesFluency(), locale);
        List<Integer> selectedSpecializations = getSelectedSpecialization(requestDto.getSpecializations(), locale);

        // Get selected country
        Country country = countryRepository.findById(requestDto.getCountryId()).orElse(null);

        // Get default slot
        Integer slotTypeId = slotTypeRepository.findDefaultSlot(SlotStatus.active.name()).orElse(null);

        // Update the user fields
        existingUser.setSlotTypeId(slotTypeId);
        existingUser.setType(UserType.Doctor);
        existingUser.setFirstName(cleanAndAddPrefix(requestDto.getFirstName()));
        existingUser.setLastName(requestDto.getLastName());
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setContactNumber(requestDto.getContactNumber());
        existingUser.setCountry(country);
        existingUser.setDoctorClassification(requestDto.getDoctorClassification());
        existingUser.setCountryCode(requestDto.getCountryCode());
        existingUser.setState(requestDto.getProvinceId());
        existingUser.setCity(requestDto.getCityId());
        existingUser.setHospitalAddress(requestDto.getHospitalAddress());
        existingUser.setHasDoctorVideo(requestDto.getHasDoctorVideo());
        existingUser.setResidenceAddress(requestDto.getResidenceAddress());
        existingUser.setExperience(requestDto.getExperience());
        existingUser.setExtraActivities(requestDto.getExtraActivities());
        existingUser.setAboutMe(requestDto.getAboutMe());
        existingUser.setLanguageFluency(String.join(",", selectedLanguages.stream().map(String::valueOf).toList()));
        existingUser.setNotificationLanguage(requestDto.getNotificationLanguage() != null ? requestDto.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);
        existingUser.setHospitalId(requestDto.getClassification().equals(String.valueOf(Classification.from_hospital)) ? requestDto.getHospitalId() : 0);
        existingUser.setGender(requestDto.getGender());
        existingUser.setPassingYear(requestDto.getPassingYear());
        existingUser.setUniversityName(requestDto.getUniversityName());
        existingUser.setIsInternational(requestDto.getCountryCode().equals(countryCode) ? YesNo.No : YesNo.Yes);
        existingUser.setClassification(Classification.valueOf(requestDto.getClassification()));

        // Save profile picture if provided
        if (requestDto.getProfilePicture() != null) {
            String filePath = Constants.USER_PROFILE_PICTURE + userId;

            // Delete existing profile if present
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

        // Update the existing user
        existingUser = usersRepository.save(existingUser);

        // Process & update doctor documents
        processAndSaveDoctorDocuments(requestDto.getDocuments(), userId);

        // Process & update hospital merchant number
        processAndUpdateHospitalMerchantNumber(requestDto.getClassification(), requestDto.getCountryCode(), userId, requestDto.getMerchantNumber());

        // Process & update charges
        processAndUpdateCharges(requestDto, userId);

        // Process & update doctor specializations
        processAndUpdateDoctorSpecialization(selectedSpecializations, existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.USER_UPDATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    // Handle selected languages
    private List<Integer> getSelectedLanguageFluency(List<Integer> languageFluencyList, Locale locale) {
        Map<Integer, String> languageList = globalService.getLanguageList(locale);
        List<Integer> selectedLanguageList = new ArrayList<>();
        if (languageFluencyList != null) {
            for (Integer langId : languageFluencyList) {
                if (languageList.containsKey(langId)) {
                    selectedLanguageList.add(langId);
                }
            }
        }
        return selectedLanguageList;
    }

    // Handle selected specialization
    private List<Integer> getSelectedSpecialization(List<Integer> specializationList, Locale locale) {
        Map<Integer, String> specializationMap = specializationService.getSpecializationList(locale);
        List<Integer> selectedSpecializationList = new ArrayList<>();
        if (specializationList != null) {
            for (Integer specId : specializationList) {
                if (specializationMap.containsKey(specId)) {
                    selectedSpecializationList.add(specId);
                }
            }
        }
        return selectedSpecializationList;
    }

    @Transactional
    private void processAndSaveDoctorDocuments(List<Map<String, Object>> documentList, Integer userId) throws IOException {
        try {
            // Save doctor documents if provided
            if (documentList != null) {
                for (Map<String, Object> documentMap : documentList) {
                    String documentName = (String) documentMap.get("documentName"); // Key representing the document name or type
                    MultipartFile document = (MultipartFile) documentMap.get("file"); // The actual file

                    if (document != null && !document.isEmpty()) {
                        String filePath = Constants.DOCTOR_DOCUMENT_PATH + userId;

                        // Extract the file extension
                        String extension = fileService.getFileExtension(Objects.requireNonNull(document.getOriginalFilename()));

                        // Generate a unique file name
                        String fileName = UUID.randomUUID() + "." + extension;

                        // Save the file
                        fileService.saveFile(document, filePath, fileName);

                        // Create and populate the DoctorDocument object
                        DoctorDocument doctorDocument = new DoctorDocument();
                        doctorDocument.setUserId(userId);
                        doctorDocument.setDocumentFileName(fileName);
                        doctorDocument.setDocumentName(documentName); // Use the key as the document name
                        doctorDocument.setCreatedAt(LocalDateTime.now());
                        doctorDocument.setStatus(DocumentStatus.Active);

                        // Save doctor document
                        doctorDocumentRepository.save(doctorDocument);
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("failed to save doctor documents: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    private void processAndSaveHospitalMerchantNumber(String classification, String countryCode, Integer userId, String merchantNumber) {
      try {
          if (classification.equals(String.valueOf(Classification.individual)) && countryCode.equals(this.countryCode)) {
              HospitalMerchantNumber hospitalMerchantNumber = new HospitalMerchantNumber();
              hospitalMerchantNumber.setUserId(userId);
              hospitalMerchantNumber.setMerchantNumber(merchantNumber);
              hospitalMerchantNumberRepository.save(hospitalMerchantNumber);
          }
      } catch (Exception ex) {
          throw new RuntimeException("failed to save hospital merchant number details: " + ex.getMessage(), ex);
      }
    }

    @Transactional
    private void processAndUpdateHospitalMerchantNumber(String classification, String countryCode, Integer userId, String merchantNumber) {
        try {
            if (classification.equals(String.valueOf(Classification.individual)) && countryCode.equals(this.countryCode)) {
                HospitalMerchantNumber hospitalMerchantNumber = hospitalMerchantNumberRepository.findByUserId(userId).orElse(null);
                if (hospitalMerchantNumber != null) {
                    hospitalMerchantNumber.setMerchantNumber(merchantNumber); //TODO: Merchant Number Unique?
                    hospitalMerchantNumberRepository.save(hospitalMerchantNumber);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("failed to update hospital merchant number details: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    private void processAndSaveCharges(DoctorUserRequestDto userData, Integer doctorId) {
        try {
            // Iterate through fee types and process charges
            for (FeeTypeNew feeType : FeeTypeNew.values()) {
                // Create a new charge entity
                Charges charges = new Charges();

                // Fetch commission type for the current fee type
                GlobalConfiguration commissionConfig = globalConfigurationRepository.findByKey(feeType.toString().toUpperCase() + "_COMMISSION_TYPE").orElse(null);

                // Set commissions & consultations fess
                Float adminCommission = null;
                Float consultationFees = null;
                Float finalConsultationFees = null;

                if (feeType.equals(FeeTypeNew.visit)) {
                    adminCommission = userData.getVisitAdminCommission();
                    consultationFees = userData.getVisitConsultationFee();
                    finalConsultationFees = userData.getVisitFinalConsultationFee();
                } else if (feeType.equals(FeeTypeNew.call)) {
                    adminCommission = userData.getCallAdminCommission();
                    consultationFees = userData.getCallConsultationFee();
                    finalConsultationFees = userData.getCallFinalConsultationFee();
                }

                if (finalConsultationFees != null) {
                    // Set charges fields value
                    charges.setFeeType(FeeType.valueOf(String.valueOf(feeType)));
                    charges.setFinalConsultationFees(finalConsultationFees);
                    charges.setCommission(adminCommission);
                    charges.setCommissionType(CommissionType.valueOf(commissionConfig.getValue()));
                    charges.setConsultationFees(consultationFees);
                    charges.setUserId(doctorId);
                    charges.setCreatedAt(new Date());

                    chargesRepository.save(charges);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("failed to save charges details: " + ex.getMessage(), ex);
        }

    }

    @Transactional
    private void processAndUpdateCharges(DoctorUserUpdateRequestDto userData, Integer doctorId) {
        try {
            // Iterate through fee types and process charges
            for (FeeTypeNew feeType : FeeTypeNew.values()) {
                // Fetch existing charges by fee type and user ID
                Charges charges = chargesRepository.findByUserIdAndFeeType(doctorId, FeeType.valueOf(feeType.name()));

                // Set commissions and consultation fees based on the fee type
                Float adminCommission = null;
                Float consultationFees = null;
                Float finalConsultationFees = null;

                if (feeType.equals(FeeTypeNew.visit)) {
                    adminCommission = userData.getVisitAdminCommission();
                    consultationFees = userData.getVisitConsultationFee();
                    finalConsultationFees = userData.getVisitFinalConsultationFee();
                } else if (feeType.equals(FeeTypeNew.call)) {
                    adminCommission = userData.getCallAdminCommission();
                    consultationFees = userData.getCallConsultationFee();
                    finalConsultationFees = userData.getCallFinalConsultationFee();
                }

                // Process only if consultation fees are provided
                if (finalConsultationFees != null) {
                    if (charges == null) {
                        // Fetch commission type for the current fee type
                        GlobalConfiguration commissionConfig = globalConfigurationRepository.findByKey(feeType.toString().toUpperCase() + "_COMMISSION_TYPE").orElse(null);

                        // If no existing charges found, create a new one
                        charges = new Charges();
                        charges.setUserId(doctorId);
                        charges.setFeeType(FeeType.valueOf(feeType.name()));
                        charges.setCommissionType(CommissionType.valueOf(commissionConfig.getValue()));
                        charges.setCreatedAt(new Date());
                    }

                    // Update charges fields
                    charges.setFinalConsultationFees(finalConsultationFees);
                    charges.setCommission(adminCommission);
                    charges.setConsultationFees(consultationFees);

                    // Save updated or new charges
                    chargesRepository.save(charges);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update charges details: " + ex.getMessage(), ex);
        }
    }


    @Transactional
    private void processAndSaveDoctorSpecialization(List<Integer> specializationList, Users user) {
        try {
            if (!specializationList.isEmpty()) {
                // Iterate through specializations process doctor specializations
                for (Integer id: specializationList) {
                    DoctorSpecialization doctorSpecialization = new DoctorSpecialization();

                    Specialization specialization = specializationRepository.findById(id).orElse(null);

                    // Set doctor specialization fields value
                    doctorSpecialization.setUserId(user);
                    doctorSpecialization.setSpecializationId(specialization);
                    doctorSpecialization.setCreatedAt(LocalDateTime.now());

                    doctorSpecializationRepository.save(doctorSpecialization);

                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("failed to save specialization details: " + ex.getMessage(), ex);
        }
    }


    @Transactional
    private void processAndUpdateDoctorSpecialization(List<Integer> specializationList, Users user) {
        try {
            // Delete all existing specializations for the doctor
            doctorSpecializationRepository.deleteByUserId(user.getUserId());

            if (!specializationList.isEmpty()) {
                // Iterate through specializations process doctor specializations
                for (Integer id: specializationList) {
                    DoctorSpecialization doctorSpecialization = new DoctorSpecialization();

                    Specialization specialization = specializationRepository.findById(id).orElse(null);

                    // Set doctor specialization fields value
                    doctorSpecialization.setUserId(user);
                    doctorSpecialization.setSpecializationId(specialization);
                    doctorSpecialization.setCreatedAt(LocalDateTime.now());
                    doctorSpecialization.setUpdatedAt(LocalDateTime.now());

                    doctorSpecializationRepository.save(doctorSpecialization);

                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("failed to update specialization details: " + ex.getMessage(), ex);
        }
    }


    @Transactional
    private void assignRole(Integer userId, String roleType) {
        try {
            // Delete existing roles
            authAssignmentRepository.deleteByUserId(String.valueOf(userId));

            // Insert new role
            authAssignmentRepository.insertRole(roleType, String.valueOf(userId), (int) (System.currentTimeMillis() / 1000));

        } catch (Exception ex) {
            throw new RuntimeException("failed to assign role to user: " + ex.getMessage(), ex);
        }
    }


    @Transactional
    public Object getDoctorAvailability(Locale locale, Integer doctorId) {

        if(doctorId == null) return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));

        Optional<Users> doctor = usersRepository.findByUserIdAndType(doctorId, UserType.Doctor);
        if(doctor.isEmpty()){
            return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        SlotType slotType = slotTypeRepository.findByStatus(SlotStatus.active);
        int slotId = slotType == null ? 4 : slotType.getId();
        List<Object[]> resultList = doctorAvailabilityRepository.findByAvailabilityByDoctorId(doctor.get().getUserId(), slotId);

        Map<String, List<DoctorAvailabilityResponseDto>> dtoList = mapResultListIntoDoctorAvailabilityResponseDto(resultList);

        return new Response(Status.SUCCESS, Constants.CODE_1, messageSource.getMessage(DOCTOR_AVAILABILITY_FOUND, null, locale), dtoList);
    }

    private Map<String, List<DoctorAvailabilityResponseDto>> mapResultListIntoDoctorAvailabilityResponseDto(List<Object[]> resultList) {
        Map<String, List<DoctorAvailabilityResponseDto>> responseMap = new HashMap<>();
        List<String> weekDays = Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");

        resultList.forEach(row ->
                responseMap.computeIfAbsent(((String) row[1]).toLowerCase(), k -> new ArrayList<>())
                        .add(new DoctorAvailabilityResponseDto((Integer) row[0], (String) row[2], (Integer) row[3], (Time) row[4]))
        );

        weekDays.forEach(day -> responseMap.putIfAbsent(day, new ArrayList<>()));

        return responseMap;
    }

    public Object setDoctorAvailability(Locale locale, SetDoctorAvailabilityRequestDto requestDto) {
        if(requestDto.getDoctorId() == null)
            return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));

        Optional<Users> doctor = usersRepository.findByUserIdAndType(requestDto.getDoctorId(), UserType.Doctor);
        if(doctor.isEmpty())
            return new Response(Status.FAILED, Constants.CODE_O, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));

        if(requestDto.getSlots() != null && !requestDto.getSlots().isEmpty()) {
            //delete older records if present from Doctor Availability table
            List<DoctorAvailability> doctorAvailabilities = doctorAvailabilityRepository.findByDoctorId(doctor.get());
            if(!doctorAvailabilities.isEmpty()) {
                doctorAvailabilities.forEach(ele -> doctorAvailabilityRepository.delete(ele));
            }

            // Iterate over the map
            for (Map.Entry<String, List<String>> entry : requestDto.getSlots().entrySet()) {
                String weekday = entry.getKey();
                List<String> slots = entry.getValue();

                //now create new entries in doctor availability tables
                if (!slots.isEmpty()) {
                    saveDataIntoDoctorAvailabilityTable(slots, weekday, doctor.get());
                }
            }
        }

        return new Response(Status.SUCCESS, Constants.CODE_1, messageSource.getMessage(Messages.SLOTS_SAVED_SUCCESSFULLY, null, locale));
    }

    private void saveDataIntoDoctorAvailabilityTable(List<String> slots, String weekDay, Users doctor) {
        SlotType slotType = slotTypeRepository.findByStatus(SlotStatus.active);
        int slotId = slotType == null ? 4 : slotType.getId();

        List<SlotMaster> masterList = slotMasterRepository.findBySlotTypeIdAndSlotDayAndSlotTimeIn(slotId, weekDay, slots);

        if(masterList.isEmpty()) return;

        List<DoctorAvailability> availabilities = masterList.stream()
                .map(slotMaster -> {
                    DoctorAvailability availability = new DoctorAvailability();
                    availability.setDoctorId(doctor);
                    availability.setSlotTypeId(slotId);
                    availability.setSlotId(slotMaster);
                    availability.setDay(weekDay);
                    availability.setCreatedAt(LocalDateTime.now());
                    availability.setUpdatedAt(LocalDateTime.now());
                    return availability;
                })
                .collect(Collectors.toList());

        doctorAvailabilityRepository.saveAll(availabilities);
    }
  
    public String cleanAndAddPrefix(String firstName) {
        // Remove any occurrences of "Dr. " or "Dr"
        if (firstName.startsWith("Dr. ")) {
            firstName = firstName.substring(4).trim(); // Remove "Dr. " (4 characters)
        } else if (firstName.startsWith("Dr ")) {
            firstName = firstName.substring(3).trim(); // Remove "Dr " (3 characters)
        } else if (firstName.startsWith("Dr")) {
            firstName = firstName.substring(2).trim(); // Remove "Dr" (2 characters)
        }

        // Add the correct "Dr. " prefix
        return "Dr. " + firstName.trim();
    }

    public Object updateDoctorUserStatus(Locale locale, Integer userId, String status) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingDoctorUser = usersRepository.findByUserIdAndType(userId, UserType.Doctor);
        if (existingDoctorUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingDoctorUser.get();

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

    public Object getDoctorUser(Locale locale, Integer userId) {
        Response response = new Response();

        // Find the user
        Optional<Users> existingMarketingUser = usersRepository.findByUserIdAndType(userId, UserType.Doctor);
        if (existingMarketingUser.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        Users existingUser = existingMarketingUser.get();

        // Construct users entity to marketing user response dto
        DoctorUserResponseDto marketingUserResponseDto = convertToDoctorUserResponseDto(existingUser);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setData(marketingUserResponseDto);
        response.setMessage(messageSource.getMessage(Messages.USER_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;

    }

    public Object deleteDoctorDocument(Locale locale, Integer userId, String documentFileName) throws IOException {
        Response response = new Response();

        // Get actual document name
        String fileName = extractFileName(documentFileName);

        // Find the document
        Optional<DoctorDocument> existingDoctorDocument = doctorDocumentRepository.findByUserIdAndDocumentFileName(userId, fileName);
        if (existingDoctorDocument.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.DOCUMENT_NOT_FOUND, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        String directoryPath = Constants.DOCTOR_DOCUMENT_PATH + userId;

        // Delete the file form server
        fileService.deleteFile(directoryPath, fileName);

        // Delete entry from doctor document table
        DoctorDocument existingDocument = existingDoctorDocument.get();
        doctorDocumentRepository.delete(existingDocument);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.DOCUMENT_DELETED_SUCCESSFULLY, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;

    }

    public String extractFileName(String input) {
        return input.substring(input.lastIndexOf("/") + 1); // Extract substring after the last '/'
    }

    private DoctorUserResponseDto convertToDoctorUserResponseDto(Users user) {
        DoctorUserResponseDto doctorUserResponseDto = new DoctorUserResponseDto();

        // Basic information
        doctorUserResponseDto.setUserId(user.getUserId());
        doctorUserResponseDto.setFirstName(user.getFirstName());
        doctorUserResponseDto.setLastName(user.getLastName());
        doctorUserResponseDto.setEmail(user.getEmail());
        doctorUserResponseDto.setContactNumber(user.getContactNumber());
        doctorUserResponseDto.setGender(user.getGender());

        // Address and location
        doctorUserResponseDto.setCountryId(user.getCountry().getId());
        doctorUserResponseDto.setProvinceId(user.getState());
        doctorUserResponseDto.setCityId(user.getCity());
        doctorUserResponseDto.setResidenceAddress(user.getResidenceAddress());
        doctorUserResponseDto.setHospitalAddress(user.getHospitalAddress());
        doctorUserResponseDto.setCountryCode(user.getCountryCode());

        // Professional details
        doctorUserResponseDto.setExperience(user.getExperience());
        doctorUserResponseDto.setUniversityName(user.getUniversityName());
        doctorUserResponseDto.setPassingYear(user.getPassingYear());
        doctorUserResponseDto.setAboutMe(user.getAboutMe());
        doctorUserResponseDto.setDoctorClassification(user.getDoctorClassification());
        doctorUserResponseDto.setClassification(String.valueOf(user.getClassification()));

        // Consultation fees and commissions
        List<Charges> chargesList = chargesRepository.findByUserId(user.getUserId());

        if (chargesList != null && !chargesList.isEmpty()) {
            chargesList.forEach(charge -> {
                if (charge.getFeeType() == FeeType.valueOf(FeeTypeNew.visit.name())) {
                    doctorUserResponseDto.setVisitConsultationFee(charge.getConsultationFees());
                    doctorUserResponseDto.setVisitAdminCommission(charge.getCommission());
                    doctorUserResponseDto.setVisitFinalConsultationFee(charge.getFinalConsultationFees());
                } else if (charge.getFeeType() == FeeType.valueOf(FeeTypeNew.call.name())) {
                    doctorUserResponseDto.setCallConsultationFee(charge.getConsultationFees());
                    doctorUserResponseDto.setCallAdminCommission(charge.getCommission());
                    doctorUserResponseDto.setCallFinalConsultationFee(charge.getFinalConsultationFees());
                }
            });
        } else {
            doctorUserResponseDto.setVisitConsultationFee(0.0F);
            doctorUserResponseDto.setVisitAdminCommission(0.0F);
            doctorUserResponseDto.setVisitFinalConsultationFee(0.0F);
            doctorUserResponseDto.setCallConsultationFee(0.0F);
            doctorUserResponseDto.setCallAdminCommission(0.0F);
            doctorUserResponseDto.setCallFinalConsultationFee(0.0F);
        }

        // Notifications and language preferences
        doctorUserResponseDto.setNotificationLanguage(user.getNotificationLanguage());
        doctorUserResponseDto.setLanguagesFluency(Collections.singletonList(user.getLanguageFluency()));

        // Fetch specialization list
        List<DoctorSpecialization> specializationList = doctorSpecializationRepository.findByUserId(user.getUserId());

        // Extract specialization IDs
        if (specializationList != null && !specializationList.isEmpty()) {
            List<Integer> specializationIds = specializationList.stream()
                    .map(spec -> spec.getSpecializationId().getId())
                    .collect(Collectors.toList());
            doctorUserResponseDto.setSpecializationList(specializationIds);
        } else {
            doctorUserResponseDto.setSpecializationList(Collections.emptyList());
        }

        // Profile and documents
        List<DoctorDocument> documents = doctorDocumentRepository.findByUserIdAndStatus(user.getUserId(), DocumentStatus.Active);

        if (documents != null && !documents.isEmpty()) {
            String doctorDocumentFilePath = Constants.DOCTOR_DOCUMENT_PATH + user.getUserId() + "/";
            List<Map<String, String>> documentMaps = documents.stream()
                    .map(doc -> {
                        Map<String, String> documentMap = new HashMap<>();
                        documentMap.put("documentFileName", doctorDocumentFilePath + doc.getDocumentFileName());
                        documentMap.put("documentName", doc.getDocumentName());
                        return documentMap;
                    })
                    .collect(Collectors.toList());
            doctorUserResponseDto.setDocuments(documentMaps);
        } else {
            doctorUserResponseDto.setDocuments(Collections.emptyList());
        }

        String profilePictureFilePath = Constants.USER_PROFILE_PICTURE + user.getUserId() + "/";
        doctorUserResponseDto.setProfilePicture(profilePictureFilePath + user.getProfilePicture());

        // Other details
        doctorUserResponseDto.setHasDoctorVideo(user.getHasDoctorVideo());
        doctorUserResponseDto.setExtraActivities(user.getExtraActivities());
        doctorUserResponseDto.setHospitalId(user.getHospitalId());

        // Merchant number
        HospitalMerchantNumber hospitalMerchantNumber = hospitalMerchantNumberRepository.findByUserId(user.getUserId()).orElse(null);
        if (hospitalMerchantNumber != null) {
            doctorUserResponseDto.setMerchantNumber(hospitalMerchantNumber.getMerchantNumber());
        }

        return doctorUserResponseDto;
    }

}
