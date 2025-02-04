package com.mhealth.admin.repository;

import com.mhealth.admin.model.Users;
import com.mhealth.admin.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer> {
    @Query("Select count(u.id) from WalletTransaction u where u.patientId.userId = ?1")
    Long countByPatientId(Integer userId);

    @Query("Select u from WalletTransaction u where u.patientId.userId = ?1 ORDER BY u.id DESC")
    List<WalletTransaction> findByPatientIdDesc(Integer patientId);

    @Query("Select u from WalletTransaction u where u.patientId.userId = ?1 and u.isDebitCredit = ?2 and " +
            "Date(u.createdAt) = ?3 and u.serviceType LIKE ?4 ORDER BY u.id DESC")
    Page<WalletTransaction> findByPatientIdIsDebitCreditServiceTypeCreatedAt(Integer userId, String debit,
                                                                             LocalDate createdDate, String type, Pageable pageable);

    @Query("Select u from WalletTransaction u where u.patientId.userId = ?1 and u.isDebitCredit = ?2 and " +
            "Date(u.createdAt) = ?3 ORDER BY u.id DESC")
    Page<WalletTransaction> findByPatientIdIsDebitCreditCreatedAt(Integer userId, String debit, LocalDate createdDate, Pageable pageable);

    @Query("Select u from WalletTransaction u where u.patientId.userId = ?1 and u.isDebitCredit = ?2 and " +
            "u.serviceType LIKE ?3 ORDER BY u.id DESC")
    Page<WalletTransaction> findByPatientIdServiceType(Integer userId, String debit, String type, Pageable pageable);

    @Query("Select u from WalletTransaction u where u.patientId.userId = ?1 and u.isDebitCredit = ?2 ORDER BY u.id DESC")
    Page<WalletTransaction> findByPatientIdIsDebit(Integer userId, String debit, Pageable pageable);

    @Query("Select u from WalletTransaction u where u.patientId.userId = ?1 and u.isDebitCredit = ?2 ORDER BY u.id DESC")
    List<WalletTransaction> findByPatientIdIsDebitList(Integer userId, String debit);

    @Query("Select u from WalletTransaction u where u.orderId = ?1")
    List<WalletTransaction> findByOrderId(Integer id);

    @Query(value = "SELECT * FROM mh_wallet_transaction WHERE order_id = ?1 AND patient_id = ?2 AND is_debit_credit = 'debit' limit 1", nativeQuery = true)
    WalletTransaction findByOrderIdAndPatientIdAndServiceTypeAndisDebitCredit(Integer id, Integer patientId);
  
    Optional<WalletTransaction> findByPatientIdAndTransactionIdAndIsDebitCredit(Users userId, String transactionId, String debit);

    @Query(value = "SELECT wt.* FROM mh_wallet_transaction wt WHERE wt.order_id AND wt.service_type = 'consultation' ORDER BY wt.id DESC limit 1", nativeQuery = true)
    WalletTransaction findByOrderIdANDServiceType(Integer id);
}