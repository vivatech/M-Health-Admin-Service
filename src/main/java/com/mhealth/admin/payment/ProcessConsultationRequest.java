package com.mhealth.admin.payment;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.*;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.exception.AdminModuleExceptionHandler;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import com.mhealth.admin.sms.SMSApiService;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.mhealth.admin.constants.Constants.*;

@Service
@Slf4j
public class ProcessConsultationRequest {

    @Value("${vc.userName}")
    private String userName;
    @Value("${vc.password}")
    private String password;
    @Value("${vc.title}")
    private String title;

    @Autowired
    private PaymentService paymentService;

    public static final String VideoURL = "https://api.cluster.dyte.in/v2/meetings/";
    public static final String EVC_PLUS = "EVCPLUS";

    @Autowired
    private MessageSource messageSource;

    @Value("${app.sms.sent}")
    private boolean smsSent;

    @Value("${m-health.country.dialing.code}")
    private String countryCode;

    @Value("${m-health.country}")
    private String mHealthCountry;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private SlotMasterRepository slotMasterRepository;
    @Autowired
    private ChargesRepository chargesRepository;
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired
    private SMSApiService smsApiService;



    @Transactional
    public Response paymentGateway(@Valid ConsultationPaymentDto request, Locale locale) {

        Users patient = usersRepository.findById(request.getPatientId()).orElseThrow(() -> new AdminModuleExceptionHandler("Patient not found"));

        // consultation

        Users doctor = usersRepository.findById(request.getDoctorId()).orElseThrow(() -> new AdminModuleExceptionHandler("Doctor not found"));
        //check whether user already booked that slots in that given time or not
        SlotMaster slotMaster = slotMasterRepository.findById(request.getSlotId()).orElseThrow(() -> new AdminModuleExceptionHandler("Slot not found"));

        Users submittedBy = usersRepository.findById(request.getSubmittedBy()).orElseThrow(() -> new AdminModuleExceptionHandler("Submitted by not found"));
        //Charges
        FeeType type = (request.getConsultType().equals(ConsultationPaymentDto.ConsultType.CLINIC_VISIT)) ? FeeType.visit : FeeType.call;
        Charges charges = chargesRepository.findByUserIdAndFeeType(doctor.getUserId(), type);
        float amount = 0.0f;
        if(charges != null) amount = charges.getFinalConsultationFees();

        //for payment calculation of money
        float finalAmount = amount;

        //new consultation creating
        Consultation consultation = saveConsultation(patient, doctor, slotMaster, request.getConsultationDate(), request.getConsultType(), request.getConsultationType(), submittedBy);

        //Save order into order table
        Orders orders = saveOrders(consultation, patient, doctor, charges, LocalDateTime.now(), request.getCurrency(), finalAmount);

        // method for dyte meeting
        saveDyteMeeting(consultation);

        String transactionType = (request.getConsultType() == ConsultationPaymentDto.ConsultType.VIDEO) ? Paid_Consultation_Video : Paid_Consultation_Clinic_Visit;

        //Save to wallet transaction table
        WalletTransaction transaction = saveWalletTransaction(patient, orders, transactionType, Service_Type_Consultation, request.getCurrency());

        PaymentDto paymentDto = PaymentDto.builder()
                .paymentNumber(request.getPaymentMsisdn())
                .amount((double) finalAmount)
                .userId(patient.getUserId())
                .transactionType(PaymentTypes.C2B)
                .build();

        Response consultationResponse = new Response(Status.SUCCESS, Constants.SUCCESS_CODE, "Consultation booked success");

        //Process payment
        if (request.getConsultationType().equals(ConsultationType.Paid)) {
            Response response = paymentService.sendPayment(paymentDto, request.getCountry());
            consultationResponse = createConsultationTransactionPaid(response, transaction, orders, consultation);
        } else {
            consultationResponse = createConsultationTransactionFree(transaction, orders, consultation);
            consultationResponse.setMessage("Consultation booked success");
        }

        if (consultationResponse.getStatus().equals(Status.SUCCESS)) {
            //notification messages
            String m, m1, m2;
            //Send the notification message to patient (m), doctor (m1) and hospital (m2) if doctor is associated to hospital
            m = messageSource.getMessage(Messages.REMINDER_SMS_FOR_TELEPHONE_VISIT, new Object[]{
                    patient.getFullName(), doctor.getFullName(), consultation.getConsultationDate(), slotMaster.getSlotStartTime()}, locale);
            sendPaymentNotification(patient.getContactNumber(), m, request.getCountry());
            m1 = messageSource.getMessage(Messages.REMINDER_SMS_FOR_TELEPHONE_VISIT_DOCTOR,
                    new Object[] {doctor.getFullName(), patient.getFullName(), consultation.getConsultationDate() + ", " + slotMaster.getSlotStartTime()},
                    locale);
            sendPaymentNotification(doctor.getContactNumber(), m1, request.getCountry());
            Users hospital = usersRepository.findById(doctor.getHospitalId()).orElse(null);
            if (hospital != null) {
                m2 = messageSource.getMessage(Messages.BOOKING_NOTIFICATION_CLINIC_TO_HOSPITAL,
                        new Object[]{hospital.getClinicName(), patient.getFullName(), doctor.getFullName(), consultation.getConsultationDate() + ", " + slotMaster.getSlotStartTime()},
                        locale);
                sendPaymentNotification(hospital.getContactNumber(), m2, request.getCountry());
            }
        }
        log.info("Consultation booking status {}", consultationResponse.getStatus());
        return consultationResponse;
    }

