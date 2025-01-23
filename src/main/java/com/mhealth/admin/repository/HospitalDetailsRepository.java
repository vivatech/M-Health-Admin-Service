package com.mhealth.admin.repository;

import com.mhealth.admin.model.HospitalDetails;
import com.mhealth.admin.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalDetailsRepository extends JpaRepository<HospitalDetails, Integer> {
    Optional<HospitalDetails> findByUserId(Integer existingUser);

    void deleteByUserId(Integer existingMarketingUser);

    // Add custom query methods here if needed
}