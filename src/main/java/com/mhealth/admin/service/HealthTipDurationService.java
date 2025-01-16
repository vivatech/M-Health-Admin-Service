package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipDurationRequest;
import com.mhealth.admin.dto.request.HealthTipDurationSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.HealthTipDuration;
import com.mhealth.admin.model.HealthTipPackage;
import com.mhealth.admin.repository.HealthTipDurationRepository;
import com.mhealth.admin.repository.HealthTipPackageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class HealthTipDurationService {

    @Autowired
    private HealthTipDurationRepository repository;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private HealthTipPackageRepository healthTipPackageRepository;

    public ResponseEntity<Response> createDuration(HealthTipDurationRequest request, Locale locale) {
        HealthTipDuration duration = new HealthTipDuration();
        duration.setDurationName(request.getDurationName());
        duration.setDurationType(request.getDurationType());
        duration.setDurationValue(request.getDurationValue());
        duration.setStatus(request.getStatus());
        duration.setCreatedAt("0000-00-00" + " 00:00:00");

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
        HealthTipDuration healthTipDuration = repository.findById(id).orElse(null);
        if (healthTipDuration == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_DURATION_NOT_FOUND, null, locale)));
        }

        List<HealthTipPackage> healthTipPackages = healthTipPackageRepository.findByHealthTipDuration(healthTipDuration);
        if (!healthTipPackages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(Status.FAILED, Constants.INTERNAL_SERVER_ERROR_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_DURATION_HAS_DEPENDENCIES, null, locale)));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_DURATION_DELETED, null, locale)));
    }

    public ResponseEntity<PaginationResponse<HealthTipDuration>> searchDurations(
            HealthTipDurationSearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize() != null ? request.getSize() : Constants.DEFAULT_PAGE_SIZE);
        StatusAI status = StringUtils.isEmpty(request.getStatus()) ? null : StatusAI.valueOf(request.getStatus());
        Page<HealthTipDuration> page = repository.findByDurationNameContainingAndStatus(
                request.getDurationName(), status, pageable);

        if (page.getContent().isEmpty()) {
            return ResponseEntity.ok(new PaginationResponse<>(
                    Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.HEALTH_TIP_DURATION_FETCHED_EMPTY, null, locale),
                    page.getContent(), page.getTotalElements(),
                    (long) page.getSize(), (long) page.getNumber()));
        }

        return ResponseEntity.ok(new PaginationResponse<>(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_DURATION_FETCHED, null, locale),
                page.getContent(), page.getTotalElements(),
                (long) page.getSize(), (long) page.getNumber()));
    }
}
