package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.OrderStatus;
import com.mhealth.admin.dto.enums.RequestType;
import com.mhealth.admin.model.Consultation;
import com.mhealth.admin.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    @Query("SELECT o FROM Orders o WHERE o.id = ?1")
    Optional<Orders> findById(Integer orderId);

    @Query("Select u from Orders u where u.caseId.consultationDate = ?1 and u.caseId.caseId = ?2 And " +
            "CONCAT(u.doctorId.firstName,' ', u.doctorId.lastName) like %?3% ")
    Page<Orders> findByConsultationDateAndCaseIdAndDoctorName(LocalDate consultationDate, Integer caseId,
                                                              String doctorName, Pageable pageable);

    @Query("Select u from Orders u where u.caseId.consultationDate = ?1 And " +
            "CONCAT(u.doctorId.firstName,' ', u.doctorId.lastName) like %?2% ")
    Page<Orders> findByConsultationDateAndDoctorName(LocalDate consultationDate,
                                                     String doctorName, Pageable pageable);

    @Query("Select u from Orders u where u.caseId.caseId = ?1 And " +
            "CONCAT(u.doctorId.firstName,' ', u.doctorId.lastName) like %?2% ")
    Page<Orders> findByCaseIdAndDoctorName(Integer caseId, String doctorName, Pageable pageable);

    @Query("Select u from Orders u where CONCAT(u.doctorId.firstName,' ', u.doctorId.lastName) like %?1% ")
    Page<Orders> findByDoctorName(String doctorName, Pageable pageable);


    @Query("Select SUM(u.amount) from Orders u where u.status not in ?1")
    Double getTotalConsultationAmount(List<OrderStatus> statuses);

    @Query("SELECT o FROM Orders o " +
            " JOIN o.patientId p " +
            " JOIN o.doctorId d " +
            " JOIN o.caseId c " +
            " WHERE (:patientName IS NULL OR :patientName = '' OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :patientName, '%'))) " +
            " AND (:doctorName IS NULL OR :doctorName = '' OR LOWER(d.firstName) LIKE LOWER(CONCAT('%', :doctorName, '%'))) " +
            " AND (:consultationDate IS NULL OR c.consultationDate = :consultationDate)")
    Page<Orders> searchOrders(String patientName, String doctorName, String consultationDate, Pageable pageable);

    @Query("SELECT o FROM Orders o \n" +
            "JOIN o.patientId p \n" +  // Change from patientId to patient
            "JOIN o.doctorId d \n" +  // Change from doctorId to doctor
            "JOIN o.caseId c \n" +
            "WHERE (:patientName IS NULL OR :patientName = '' \n" +
            "       OR LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :patientName, '%'))) \n" +
            "AND (:doctorName IS NULL OR :doctorName = '' \n" +
            "       OR LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :doctorName, '%'))) \n" +
            "AND (:consultationDate IS NULL OR c.consultationDate = :consultationDate) \n" +
            "AND c.requestType = :requestType \n" +
            "AND c.caseId IS NOT NULL\n")
    Page<Orders> fetchOrders(
            @Param("patientName") String patientName,
            @Param("doctorName") String doctorName,
            @Param("consultationDate") LocalDate consultationDate,
            @Param("requestType") RequestType requestType,
            Pageable pageable);




    Orders findByCaseId(Consultation in);
}