package com.mhealth.admin.repository;

import com.mhealth.admin.model.DoctorDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorDocumentRepository extends JpaRepository<DoctorDocument,Integer> {
}
