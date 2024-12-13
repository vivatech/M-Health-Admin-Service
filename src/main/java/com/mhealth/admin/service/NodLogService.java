package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.NodLogSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.NodLog;
import com.mhealth.admin.repository.NodLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class NodLogService {

    @Autowired
    private NodLogRepository repository;

    public PaginationResponse<NodLog> fetchAll(NodLogSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage().intValue(),
                request.getSize().intValue(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<NodLog> pageResult = repository.findAllByFilters(
                request.getSearchId(),
                request.getPatientName(),
                pageable
        );

        return new PaginationResponse<>(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                Constants.SUCCESS,
                pageResult.getContent(),
                pageResult.getTotalElements(),
                (long) pageResult.getSize(),
                pageResult.getTotalElements()
        );
    }
}
