package com.mhealth.admin.service;

import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.ValidateResult;
import com.mhealth.admin.dto.enums.*;
import com.mhealth.admin.dto.request.DoctorUserRequestDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import com.mhealth.admin.sms.SMSApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class DoctorUserService {

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

    @Value("${app.sms.sent}")
    private boolean smsSent;

    @Value("${m-health.country}")
    private String country;

    @Value("${m-health.country.code}")
    private String countryCode;

    @Value("${m-health.project.name}")
    private String projectName;

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

        // Get selected languages & specialization
        List<Integer> selectedLanguages = getSelectedLanguageFluency(requestDto.getLanguagesFluency(), locale);
        List<Integer> selectedSpecializations = getSelectedSpecialization(requestDto.getSpecializations(), locale);

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

        // Get selected country
        Country country = countryRepository.findById(requestDto.getCountryId()).orElse(null);

        // Get default slot
        Integer slotTypeId = slotTypeRepository.findDefaultSlot(SlotStatus.active.name()).orElse(null);

        // Create and persist entities
        Users user = new Users();
        user.setSlotTypeId(slotTypeId);
        user.setType(UserType.Doctor);
        user.setFirstName("Dr. " + requestDto.getFirstName());
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
          throw new RuntimeException("failed to hospital merchant number details: " + ex.getMessage(), ex);
      }
    }

    @Transactional
    private void processAndSaveCharges(DoctorUserRequestDto userData, Integer doctorId) {
        try {
            // Fetch doctor classification charge
            GlobalConfiguration doctorClassificationCharge = globalConfigurationRepository.findByKey(userData.getDoctorClassification().toUpperCase()).orElse(null);

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

}
