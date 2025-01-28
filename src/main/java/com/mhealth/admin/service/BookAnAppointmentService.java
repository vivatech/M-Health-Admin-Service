package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.CancelAppointmentRequest;
import com.mhealth.admin.dto.dto.DoctorProfileResponse;
import com.mhealth.admin.dto.dto.LanguageListResponse;
import com.mhealth.admin.dto.enums.OrderStatus;
import com.mhealth.admin.dto.enums.RequestType;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.mhealth.admin.constants.Constants.*;


@Service
public class BookAnAppointmentService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private DoctorSpecializationRepository doctorSpecializationRepository;
    @Autowired
    private ConsultationRepository consultationRepository;
    @Value("${cancel.appointment.difference}")
    private Long timeDifference;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    public Object viewDoctorProfile(Integer doctorId, Locale locale) {
        Users doctor = usersRepository.findByUserIdAndType(doctorId, UserType.Doctor).orElse(null);
        if (doctor == null) {
            return new Response(Status.FAILED, Constants.FORBIDDEN_STATUS_CODE, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        DoctorProfileResponse response = new DoctorProfileResponse();

        String fName = StringUtils.isEmpty(doctor.getFirstName()) ? "" : doctor.getFirstName();
        String lName = StringUtils.isEmpty(doctor.getLastName()) ? "" : doctor.getLastName();
        response.setDoctorName((fName + " " + lName).trim());

        response.setDoctorPicture(StringUtils.isEmpty(doctor.getProfilePicture()) ? null : Constants.USER_PROFILE_PICTURE + doctor.getUserId() + "/" + doctor.getProfilePicture());

        float exp = doctor.getExperience() == null ? 0.00f : doctor.getExperience();
        response.setExperience(exp);

        String countryName = doctor.getCountry() == null ? "" : doctor.getCountry().getName();
        response.setCountry(countryName);

        Users hospital = usersRepository.findByUserIdAndType(doctor.getHospitalId(), UserType.Clinic).orElse(null);
        if (hospital != null) {
            String clinicName = StringUtils.isEmpty(hospital.getClinicName()) ? "" : hospital.getClinicName();
            response.setHospitalNameWithCountry(clinicName + "-" + countryName);
            response.setHospitalName(clinicName);
        }

        if (doctor.getState() != null) {
            stateRepository.findById(doctor.getState()).ifPresent(state -> response.setProvince(state.getName()));
        }

        if (doctor.getCity() != null) {
            cityRepository.findById(doctor.getCity()).ifPresent(city -> response.setCity(city.getName()));
            ;
        }

        List<String> langList = new ArrayList<>();
        if (!doctor.getLanguageFluency().isEmpty()) {
            String[] lang = doctor.getLanguageFluency().split(",");
            for (String language : lang) {
                languageRepository.findById(Integer.valueOf(language)).ifPresent(langName -> langList.add(langName.getName()));
            }
        }
        response.setLanguageSpoken(langList);

        List<String> specializationList = new ArrayList<>(Collections.singletonList(GENERAL_PRACTITIONER));
        if (!doctor.getDoctorClassification().equalsIgnoreCase(GENERAL_PRACTITIONER)) {
            List<DoctorSpecialization> doctorSpecializations = doctorSpecializationRepository.findByUserId(doctorId);
            specializationList = doctorSpecializations.stream().map(ele -> {
                if (locale.getLanguage().equalsIgnoreCase("en")) {
                    return ele.getSpecializationId().getName();
                } else {
                    return ele.getSpecializationId().getNameSl();
                }
            }).distinct().toList();
        }
        response.setSpecializationName(specializationList);

        return response;
    }

    public Object sortByAvailability(Locale locale) {
        String[] list = messageSource.getMessage(Messages.AVAILABILITY_SORT, null, locale).split(",");
        return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.AVAILABLE_LIST_RETRIEVED, null, locale), list);
    }

    public Object getLanguage(Locale locale) {
        List<Language> languageList = languageRepository.findAll();

        if (!languageList.isEmpty()) {
            List<LanguageListResponse> list = languageList.stream().map(item -> new LanguageListResponse(item.getId(), item.getName())).toList();
            return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.LANGUAGE_LIST_RETRIEVED, null, locale), list);
        }
        return new Response(Status.FAILED, Constants.SUCCESS, messageSource.getMessage(Messages.RECORD_NOT_FOUND, null, locale));
    }

    public Object getSortBy(Locale locale) {
        String[] list = messageSource.getMessage(Messages.SORT_BY, null, locale).split(",");
        return new Response(Status.SUCCESS, Constants.SUCCESS, messageSource.getMessage(Messages.SORT_LIST_RETRIEVED, null, locale), list);
    }

    public Object getPaymentMethod(Locale locale) {
        Map<String, List<String>> paymentMethodResponse = new HashMap<>();
        List<String> paymentMethodList = new ArrayList<>();
        List<String> currencyList = new ArrayList<>();

        paymentMethodList.add(Payment_Method_EVC);
        currencyList.add(locale.getLanguage().equalsIgnoreCase(LOCALE_SOMALIA) ? Dollar_Currency : Currency_USD);

        paymentMethodResponse.put("paymentMethods", paymentMethodList);
        paymentMethodResponse.put("currencyOption", currencyList);
        return new Response(Status.SUCCESS, SUCCESS, messageSource.getMessage(Messages.RECORD_FOUND, null, locale), paymentMethodResponse);
    }

    public Object cancelAppointment(CancelAppointmentRequest request, Locale locale) {
        if (request.validate() != null) {
            return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, request.validate());
        }
        Users users = usersRepository.findById(request.getUserId()).orElse(null);
        if (users == null) {
            return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, messageSource.getMessage(Messages.USER_NOT_FOUND, null, locale));
        }
        Consultation consultation = consultationRepository.findById(request.getCaseId()).orElse(null);
        if (consultation == null) {
            return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, messageSource.getMessage(Messages.CONSULTATION_NOT_FOUND, null, locale));
        }

        LocalDateTime dateTime = LocalDateTime.of(consultation.getConsultationDate(), consultation.getSlotId().getSlotStartTime());
        Duration difference = Duration.between(LocalDateTime.now(), dateTime);

        if (difference.toHours() < timeDifference) {
            return new Response(Status.FAILED, FORBIDDEN_STATUS_CODE, messageSource.getMessage(Messages.CANCEL_REQUEST_CANNOT_PROCESSED, null, locale));
        }

        Orders orders = ordersRepository.findByCaseId(consultation);
        orders.setStatus(OrderStatus.Cancelled);
        orders.setUpdatedAt(LocalDateTime.now());

        consultation.setCancelMessage(request.getMessage());
        consultation.setRequestType(RequestType.Cancel);
        consultation.setUpdatedAt(LocalDateTime.now());

        WalletTransaction existTransaction = walletTransactionRepository.findByOrderIdANDServiceType(orders.getId());

        WalletTransaction currentTransaction = createWalletTransactionEntry(orders, existTransaction);

        //TODO : refund should be done from admin to patient wallet through waafi
