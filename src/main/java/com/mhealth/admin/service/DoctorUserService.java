package com.mhealth.admin.service;

import com.mhealth.admin.config.Utility;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.ValidateResult;
import com.mhealth.admin.dto.enums.*;
import com.mhealth.admin.dto.request.DoctorUserRequestDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.exception.AdminModuleExceptionHandler;
import com.mhealth.admin.model.*;
import com.mhealth.admin.model.State;
import com.mhealth.admin.repository.*;
import com.mhealth.admin.sms.SMSApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    public Object createDoctorUser(Locale locale, DoctorUserRequestDto doctorRequest) throws Exception {

        Response response = new Response();

        // Fetch and map data
        Map<Integer, String> languageList = globalService.getLanguageList(locale);
        Map<Integer, String> specializationList = specializationService.getSpecializationList(locale);
        Map<Integer, String> cityList = globalService.getCities(locale);
        Map<String, Object> countryList = globalService.getCountries(locale, null);


        // Handle selected languages and specializations
        List<String> selectedLanguages = new ArrayList<>();
        if (doctorRequest.getLanguagesFluency() != null) {
            for (Integer langId : doctorRequest.getLanguagesFluency()) {
                if (languageList.containsKey(langId)) {
                    selectedLanguages.add(languageList.get(langId));
                }
            }
        }

        List<String> selectedSpecializations = new ArrayList<>();
        if (doctorRequest.getSpecializations() != null) {
            for (Integer specId : doctorRequest.getSpecializations()) {
                if (specializationList.containsKey(specId)) {
                    selectedSpecializations.add(specializationList.get(specId));
                }
            }
        }

        // Validate file uploads
        if (doctorRequest.getProfilePicture() != null) {
            ValidateResult validationResult = fileService.validateFile(locale, doctorRequest.getProfilePicture(), List.of("jpg", "jpeg", "png"), 1_000_000);
            if (!validationResult.isResult()) {
                response.setCode(Constants.CODE_O);
                response.setMessage(validationResult.getError());
                response.setStatus(Status.FAILED);
                return response;
            }
        }

        // Validate file uploads
        if (doctorRequest.getDoctorIdDocument() != null) {
            ValidateResult validationResult = fileService.validateFile(locale, doctorRequest.getDoctorIdDocument(), List.of("jpg", "jpeg", "png"), 1_000_000);
            if (!validationResult.isResult()) {
                response.setCode(Constants.CODE_O);
                response.setMessage(validationResult.getError());
                response.setStatus(Status.FAILED);
                return response;
            }
        }

        if (doctorRequest.getDocuments() != null) {
            for (Map<String, MultipartFile> documentMap : doctorRequest.getDocuments()) {
                for (Map.Entry<String, MultipartFile> entry : documentMap.entrySet()) {
                    MultipartFile document = entry.getValue(); // The actual file
                    ValidateResult validationResult = fileService.validateFile(locale, document, List.of("pdf"), 2_200_000);
                    if (!validationResult.isResult()) {
                        response.setCode(Constants.CODE_O);
                        response.setMessage(validationResult.getError());
                        response.setStatus(Status.FAILED);
                        return response;
                    }
                }
            }
        }

        Country country = countryRepository.findById(doctorRequest.getCountryId()).orElse(null);
        State state = stateRepository.findById(doctorRequest.getStateId()).orElse(null);
        City city = cityRepository.findById(doctorRequest.getCityId()).orElse(null);
        Integer slotTypeId = slotTypeRepository.findDefaultSlot(SlotStatus.active.name()).orElse(null);

        // Create and persist entities
        Users user = new Users();
        user.setSlotTypeId(slotTypeId);
        user.setType(UserType.Doctor);
        user.setFirstName("Dr. " + doctorRequest.getFirstName());
        user.setLastName(doctorRequest.getLastName());
        user.setEmail(doctorRequest.getEmail());
        user.setContactNumber(doctorRequest.getContactNumber());
        user.setPassword(utility.md5Hash(doctorRequest.getPassword()));
        user.setCountry(country);
        user.setDoctorClassification(doctorRequest.getDoctorClassification());
        user.setCountryCode(doctorRequest.getCountryCode());
        user.setState(doctorRequest.getStateId());
        user.setCity(doctorRequest.getCityId());
        user.setHospitalAddress(doctorRequest.getHospitalAddress());
        user.setHasDoctorVideo(doctorRequest.getHasDoctorVideo());
        user.setResidenceAddress(doctorRequest.getResidenceAddress());
        user.setExperience(doctorRequest.getExperience());
        user.setExtraActivities(doctorRequest.getExtraActivities());
        user.setAboutMe(doctorRequest.getAboutMe());
        user.setLanguageFluency(String.join(",", selectedLanguages.stream().map(String::valueOf).toList()));
        user.setNotificationLanguage(doctorRequest.getNotificationLanguage() != null ? doctorRequest.getNotificationLanguage() : Constants.DEFAULT_LANGUAGE);
        user.setHospitalId(doctorRequest.getClassification().equals(String.valueOf(Classification.from_hospital)) ? doctorRequest.getHospitalId() : 0);
        user.setGender(doctorRequest.getGender());
        user.setPassingYear(doctorRequest.getPassingYear());
        user.setUniversityName(doctorRequest.getUniversityName());
        user.setIsInternational(doctorRequest.getCountryCode().equals(countryCode) ? YesNo.No : YesNo.Yes);

        // Save documents if provided
        if (doctorRequest.getProfilePicture() != null) {
            String filePath = Constants.USER_PROFILE_PICTURE + user.getUserId();

            // Extract the file extension
            String extension = fileService.getFileExtension(Objects.requireNonNull(doctorRequest.getProfilePicture().getOriginalFilename()));

            // Generate a random file name
            String fileName = UUID.randomUUID() + "." + extension;

            // Save the file
            fileService.saveFile(doctorRequest.getProfilePicture(), filePath, fileName);

            user.setProfilePicture(fileName);

        }

        if (doctorRequest.getDoctorIdDocument() != null) {
            String filePath = Constants.USER_PROFILE_PICTURE + user.getUserId();

            // Extract the file extension
            String extension = fileService.getFileExtension(Objects.requireNonNull(doctorRequest.getDoctorIdDocument().getOriginalFilename()));

            // Generate a random file name
            String fileName = UUID.randomUUID() + "." + extension;

            // Save the file
            fileService.saveFile(doctorRequest.getDoctorIdDocument(), filePath, fileName);

            //TODO: Set document_id & document_name
        }


        // Save the user
        user = usersRepository.save(user);

        if (doctorRequest.getDocuments() != null) {
            for (Map<String, MultipartFile> documentMap : doctorRequest.getDocuments()) {
                for (Map.Entry<String, MultipartFile> entry : documentMap.entrySet()) {
                    String documentName = entry.getKey(); // Key representing the document name or type
                    MultipartFile document = entry.getValue(); // The actual file

                    if (document != null && !document.isEmpty()) {
                        String filePath = Constants.DOCTOR_DOCUMENT_PATH + user.getUserId();

                        // Extract the file extension
                        String extension = fileService.getFileExtension(Objects.requireNonNull(document.getOriginalFilename()));

                        // Generate a unique file name
                        String fileName = UUID.randomUUID() + "." + extension;

                        // Save the file
                        fileService.saveFile(document, filePath, fileName);

                        // Create and populate the DoctorDocument object
                        DoctorDocument doctorDocument = new DoctorDocument();
                        doctorDocument.setUserId(user.getUserId());
                        doctorDocument.setDocumentFileName(fileName);
                        doctorDocument.setDocumentName(documentName); // Use the key as the document name
                        doctorDocument.setCreatedAt(LocalDateTime.now());
                        doctorDocument.setStatus(DocumentStatus.Active);

                        // Save doctor document
                        doctorDocumentRepository.save(doctorDocument);
                    }
                }
            }
        }


        // Process & save hospital merchant number
        processAndSaveHospitalMerchantNumber(doctorRequest.getClassification(), doctorRequest.getCountryCode(), user.getUserId(), doctorRequest.getMerchantNumber());


        // Process & save charges
        processAndSaveCharges(doctorRequest, user.getUserId());


        // Process & save doctor specializations
        processAndSaveDoctorSpecialization(doctorRequest.getSpecializations(), user);

        // Assign role
        assignRole(user.getUserId(), UserType.Doctor.name());



        // Send SMS
        try {
            locale = Utility.getUserNotificationLanguageLocale(user.getNotificationLanguage(), locale);
            String smsMessage = messageSource.getMessage(Messages.REGISTER_DOCTOR_USER, new Object[]{user.getFirstName() + " " + user.getLastName(), projectName}, locale);
            String smsNumber = "+" + countryCode + doctorRequest.getContactNumber();
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

        return null;
    }

    @Transactional
    private void processAndSaveHospitalMerchantNumber(String classification, String countryCode, Integer userId, String merchantNumber) {
        if (classification.equals(String.valueOf(Classification.individual)) && countryCode.equals(this.countryCode)) {
            HospitalMerchantNumber hospitalMerchantNumber = new HospitalMerchantNumber();
            hospitalMerchantNumber.setUserId(userId);
            hospitalMerchantNumber.setMerchantNumber(merchantNumber);
            hospitalMerchantNumberRepository.save(hospitalMerchantNumber);
        }
    }

    @Transactional
    private void processAndSaveCharges(DoctorUserRequestDto userData, Integer doctorId) {
        // Fetch doctor classification charge
        GlobalConfiguration doctorClassificationCharge = globalConfigurationRepository.findByKey(userData.getDoctorClassification().toUpperCase()).orElse(null);

        if (doctorClassificationCharge == null) {
            throw new AdminModuleExceptionHandler("Invalid doctor classification charge");
        }

        // Iterate through fee types and process charges
        for (FeeTypeNew feeType : FeeTypeNew.values()) {
            // Create a new charge entity
            Charges charges = new Charges();

            // Fetch commission type for the current fee type
            GlobalConfiguration commissionConfig = globalConfigurationRepository.findByKey(feeType.toString().toUpperCase() + "_COMMISSION_TYPE").orElse(null);
            if (commissionConfig == null) {
                throw new AdminModuleExceptionHandler("Invalid commission configuration for fee type: " + feeType);
            }

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
    }

    @Transactional
    private void processAndSaveDoctorSpecialization(List<Integer> specializationList, Users user) {
        if (!specializationList.isEmpty()) {
            // Iterate through specializations process doctor specializations
            for (Integer id: specializationList) {
                DoctorSpecialization doctorSpecialization = new DoctorSpecialization();

                Specialization specialization = specializationRepository.findById(id).orElse(null);

                if (specialization == null) {
                    throw new AdminModuleExceptionHandler("Invalid specialization");
                }

                // Set doctor specialization fields value
                doctorSpecialization.setUserId(user);
                doctorSpecialization.setSpecializationId(specialization);
                doctorSpecialization.setCreatedAt(LocalDateTime.now());

                doctorSpecializationRepository.save(doctorSpecialization);

            }
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
            log.error("exception occurred while assigning role to the user", ex);
            throw new RuntimeException("failed to assign role to user: " + ex.getMessage(), ex);
        }
    }

    }
