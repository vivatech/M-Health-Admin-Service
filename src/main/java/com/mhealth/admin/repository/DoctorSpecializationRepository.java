package com.mhealth.admin.repository;

import com.mhealth.admin.model.DoctorSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DoctorSpecializationRepository extends JpaRepository<DoctorSpecialization, Integer> {
    @Query("Select u from DoctorSpecialization u where u.userId.userId = ?1")
    List<DoctorSpecialization> findByUserId(Integer val);
    @Modifying
    @Query(value = "DELETE FROM mh_doctor_specialization WHERE user_id = :userId", nativeQuery = true)
    @Transactional
    void deleteByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT u.specializationId.name FROM DoctorSpecialization u WHERE u.userId.userId = ?1 ")
    List<String> findSpName(Integer userId);
}