//        if(payment == success) {
//            //update the status
//        }
        consultationRepository.save(consultation);
        ordersRepository.save(orders);
        currentTransaction.setTransactionStatus(Transaction_COMPLETE);
        walletTransactionRepository.save(currentTransaction);

        return new Response(Status.SUCCESS, SUCCESS, messageSource.getMessage(Messages.CONSULTATION_CANCEL_SUCCESSFULLY, null, locale));
    }

    private WalletTransaction createWalletTransactionEntry(Orders order, WalletTransaction existTransaction) {

        WalletTransaction transaction = new WalletTransaction();
        transaction.setPaymentMethod(existTransaction.getPaymentMethod());
        transaction.setPatientId(order.getPatientId());
        transaction.setOrderId(order.getId());
        transaction.setPaymentGatewayType(existTransaction.getPaymentGatewayType());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(REFUND_TRANSFER);
        transaction.setAmount(order.getAmount());
        transaction.setIsDebitCredit(DEBIT);
        transaction.setPayeeId(order.getPatientId().getUserId()); // payee is Patient id
        transaction.setPayerId(1); //SuperAdmin
        transaction.setReferenceNumber(order.getPatientId().getUserId().toString());  //Since status is same for both case

        Users adminContactNumber = usersRepository.findById(1).orElse(null);
        transaction.setPayeeMobile(String.valueOf(order.getPatientId().getContactNumber()));
        assert adminContactNumber != null;
        transaction.setPayerMobile(String.valueOf(adminContactNumber.getContactNumber()));

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setRefTransactionId(order.getId().toString());

        transaction.setCurrentBalance(0.0F); // by-default
        transaction.setPreviousBalance(0.0f); // by-default
        transaction.setServiceType(Service_Type_Consultation);
        transaction.setTransactionId(generateDateTime());
        transaction.setTransactionStatus(Transaction_PENDING);

        return walletTransactionRepository.save(transaction);

    }
    String generateDateTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + (int) (Math.random() * 1000);
    }
}
