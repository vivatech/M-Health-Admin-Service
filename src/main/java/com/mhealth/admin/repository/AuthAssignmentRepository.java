package com.mhealth.admin.repository;

import com.mhealth.admin.model.AuthAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AuthAssignmentRepository extends JpaRepository<AuthAssignment, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM auth_assignment WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO auth_assignment (item_name, user_id, created_at) VALUES (:itemName, :userId, :createdAt)", nativeQuery = true)
    void insertRole(@Param("itemName") String itemName, @Param("userId") String userId, @Param("createdAt") Integer createdAt);
}