    private void sendPaymentNotification(String msisdn, String message, String country) {
        if(smsSent){
            String destinationMsisdn = "+" + countryCode + msisdn;
            smsApiService.sendMessage(destinationMsisdn, message, country);
        }
    }

    private Consultation saveConsultation(Users patient, Users doctor, SlotMaster slotMaster, LocalDate consultationDate, ConsultationPaymentDto.ConsultType consultType, ConsultationType consultationType, Users submittedBy) {
        Consultation consultation = new Consultation();
        consultation.setPatientId(patient);
        consultation.setDoctorId(doctor);
        consultation.setConsultationDate(consultationDate);
        consultation.setConsultType(consultType.toString());

        consultation.setSlotId(slotMaster);
        consultation.setMessage("");
        consultation.setRequestType(RequestType.Inprocess);
        consultation.setAddedType(AddedType.Patient);
        consultation.setReportSuggested("0"); //no report suggested by lab right now

        consultation.setConsultationType(consultationType);
        consultation.setPaymentMethod(EVC_PLUS);

        consultation.setAddedBy(submittedBy.getUserId());
        consultation.setConsultStatus(ConsultStatus.pending);
        consultation.setCreatedAt(LocalDateTime.now());
        consultation.setUpdatedAt(LocalDateTime.now());
        consultation.setChannel(Channel.Web);
        return consultationRepository.save(consultation);
    }

    private Orders saveOrders(Consultation consultation, Users patient, Users doctor, Charges charges, LocalDateTime dateTime, String currency, float amount) {
        Orders order = new Orders();

        if(charges != null) {
            order.setCommissionType(charges.getCommissionType());
            order.setCommission(charges.getCommission());
            order.setDoctorAmount(charges.getConsultationFees());
        }
        order.setCaseId(consultation);
        order.setPatientId(patient);
        order.setDoctorId(doctor);
        order.setAmount(amount);
        order.setCouponId(null);
        order.setCurrency(currency);
        order.setCurrencyAmount(amount);
        order.setCreatedAt(dateTime);
        order.setUpdatedAt(dateTime);
        order.setCommissionType(CommissionType.cost);//by-default
        order.setStatus(OrderStatus.Inprogress);
        return ordersRepository.save(order);
    }

    public void saveDyteMeeting(Consultation consultation) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Map<String, Object>> requestEntity = getMapHttpEntity();

