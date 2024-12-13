package com.mhealth.admin.repository;

import com.mhealth.admin.model.UsersCreatedWithPromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersCreatedWithPromoCodeRepository extends JpaRepository<UsersCreatedWithPromoCode,Integer> {
}
