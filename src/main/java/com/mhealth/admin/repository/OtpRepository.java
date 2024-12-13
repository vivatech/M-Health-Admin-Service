package com.mhealth.admin.repository;

import com.mhealth.admin.model.UserOTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<UserOTP,Integer> {
    UserOTP findFirstByUserIdOrderByIdDesc(Integer userId);
}
