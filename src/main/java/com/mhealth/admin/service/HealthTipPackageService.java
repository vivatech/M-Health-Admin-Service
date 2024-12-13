package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipPackageRequest;
import com.mhealth.admin.dto.request.HealthTipPackageSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.HealthTipDuration;
import com.mhealth.admin.model.HealthTipPackage;
import com.mhealth.admin.repository.HealthTipDurationRepository;
import com.mhealth.admin.repository.HealthTipPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class HealthTipPackageService {
    @Autowired
    private HealthTipPackageRepository repository;

    @Autowired
    private HealthTipDurationRepository durationRepository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> createHealthTipPackage(HealthTipPackageRequest request, Locale locale) {
        HealthTipDuration duration = durationRepository.findById(request.getDurationId())
                .orElse(null);
        if (duration == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_DURATION_NOT_FOUND, null, locale)));
        }

        HealthTipPackage healthTipPackage = new HealthTipPackage();
        healthTipPackage.setPackageName(request.getPackageName());
        healthTipPackage.setPackageNameSl(request.getPackageNameSl());
        healthTipPackage.setHealthTipDuration(duration);
        healthTipPackage.setPackagePrice(request.getPackagePrice());
        healthTipPackage.setPackagePriceVideo(request.getPackagePriceVideo());
        healthTipPackage.setType(request.getType());
        healthTipPackage.setStatus(request.getStatus());
        healthTipPackage.setCreatedAt(LocalDateTime.now());

        repository.save(healthTipPackage);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_PACKAGE_CREATED_SUCCESS, null, locale)));
    }


    public ResponseEntity<Response> updateHealthTipPackage(Integer id, HealthTipPackageRequest request, Locale locale) {
        HealthTipPackage healthTipPackage = repository.findById(id).orElse(null);
        if (healthTipPackage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_DURATION_NOT_FOUND, null, locale)));
        }

        if (request.getDurationId() != null) {
            HealthTipDuration duration = durationRepository.findById(request.getDurationId())
                    .orElse(null);
            if (duration == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                                messageSource.getMessage(Constants.HEALTH_TIP_DURATION_NOT_FOUND, null, locale)));
            }
            healthTipPackage.setHealthTipDuration(duration);
        }

        if (request.getPackageName() != null && !request.getPackageName().isEmpty()) {
            healthTipPackage.setPackageName(request.getPackageName());
        }

        if (request.getPackageNameSl() != null && !request.getPackageNameSl().isEmpty()) {
            healthTipPackage.setPackageNameSl(request.getPackageNameSl());
        }

        if (request.getPackagePrice() != null) {
            healthTipPackage.setPackagePrice(request.getPackagePrice());
        }

        if (request.getPackagePriceVideo() != null) {
            healthTipPackage.setPackagePriceVideo(request.getPackagePriceVideo());
        }

        if (request.getType() != null) {
            healthTipPackage.setType(request.getType());
        }

        if (request.getStatus() != null) {
            healthTipPackage.setStatus(request.getStatus());
        }

        healthTipPackage.setUpdatedAt(LocalDateTime.now());
        repository.save(healthTipPackage);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_PACKAGE_UPDATED_SUCCESS, null, locale)));
    }


    public ResponseEntity<Response> updateStatus(Integer id, StatusAI status, Locale locale) {
        HealthTipPackage healthTipPackage = repository.findById(id).orElse(null);
        if (healthTipPackage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_PACKAGE_NOT_FOUND, null, locale)));
        }

        healthTipPackage.setStatus(status);
        healthTipPackage.setUpdatedAt(LocalDateTime.now());
        repository.save(healthTipPackage);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_PACKAGE_STATUS_UPDATED_SUCCESS, null, locale)));
    }

    public ResponseEntity<Response> deleteHealthTipPackage(Integer id, Locale locale) {
        HealthTipPackage healthTipPackage = repository.findById(id).orElse(null);
        if (healthTipPackage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_PACKAGE_NOT_FOUND, null, locale)));
        }

        repository.delete(healthTipPackage);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_PACKAGE_DELETED_SUCCESS, null, locale)));
    }


    public ResponseEntity<PaginationResponse<HealthTipPackage>> searchHealthTipPackages(HealthTipPackageSearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<HealthTipPackage> page = repository.findByNameAndStatusAndDuration(
                request.getPackageName(),
                request.getStatus(),
                request.getDurationId(),
                pageable
        );

        return ResponseEntity.ok(new PaginationResponse<>(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_PACKAGE_FETCHED_SUCCESS, null, locale),
                page.getContent(), page.getTotalElements(), (long) page.getSize(), (long) page.getNumber()));
    }

    public ResponseEntity<Response> findHealthTipPackageById(Integer id, Locale locale) {
        HealthTipPackage healthTipPackage = repository.findById(id).orElse(null);
        if (healthTipPackage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_PACKAGE_NOT_FOUND, null, locale)));
        }

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_PACKAGE_FETCHED_SUCCESS, null, locale)));
    }

    public ResponseEntity<PaginationResponse<HealthTipPackage>> searchPackages(
            String packageName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        Page<HealthTipPackage> packages = repository.searchByPackageNameAndCreatedAt(
                packageName, startDate, endDate, pageable);

        return ResponseEntity.ok(new PaginationResponse<>(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                Constants.HEALTH_TIP_PACKAGE_FETCHED_SUCCESS,
                packages.getContent(),
                packages.getTotalElements(),
                (long) packages.getSize(),
                (long) packages.getNumber()));
    }
}
