package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.model.AuthKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthKeyRepository extends JpaRepository<AuthKey,Integer> {

    @Query("SELECT a FROM AuthKey a WHERE a.userId = :userId AND a.loginType = :loginType")
    Optional<AuthKey> findByUserIdAndLoginType(@Param("userId") Integer userId, @Param("loginType") UserType loginType);

    @Query("SELECT a FROM AuthKey a WHERE a.userId = :userId AND a.loginType = :loginType")
    List<AuthKey> findAllByUserIdAndLoginType(@Param("userId") Integer userId, @Param("loginType") UserType loginType);

    AuthKey findByUserIdAndLoginTypeAndAuthKey(Integer userId, UserType userType, String key);
}
