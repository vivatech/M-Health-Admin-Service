package com.mhealth.admin.repository;

import com.mhealth.admin.model.HospitalMerchantNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalMerchantNumberRepository extends JpaRepository<HospitalMerchantNumber, Integer> {
    Optional<HospitalMerchantNumber> findByUserId(Integer user);

    void deleteByUserId(Integer existingMarketingUser);
}
