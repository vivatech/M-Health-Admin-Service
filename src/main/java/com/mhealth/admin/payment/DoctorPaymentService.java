package com.mhealth.admin.payment;

import com.mhealth.admin.config.Utility;
import com.mhealth.admin.dto.enums.PaymentStatus;
import com.mhealth.admin.dto.request.DoctorPaymentRequest;
import com.mhealth.admin.dto.response.DoctorPaymentResponse;
import com.mhealth.admin.model.Consultation;
import com.mhealth.admin.model.DoctorPayment;
import com.mhealth.admin.model.Orders;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.DoctorPaymentRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DoctorPaymentService {
    private final DoctorPaymentRepository doctorPaymentRepository;

    public DoctorPaymentService(DoctorPaymentRepository doctorPaymentRepository) {
        this.doctorPaymentRepository = doctorPaymentRepository;
    }


    public Specification<Orders> filterByParams(DoctorPaymentRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by consultationDate
            if (request.getConsultationDate() != null) {
                Date startDate = Utility.startDate(request.getConsultationDate());
                Date endDate = Utility.endDate(request.getConsultationDate());
                predicates.add(criteriaBuilder.between(root.get("createdAt"), startDate, endDate));
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

    public DoctorPaymentResponse mapOrdersEntityToDto(Orders order) {
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
        return dto;
    }
}
