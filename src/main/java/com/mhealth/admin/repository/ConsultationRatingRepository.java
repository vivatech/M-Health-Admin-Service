package com.mhealth.admin.repository;

import com.mhealth.admin.model.ConsultationRating;
import com.mhealth.admin.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConsultationRatingRepository extends JpaRepository<ConsultationRating, Integer> {
    List<ConsultationRating> findByDoctorId(Users val);

    @Query(value = "SELECT AVG(cr.rating) FROM mh_consultation_rating cr WHERE cr.doctor_id = ?1", nativeQuery = true)
    float findDoctorRating(Integer userId);

    @Query(value = "SELECT COUNT(DISTINCT(cr.patient_id)) FROM mh_consultation_rating cr WHERE cr.doctor_id = ?1", nativeQuery = true)
    int findReviews(Integer userId);

    @Query("SELECT SUM(cr.rating) FROM ConsultationRating cr WHERE cr.doctorId.userId = ?1 AND cr.status = 'Approve'")
    Double sumRatingsByDoctorId(Integer doctorId);

    @Query("SELECT COUNT(cr.id) FROM ConsultationRating cr WHERE cr.doctorId.userId = ?1 AND cr.status = 'Approve'")
    Long countApprovedRatingsByDoctorId(Integer doctorId);

    @Query("SELECT cr FROM ConsultationRating cr WHERE cr.caseId = ?1")
    List<ConsultationRating> getByCaseId(Integer caseId);

    @Query("SELECT cr FROM ConsultationRating cr WHERE cr.caseId = ?1 and cr.doctorId.userId = ?2")
    List<ConsultationRating> getByCaseIdAndDoctorId(Integer caseId,Integer doctorId);

//    @Query("SELECT cr FROM ConsultationRating cr WHERE cr.doctorId.userId = ?1 AND cr.status = 'Approve'")
    @Query(value = "SELECT cr.* FROM mh_consultation_rating cr JOIN mh_users u ON u.user_id = cr.doctor_id WHERE cr.doctor_id = ?1 LIMIT 2 ", nativeQuery = true)
    List<ConsultationRating> getByDoctorIdActive(Integer doctorId);

    @Query("SELECT count(cr.id) FROM ConsultationRating cr WHERE cr.doctorId.userId = ?1")
    Long countByDoctorIdAll(Integer doctorId);

    @Query("SELECT cr FROM ConsultationRating cr WHERE cr.doctorId.userId = ?1 AND cr.status = 'Approve' order by cr.id desc")
    Page<ConsultationRating> findByDoctorIdApproveOrderIdDesc(Integer doctorId, Pageable pageable);


    @Query("SELECT count(cr) FROM ConsultationRating cr WHERE cr.caseId = ?1 and cr.patientId.userId = ?2")
    Long countByCaseIdAndPatientId(Integer caseId, Integer userId);
}