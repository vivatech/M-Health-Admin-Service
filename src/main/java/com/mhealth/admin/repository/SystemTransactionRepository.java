package com.mhealth.admin.repository;

import com.mhealth.admin.model.NodLog;
import com.mhealth.admin.model.SystemTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SystemTransactionRepository extends JpaRepository<SystemTransaction, Integer> {

    @Query("""
    SELECT st
    FROM SystemTransaction st
    WHERE (:serviceType IS NULL OR :serviceType = '' OR st.transactionType = :serviceType)
      AND (st.orderType IS NOT NULL)
      AND (:status IS NULL OR :status = '' OR st.status = :status)
      AND (:orderType IS NULL OR :orderType = '' OR st.orderType = :orderType)
      AND (:channel IS NULL OR :channel = '' OR st.channel = :channel)
      AND (:fromDate IS NULL OR :fromDate = '' OR st.createdAt >= :fromDate)
      AND (:toDate IS NULL OR :toDate = '' OR st.createdAt <= :toDate)
""")
    Page<SystemTransaction> fetchPatientTransaction(
            @Param("serviceType") String serviceType,
            @Param("status") String status,
            @Param("orderType") String orderType,
            @Param("channel") String channel,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            Pageable pageable
    );

    @Query("""
    SELECT st
    FROM NodLog st
    WHERE st.orderType IS NOT NULL
      AND st.status IS NOT NULL
      AND (:serviceType IS NULL OR :serviceType = '' OR st.transactionType = :serviceType)
      AND (:status IS NULL OR :status = '' OR st.status = :status)
      AND (:orderType IS NULL OR :orderType = '' OR st.orderType = :orderType)
      AND (:channel IS NULL OR :channel = '' OR st.channel = :channel)
      AND (:fromDate IS NULL OR :fromDate = '' OR st.createdAt >= :fromDate)
      AND (:toDate IS NULL OR :toDate = '' OR st.createdAt <= :toDate)
""")
    Page<NodLog> fetchNodTransaction(
            @Param("serviceType") String serviceType,
            @Param("status") String status,
            @Param("orderType") String orderType,
            @Param("channel") String channel,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            Pageable pageable
    );


}