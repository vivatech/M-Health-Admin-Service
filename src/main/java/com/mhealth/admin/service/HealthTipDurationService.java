package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipDurationRequest;
import com.mhealth.admin.dto.request.HealthTipDurationSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.HealthTipDuration;
import com.mhealth.admin.repository.HealthTipDurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class HealthTipDurationService {

    @Autowired
    private HealthTipDurationRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> createDuration(HealthTipDurationRequest request, Locale locale) {
        HealthTipDuration duration = new HealthTipDuration();
        duration.setDurationName(request.getDurationName());
        duration.setDurationType(request.getDurationType());
        duration.setDurationValue(request.getDurationValue());
        duration.setStatus(request.getStatus());

        repository.save(duration);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_DURATION_DURATION_CREATED, null, locale)));
    }

    public ResponseEntity<Response> updateDuration(Integer id, HealthTipDurationRequest request, Locale locale) {
        HealthTipDuration duration = repository.findById(id).orElse(null);
        if (duration == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_DURATION_NOT_FOUND, null, locale)));
        }

        duration.setDurationName(request.getDurationName());
        duration.setDurationType(request.getDurationType());
        duration.setDurationValue(request.getDurationValue());
        duration.setStatus(request.getStatus());

        repository.save(duration);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_DURATION_DURATION_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> changeStatus(Integer id, StatusAI status, Locale locale) {
        HealthTipDuration duration = repository.findById(id).orElse(null);
        if (duration == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_DURATION_NOT_FOUND, null, locale)));
        }

        duration.setStatus(status);
        repository.save(duration);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_DURATION_STATUS_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> deleteDuration(Integer id, Locale locale) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_DURATION_NOT_FOUND, null, locale)));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_DURATION_DELETED, null, locale)));
    }

    public ResponseEntity<PaginationResponse<HealthTipDuration>> searchDurations(
            HealthTipDurationSearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<HealthTipDuration> page = repository.findByDurationNameContainingAndStatus(
                request.getDurationName(), request.getStatus(), pageable);

        return ResponseEntity.ok(new PaginationResponse<>(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_DURATION_FETCHED, null, locale),
                page.getContent(), page.getTotalElements(),
                (long) page.getSize(), (long) page.getNumber()));
    }
}
