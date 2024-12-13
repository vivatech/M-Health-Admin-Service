package com.mhealth.admin.repository;

import com.mhealth.admin.model.UsersUsedCouponCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsersUsedCouponCodeRepository extends JpaRepository<UsersUsedCouponCode,Integer> {

    @Query("Select u from UsersUsedCouponCode u where u.userId = ?1 and u.couponId = ?2")
    List<UsersUsedCouponCode> findByUserIdAndCouponId(Integer userId, Integer id);
}
