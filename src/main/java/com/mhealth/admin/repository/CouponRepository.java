package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.CouponCategory;
import com.mhealth.admin.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    @Query("Select u from Coupon u where u.status = ?1")
    List<Coupon> findByStatus(Integer i);

    @Query("Select u from Coupon u where u.couponCode = ?1 and u.category = ?2 and u.status =?3")
    List<Coupon> findByCouponCodeAndCategoryAndStatus(String couponCode, CouponCategory category, Integer i);

    @Query(value = "SELECT * FROM mh_coupon WHERE coupon_code = ?1 AND category = ?2 AND status = ?3", nativeQuery = true)
    Coupon findByNameStatusAndCategory(String couponCode, String category, Integer i);
}