        ResponseEntity<Map> response = restTemplate.exchange(VideoURL, HttpMethod.POST, requestEntity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && (Boolean) response.getBody().get("success")) {
            Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("data");

            consultation.setMeetingId((String) responseData.get("id"));
            consultationRepository.save(consultation);
        } else {
            throw new RuntimeException("Failed to create meeting");
        }
    }

    public Response createConsultationTransactionFree(WalletTransaction transaction, Orders order, Consultation consultation){
        transaction.setTransactionId(generateDateTime());
        transaction.setTransactionStatus(Transaction_COMPLETE);
        order.setStatus(OrderStatus.Completed);
        consultation.setRequestType(RequestType.Book);
        ordersRepository.save(order);
        consultationRepository.save(consultation);
        walletTransactionRepository.save(transaction);
        return new Response(Status.SUCCESS, Constants.SUCCESS_CODE, null);
    }

    public Response createConsultationTransactionPaid(Response response, WalletTransaction transaction, Orders order, Consultation consultation) {

        Response res = new Response();

        if(response.getStatus().equals(Status.SUCCESS)) {
            transaction.setTransactionId(generateDateTime());
            transaction.setTransactionStatus(Transaction_COMPLETE);
            order.setStatus(OrderStatus.Completed);
            consultation.setRequestType(RequestType.Book);
            res.setStatus(Status.SUCCESS);
        } else {
            transaction.setTransactionId(generateDateTime());
            transaction.setTransactionStatus(CANCELLED);
            order.setStatus(OrderStatus.Cancelled);
            consultation.setRequestType(RequestType.Cancel);
            consultation.setCancelMessage(response.getMessage());
            res.setStatus(Status.FAILED);
            res.setMessage(response.getMessage());
        }

        ordersRepository.save(order);
        consultationRepository.save(consultation);
        walletTransactionRepository.save(transaction);
        return res;
    }

    private WalletTransaction saveWalletTransaction(Users patient, Orders order, String transactionType, String serviceType, String paymentCurrency) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setPaymentMethod(paymentCurrency);
        transaction.setPatientId(patient);
        transaction.setOrderId(order.getId());
        transaction.setPaymentGatewayType(EVC_PLUS);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(transactionType);
        transaction.setAmount(order.getAmount());
        transaction.setIsDebitCredit(DEBIT);
        transaction.setPayeeId(1); // payee is super admin and his id is 1
        transaction.setPayerId(patient.getUserId());
        transaction.setReferenceNumber(patient.getUserId().toString());  //this is same as payer no

        Users adminContactNumber = usersRepository.findByTypeAndStatus(UserType.Superadmin, StatusAI.A).stream().findFirst().orElseThrow(() -> new AdminModuleExceptionHandler("Admin contact number not found"));
        transaction.setPayeeMobile(adminContactNumber.getContactNumber());
        transaction.setPayerMobile(patient.getContactNumber());

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setRefTransactionId(order.getId().toString());
//        transaction.setPaymentNumber(request.getPaymentNumber());
        //        todo : need to implement mh_wallet
        transaction.setCurrentBalance(0.0F); // by-default
        transaction.setPreviousBalance(0.0f); // by-default
        transaction.setServiceType(serviceType); //Service_Type_Lab_Report
        transaction.setTransactionId(generateDateTime());
        transaction.setTransactionStatus(Transaction_PENDING);

        return transaction;
    }

    String generateDateTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + (int) (Math.random() * 1000);
    }

    private HttpEntity<Map<String, Object>> getMapHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.valueOf(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(userName, password);
//        headers.set("Authorization", "Basic YjY4ZjY2MWUtYmYyMi00YmUwLTg3OWItYWQ5ODYwMGUzOWRhOjdhYzhiYjdmNDllZDQ5ODZmYTQ3");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);
        requestBody.put("record_on_start", false);
        requestBody.put("live_stream_on_start", false);

        return new HttpEntity<>(requestBody, headers);
    }

}


