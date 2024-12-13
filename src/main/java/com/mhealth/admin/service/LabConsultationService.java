package com.mhealth.admin.service;

import com.mhealth.admin.dto.dto.LabConsultationResponseDTO;
import com.mhealth.admin.repository.LabConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LabConsultationService {

    @Autowired
    private LabConsultationRepository labConsultationRepository;

    public Page<LabConsultationResponseDTO> searchLabConsultations(
            String patientName, String doctorName, Integer caseId, LocalDate consultationDate, Pageable pageable) {
        return labConsultationRepository.searchLabConsultations(
                patientName, doctorName, caseId, consultationDate, pageable);
    }
}
