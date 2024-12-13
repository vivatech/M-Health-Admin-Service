package com.mhealth.admin.repository;

import com.mhealth.admin.model.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Integer> {
}