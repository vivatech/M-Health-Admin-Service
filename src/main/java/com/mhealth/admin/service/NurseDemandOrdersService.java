package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.NurseDemandOrders;
import com.mhealth.admin.repository.NurseDemandOrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class NurseDemandOrdersService {
    @Autowired
    private NurseDemandOrdersRepository repository;

    public PaginationResponse<NurseDemandOrders> searchOrders(
            String patientName, String nurseName, LocalDate consultationDate, Pageable pageable) {

        Page<NurseDemandOrders> page = repository.searchOrders(
                patientName, nurseName, consultationDate, pageable);

        return new PaginationResponse<>(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                Constants.HEALTH_TIP_FETCHED_SUCCESS,
                page.getContent(),
                (long)page.getNumber(),
                (long)page.getSize(),
                page.getTotalElements());
    }
}
