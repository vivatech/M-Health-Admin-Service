package com.mhealth.admin.repository;

import com.mhealth.admin.model.VideoAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoAttachmentRepository extends JpaRepository<VideoAttachment, Integer> {
    @Query("Select u from VideoAttachment u where u.caseId = ?1 order by u.id desc")
    List<VideoAttachment> findByCaseIdIdDesc(Integer caseId);
}