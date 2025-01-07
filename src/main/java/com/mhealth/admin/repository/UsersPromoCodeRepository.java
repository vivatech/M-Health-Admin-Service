package com.mhealth.admin.repository;

import com.mhealth.admin.model.UsersPromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsersPromoCodeRepository extends JpaRepository<UsersPromoCode,Integer> {

    @Query("SELECT u FROM UsersPromoCode u WHERE u.promoCode = :promoCode")
    UsersPromoCode findByPromoCode(@Param("promoCode") String promoCode);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM mh_users_promo_code WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Integer userId);
}
