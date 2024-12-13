package com.mhealth.admin.repository;

import com.mhealth.admin.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    @Query("Select u from Wallet u where u.userId = ?1 order by u.id ASC")
    List<Wallet> findLastTransacton(Integer userId);
}