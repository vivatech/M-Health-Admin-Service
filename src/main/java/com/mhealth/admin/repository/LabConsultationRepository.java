package com.mhealth.admin.repository;

import com.mhealth.admin.dto.dto.LabConsultationResponseDTO;
import com.mhealth.admin.model.LabConsultation;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LabConsultationRepository extends JpaRepository<LabConsultation, Integer>, JpaSpecificationExecutor<LabConsultationResponseDTO> {
    @Query("Select u from LabConsultation u where u.patient.userId = ?1 and u.labOrdersId is null and u.caseId is null")
    List<LabConsultation> findByPatientIdANdLabOrderIsNullAndCaseIsNull(Integer userId);

    @Query("Select u from LabConsultation u where u.patient.userId = ?1 and u.categoryId.catId= ?2 and u.subCatId = ?3 and u.labOrdersId is null and u.caseId is null")
    List<LabConsultation> findByPatientIdCategoryIdSubCategoryIdLabOrderAndCaseNull(Integer userId, Integer categoryId, Integer subCatId);

    @Query("Select u from LabConsultation u where u.caseId.caseId = ?1 and u.labOrdersId IS NULL ")
    List<LabConsultation> findByCaseId(Integer caseId);
    @Query("Select u from LabConsultation u where u.patient.userId = ?1 AND u.caseId IS NULL AND u.labOrdersId IS NULL")
    List<LabConsultation> findByPatientId(Integer caseId);

    @Query(value = "Select s.sub_cat_name from mh_lab_consultation u LEFT JOIN mh_lab_sub_cat_master s ON s.sub_cat_id = u.sub_cat_id where u.lab_orders_id = ?1", nativeQuery = true)
    List<String> findByLabOrderId(Integer id);

    @Query("Select u from LabConsultation u where u.patient.userId = ?1 and u.caseId.caseId = ?2 and u.categoryId.catId = ?3 and u.subCatId = ?4 order by u.labConsultId DESC")
    List<LabConsultation> findByPatientIdCaseIdCategoryIdSubCategoryId(Integer userId, Integer caseId, Integer categoryId, Integer subcategoryId);

    @Query("Select u from LabConsultation u where u.patient.userId = ?1 and u.categoryId.catId = ?2 and u.subCatId = ?3 order by u.labConsultId DESC")
    List<LabConsultation> findByPatientIdCategoryIdSubCategoryId(Integer userId, Integer categoryId, Integer subcategoryId);

    @Query("Select u from LabConsultation u where u.patient.userId = ?1 and u.caseId.caseId = ?2 and u.categoryId.catId = ?3 order by u.labConsultId DESC")
    List<LabConsultation> findByPatientIdCaseIdCategoryId(Integer userId, Integer caseId, Integer categoryId);

    @Query("Select u from LabConsultation u where u.patient.userId = ?1 and u.categoryId.catId = ?2 order by u.labConsultId DESC")
    List<LabConsultation> findByPatientIdCategoryId(Integer userId, Integer categoryId);

    @Query("Select u from LabConsultation u where u.patient.userId = ?1 and u.caseId.caseId = ?2 and u.subCatId = ?3 order by u.labConsultId DESC")
    List<LabConsultation> findByPatientIdCaseIdSubCategoryId(Integer userId, Integer caseId, Integer subcategoryId);

    @Query("Select u from LabConsultation u where u.patient.userId = ?1 and u.subCatId = ?2 order by u.labConsultId DESC")
    List<LabConsultation> findByPatientIdSubCategoryId(Integer userId, Integer subcategoryId);

    @Query("Select u from LabConsultation u where u.patient.userId = ?1 and u.caseId.caseId = ?2 order by u.labConsultId DESC")
    List<LabConsultation> findByPatientIdCaseId(Integer userId, Integer caseId);

    @Query("Select u from LabConsultation u where u.subCatId in ?1 and u.caseId.caseId = ?2 and labOrdersId IS NOT NULL")
    List<LabConsultation> findBySubCategoryIdCaseIdLadIdNotNull(List<Integer> subCatId,Integer caseId);

    @Query("Select u from LabConsultation u where u.subCatId in ?1 and u.caseId.caseId = ?2")
    List<LabConsultation> findBySubCategoryIdCaseId(Integer subCatId,Integer caseId);

    @Query("Select u from LabConsultation u where u.subCatId in ?1 and u.caseId.caseId IS NULL AND u.labOrdersId IS NULL AND u.patient.userId = ?2 ")
    List<LabConsultation> findBySubCategoryIdCaseIdNullLabOrderNullPatientId(Integer i, Integer userId);

    @Query("SELECT new com.mhealth.admin.dto.dto.LabConsultationResponseDTO(" +
            "lc.labConsultId, c.caseId, lo.id, lc.categoryId.catId, lc.subCatId, " +
            "CONCAT(p.firstName, ' ', p.lastName), CONCAT(d.firstName, ' ', d.lastName), " +
            "lc.doctorPrescription, lc.labConsultCreatedAt, c.consultationDate) " +
            "FROM LabConsultation lc " +
            "JOIN lc.caseId c " +
            "JOIN lc.patient p " +
            "LEFT JOIN lc.doctor d " +
            "LEFT JOIN lc.labOrdersId lo " +
            "WHERE (:patientName IS NULL OR CONCAT(p.firstName, ' ', p.lastName) LIKE %:patientName%) " +
            "AND (:doctorName IS NULL OR CONCAT(d.firstName, ' ', d.lastName) LIKE %:doctorName%) " +
            "AND (:caseId IS NULL OR c.caseId = :caseId) " +
            "AND (:consultationDate IS NULL OR c.consultationDate = :consultationDate)")
    Page<LabConsultationResponseDTO> searchLabConsultations(
            @Param("patientName") String patientName,
            @Param("doctorName") String doctorName,
            @Param("caseId") Integer caseId,
            @Param("consultationDate") LocalDate consultationDate,
            Pageable pageable);

    @Query(value = "SELECT s.subCatName FROM LabConsultation u LEFT JOIN LabSubCategoryMaster s ON s.subCatId = u.subCatId WHERE u.labOrdersId.id = ?1")
    String findByLabOrderIdAndName(Integer id);
}