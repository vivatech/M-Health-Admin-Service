package com.mhealth.admin.repository;

import com.mhealth.admin.model.GroupOrderConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupOrderConsultationRepository extends JpaRepository<GroupOrderConsultation, Integer> {

    List<GroupOrderConsultation> findByStatus(GroupOrderConsultation.Status status);
}
