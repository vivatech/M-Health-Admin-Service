package com.mhealth.admin.payment;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.PaymentStatus;
import com.mhealth.admin.dto.request.DoctorPaymentRequest;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.B2CPaymentDto;
import com.mhealth.admin.dto.response.DoctorPaymentResponse;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Consultation;
import com.mhealth.admin.model.DoctorPayment;
import com.mhealth.admin.model.Orders;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.DoctorPaymentRepository;
import com.mhealth.admin.repository.OrdersRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@Tag(name = "Admin Module Payment Operations", description = "APIs for Payment Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${m-health.country}")
    private String mHealthCountry;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private DoctorPaymentRepository doctorPaymentRepository;

    @Operation(summary = "Create a B2C payment by add msisdn and amount in the request body", responses = {
            @ApiResponse(responseCode = "200", description = "Payment Success"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid"),
            @ApiResponse(responseCode = "405", description = "Payment is already in progress")
    })
    @Transactional
    @PostMapping("/send-b2c")
    public ResponseEntity<Response> createPayment(
            @RequestBody B2CPaymentDto request,
            @RequestHeader(name = "country-code", required = false, defaultValue = Constants.DEFAULT_COUNTRY) String countryCode,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        if (StringUtils.isEmpty(request.getMsisdn()) || request.getAmount() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(Status.FAILED, Constants.INTERNAL_SERVER_ERROR_CODE, "Request body is invalid"));
        }
        return ResponseEntity.ok(paymentService.sendPayment(request.getMsisdn(), request.getAmount(), countryCode.toUpperCase()));
    }

    @PostMapping("/refund-payment")
    public ResponseEntity<Response> refundPayment(@RequestBody B2CPaymentDto request) {
        return ResponseEntity.ok(paymentService.refundPayment(request.getMsisdn(), request.getTransactionId(), mHealthCountry));
    }

    @PostMapping("/doctor/payment")
    public ResponseEntity<PaginationResponse<DoctorPaymentResponse>> doctorPaymentList(@RequestBody DoctorPaymentRequest request,
                                                      @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        Integer size = request.getSize() == null ? Constants.DEFAULT_PAGE_SIZE : request.getSize();
        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        Pageable pageable = PageRequest.of(pageNumber, size);
        Specification<Orders> specification = filterByParams(request);
        Page<Orders> ordersPage = ordersRepository.findAll(specification, pageable);
        List<DoctorPaymentResponse> dtoList = new ArrayList<>();
        for (Orders order : ordersPage) {
            DoctorPaymentResponse dto = new DoctorPaymentResponse();
            dto.setCaseId(order.getCaseId().getCaseId());
            dto.setDoctorId(order.getDoctorId().getUserId());
            dto.setDoctorName(order.getDoctorId().getFullName());
            dto.setPatientId(order.getPatientId().getUserId());
            dto.setPatientName(order.getPatientId().getFullName());
            dto.setClinicId(order.getDoctorId().getHospitalId());
            dto.setClinicName(order.getDoctorId().getClinicName());
            dto.setConsultationDate(order.getCreatedAt());
            dto.setAmount(order.getAmount());
            dto.setCommission(order.getCommission());
            dto.setDoctorAmount(order.getDoctorAmount());
            dto.setPaymentAmount(order.getDoctorAmount());
            dto.setConsultationStatus(order.getCaseId().getConsultStatus().toString());
            DoctorPayment doctorPayment = doctorPaymentRepository.findByCaseId(order.getCaseId().getCaseId());
            dto.setPaymentStatus(doctorPayment != null ? doctorPayment.getPaymentStatus().toString() : PaymentStatus.Pending.toString());
            dtoList.add(dto);

        }
        return ResponseEntity.ok(new PaginationResponse<>(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.DOCTOR_ADD_PAYMENT_FETCHED_SUCCESS, null, locale),
                dtoList, ordersPage.getTotalElements(), (long) ordersPage.getSize(), (long) ordersPage.getNumber()));
    }

    public static Specification<Orders> filterByParams(DoctorPaymentRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by consultationDate
            if (request.getConsultationDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("createdAt"), request.getConsultationDate()));
            }

            // Filter by caseId
            if (request.getCaseId() != null) {
                //Use join to get caseId from consultation table
                Join<Orders, Consultation> consultation = root.join("caseId");
                predicates.add(criteriaBuilder.equal(consultation.get("caseId"), request.getCaseId()));
            }

            // Filter by fullName and contactNumber
            if (request.getPatientName() != null) {
                Join<Orders, Users> patient = root.join("patientId");

                Predicate namePredicate = criteriaBuilder.or(
                        criteriaBuilder.like(patient.get("firstName"), "%" + request.getPatientName() + "%"),
                        criteriaBuilder.like(patient.get("lastName"), "%" + request.getPatientName() + "%"),
                        criteriaBuilder.like(criteriaBuilder.concat(
                                        criteriaBuilder.concat(patient.get("firstName"), " "),
                                        patient.get("lastName")),
                                "%" + request.getPatientName() + "%"
                        )
                );
                predicates.add(namePredicate);
            }

            if (request.getDoctorName() != null) {
                Join<Orders, Users> doctor = root.join("doctorId");

                Predicate namePredicate = criteriaBuilder.or(
                        criteriaBuilder.like(doctor.get("firstName"), "%" + request.getDoctorName() + "%"),
                        criteriaBuilder.like(doctor.get("lastName"), "%" + request.getDoctorName() + "%"),
                        criteriaBuilder.like(criteriaBuilder.concat(
                                        criteriaBuilder.concat(doctor.get("firstName"), " "),
                                        doctor.get("lastName")),
                                "%" + request.getDoctorName() + "%"
                        )
                );
                predicates.add(namePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
