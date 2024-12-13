package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.Degree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DegreeRepository extends JpaRepository<Degree,Integer> {
    @Query("Select u from Degree u where u.name LIKE %:name% AND (:status is null or u.status = :status)")
    List<Degree> findByNameContainingAndStatus(@Param("name") String name,@Param("status") StatusAI status);
}
