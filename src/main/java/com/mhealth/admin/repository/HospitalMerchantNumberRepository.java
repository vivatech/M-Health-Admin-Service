package com.mhealth.admin.repository;

import com.mhealth.admin.model.HospitalMerchantNumber;
import com.mhealth.admin.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalMerchantNumberRepository extends JpaRepository<HospitalMerchantNumber, Integer> {
    Optional<HospitalMerchantNumber> findByUserId(Integer user);

    void deleteByUserId(Integer existingMarketingUser);
    // Add custom query methods here if needed
}
