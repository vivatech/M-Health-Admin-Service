package com.mhealth.admin.payment;

import com.mhealth.admin.dto.request.DoctorPaymentRequest;
import com.mhealth.admin.model.GroupOrderConsultation;
import com.mhealth.admin.model.Users;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class InternationalDoctorPaymentService {

    public Specification<GroupOrderConsultation> filterByParams(DoctorPaymentRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getDoctorName() != null) {
                Join<GroupOrderConsultation, Users> doctor = root.join("userId");

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
            if (request.getContactNumber() != null) {
                Join<GroupOrderConsultation, Users> doctor = root.join("userId");
                Predicate contactPredicate = criteriaBuilder.equal(doctor.get("contactNumber"), request.getContactNumber());
                predicates.add(contactPredicate);
            }
            if (request.getEmail() != null) {
                Join<GroupOrderConsultation, Users> doctor = root.join("userId");
                Predicate emailPredicate = criteriaBuilder.equal(doctor.get("email"), request.getEmail());
                predicates.add(emailPredicate);
            }
            if (request.getStatus() != null) {
                Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), GroupOrderConsultation.Status.valueOf(request.getStatus()));
                predicates.add(statusPredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
