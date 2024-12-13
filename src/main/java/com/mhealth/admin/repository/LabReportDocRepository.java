package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.AddedType;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.LabReportDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LabReportDocRepository extends JpaRepository<LabReportDoc, Integer> {
    @Query("Select u from LabReportDoc u where u.labOrdersId = ?1")
    List<LabReportDoc> findByLabOrderId(Integer id);

    @Query("Select u from LabReportDoc u where u.caseId = ?1 and u.status = ?2")
    List<LabReportDoc> findByCaseIdAndStatus(Integer id, StatusAI statusAI);

    @Query("Select u from LabReportDoc u where u.status = ?1")
    List<LabReportDoc> findByStatus(StatusAI statusAI);

    @Query("Select u from LabReportDoc u where u.caseId = ?1 and u.status = ?2 and u.labOrdersId = ?3")
    List<LabReportDoc> findByCaseIdAndStatusAndLabOrdersId(Integer caseId, StatusAI statusAI, Integer labOrdersId);

    @Query("Select u from LabReportDoc u where u.status = ?1 and u.labOrdersId = ?2")
    List<LabReportDoc> findByStatusAndLabOrdersId(StatusAI statusAI, Integer labOrdersId);

    @Query("Select u from LabReportDoc u where u.caseId = ?1 and u.addedBy = ?2 and u.addedType = ?3 and u.status = ?4")
    List<LabReportDoc> findByCaseIdAndAddedByAddedTypeAndStatus(Integer caseId, Integer userId, AddedType addedType, StatusAI statusAI);
}