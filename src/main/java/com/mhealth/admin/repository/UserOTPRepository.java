package com.mhealth.admin.repository;

import com.mhealth.admin.model.UserOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserOTPRepository extends JpaRepository<UserOTP, Integer> {
   @Query(value = "SELECT u.* FROM mh_user_otp u WHERE u.user_id = ?1 AND u.is_from = 'Login' ORDER BY u.id DESC LIMIT 1", nativeQuery = true)
   UserOTP findByUserIdAndIsFrom(Integer userId);
}