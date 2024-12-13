package com.mhealth.admin.repository;

import com.mhealth.admin.model.UserRelative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRelativeRepository extends JpaRepository<UserRelative, Byte> {
    @Query("Select u from UserRelative u where u.createdBy = ?1")
    List<UserRelative> findByCreatedBy(Integer userId);
}