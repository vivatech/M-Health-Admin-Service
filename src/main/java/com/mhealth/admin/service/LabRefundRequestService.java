package com.mhealth.admin.service;

import com.mhealth.admin.dto.LabRefundRequestResponseDTO;
import com.mhealth.admin.repository.LabRefundRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LabRefundRequestService {
    @Autowired
    private LabRefundRequestRepository labRefundRequestRepository;

    public Page<LabRefundRequestResponseDTO> searchRefundRequests(
            String patientName, String labName, Pageable pageable) {
        return labRefundRequestRepository.findByPatientNameAndLabName(patientName, labName, pageable);
    }
}
