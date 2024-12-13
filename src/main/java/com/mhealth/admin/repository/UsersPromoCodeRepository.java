package com.mhealth.admin.repository;

import com.mhealth.admin.model.UsersPromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersPromoCodeRepository extends JpaRepository<UsersPromoCode,Integer> {

    @Query("SELECT u FROM UsersPromoCode u WHERE u.promoCode = :promoCode")
    UsersPromoCode findByPromoCode(@Param("promoCode") String promoCode);
}
