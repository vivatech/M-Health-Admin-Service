package com.mhealth.admin.repository;

import com.mhealth.admin.model.NodLog;
import com.mhealth.admin.model.SystemTransaction;
import com.mhealth.admin.report.controller.dto.PatientTransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SystemTransactionRepository extends JpaRepository<SystemTransaction, Integer> {

    @Query(value = """
    SELECT st.patient_id, p.country_code, p.contact_number, st.channel, st.status, 
           p.first_name, p.last_name, p.residence_address, c.name AS city_name, 
           st.ref_id, st.created_at, st.order_type, p.dob, p.gender, 
           CONCAT(d.first_name, ' ', d.last_name) AS opp_full_name, 
           h.clinic_name, st.transaction_type
    FROM mh_system_transaction st
    LEFT JOIN mh_users p ON p.user_id = st.patient_id
    LEFT JOIN mh_consultation mc ON mc.case_id = st.ref_id
    LEFT JOIN mh_users d ON d.user_id = mc.doctor_id
    LEFT JOIN mh_users h ON d.hospital_id = h.user_id
    LEFT JOIN mh_cities c ON c.id = p.city_id
    WHERE (:transactionType IS NULL OR st.transaction_type = :transactionType)
      AND (:status IS NULL OR st.status = :status)
      AND (:orderType IS NULL OR st.order_type = :orderType)
      AND (:channel IS NULL OR st.channel = :channel)
      AND (:fromDate IS NULL OR st.created_at >= :fromDate)
      AND (:toDate IS NULL OR st.created_at <= :toDate)

    UNION ALL 

    SELECT nl.user_id AS patient_id, u.country_code, u.contact_number, 
           nl.channel, nl.status, u.first_name, u.last_name, u.residence_address, 
           c.name AS city_name, nl.search_id AS ref_id, nl.created_at, nl.order_type, 
           u.dob, u.gender, pn.name AS opp_full_name, '' AS clinic_name, 
           nl.transaction_type
    FROM mh_nod_logs nl
    LEFT JOIN mh_users u ON u.user_id = nl.user_id
    LEFT JOIN mh_dservice_state ds ON nl.search_id = ds.search_id
    LEFT JOIN mh_partner_nurse pn ON pn.id = ds.nurse_id
    LEFT JOIN mh_cities c ON c.id = u.city_id
    WHERE (:transactionType IS NULL OR nl.transaction_type = :transactionType)
      AND (:status IS NULL OR nl.status = :status)
      AND (:orderType IS NULL OR nl.order_type = :orderType)
      AND (:channel IS NULL OR nl.channel = :channel)
      AND (:fromDate IS NULL OR nl.created_at >= :fromDate)
      AND (:toDate IS NULL OR nl.created_at <= :toDate)

    ORDER BY created_at DESC
    """,
            countQuery = """
        SELECT COUNT(*) FROM (
            SELECT st.patient_id FROM mh_system_transaction st
            WHERE (:transactionType IS NULL OR st.transaction_type = :transactionType)
              AND (:status IS NULL OR st.status = :status)
              AND (:orderType IS NULL OR st.order_type = :orderType)
              AND (:channel IS NULL OR st.channel = :channel)
              AND (:fromDate IS NULL OR st.created_at >= :fromDate)
              AND (:toDate IS NULL OR st.created_at <= :toDate)

            UNION ALL

            SELECT nl.user_id FROM mh_nod_logs nl
            WHERE (:transactionType IS NULL OR nl.transaction_type = :transactionType)
              AND (:status IS NULL OR nl.status = :status)
              AND (:orderType IS NULL OR nl.order_type = :orderType)
              AND (:channel IS NULL OR nl.channel = :channel)
              AND (:fromDate IS NULL OR nl.created_at >= :fromDate)
              AND (:toDate IS NULL OR nl.created_at <= :toDate)
        ) AS total_records
    """,
            nativeQuery = true)
    Page<Object[]> searchWithFilters(
            @Param("transactionType") String transactionType,
            @Param("status") String status,
            @Param("orderType") String orderType,
            @Param("channel") String channel,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            Pageable pageable
    );

}