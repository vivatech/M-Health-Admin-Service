package com.mhealth.admin.repository;

import com.mhealth.admin.model.SystemTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SystemTransactionRepository extends JpaRepository<SystemTransaction, Integer> {
    @Query(value = """
      SELECT * FROM mh_system_transaction st
      WHERE (:serviceType IS NULL OR :serviceType = '' OR st.transaction_type = :serviceType)
      AND (st.order_type IS NOT NULL)
      AND (:status IS NULL OR :status = '' OR st.status = :status)
      AND (:orderType IS NULL OR :orderType = '' OR st.order_type = :orderType)
      AND (:channel IS NULL OR :channel = '' OR st.channel = :channel)
      AND (:fromDate IS NULL OR :fromDate = '' OR st.created_at >= :fromDate)
      AND (:toDate IS NULL OR :toDate = '' OR st.created_at <= :toDate)
""", nativeQuery = true)
    Page<SystemTransaction> fetchPatientTransaction(
            @Param("serviceType") String serviceType,
            @Param("status") String status,
            @Param("orderType") String orderType,
            @Param("channel") String channel,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            Pageable pageable
    );

    @Query(value = """
    SELECT st.*
    FROM mh_nod_logs st
    WHERE st.order_type IS NOT NULL
      AND st.status IS NOT NULL
      AND (:serviceType IS NULL OR :serviceType = '' OR st.transaction_type = :serviceType)
      AND (:status IS NULL OR :status = '' OR st.status = :status)
      AND (:orderType IS NULL OR :orderType = '' OR st.order_type = :orderType)
      AND (:channel IS NULL OR :channel = '' OR st.channel = :channel)
      AND (:fromDate IS NULL OR :fromDate = '' OR st.created_at >= :fromDate)
      AND (:toDate IS NULL OR :toDate = '' OR st.created_at <= :toDate)
    LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<Object[]> fetchNodTransaction(
            @Param("serviceType") String serviceType,
            @Param("status") String status,
            @Param("orderType") String orderType,
            @Param("channel") String channel,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM mh_nod_logs st
    WHERE st.order_type IS NOT NULL
      AND st.status IS NOT NULL
      AND (:serviceType IS NULL OR :serviceType = '' OR st.transaction_type = :serviceType)
      AND (:status IS NULL OR :status = '' OR st.status = :status)
      AND (:orderType IS NULL OR :orderType = '' OR st.order_type = :orderType)
      AND (:channel IS NULL OR :channel = '' OR st.channel = :channel)
      AND (:fromDate IS NULL OR :fromDate = '' OR st.created_at >= :fromDate)
      AND (:toDate IS NULL OR :toDate = '' OR st.created_at <= :toDate)
""", nativeQuery = true)
    long countNodTransaction(
            @Param("serviceType") String serviceType,
            @Param("status") String status,
            @Param("orderType") String orderType,
            @Param("channel") String channel,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );


}