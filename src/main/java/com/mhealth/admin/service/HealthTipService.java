package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipRequest;
import com.mhealth.admin.dto.request.HealthTipSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.HealthTip;
import com.mhealth.admin.model.HealthTipCategoryMaster;
import com.mhealth.admin.repository.HealthTipCategoryMasterRepository;
import com.mhealth.admin.repository.HealthTipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Service
public class HealthTipService {

    @Autowired
    private HealthTipRepository repository;

    @Autowired
    private HealthTipCategoryMasterRepository categoryRepository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> createHealthTip(HealthTipRequest request, Locale locale) {
        HealthTipCategoryMaster category = categoryRepository.findById(request.getCategoryId())
                .orElse(null);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_NOT_FOUND, null, locale)));
        }

        String photoFileName = uploadFile(request.getPhoto());
        String videoFileName = uploadFile(request.getVideo());
        String videoThumbFileName = uploadFile(request.getVideoThumb());

        HealthTip healthTip = new HealthTip();
        healthTip.setHealthTipCategory(category);
        healthTip.setTopic(request.getTopic());
        healthTip.setDescription(request.getDescription());
        healthTip.setPhoto(photoFileName);
        healthTip.setVideo(videoFileName);
        healthTip.setVideoThumb(videoThumbFileName);
        healthTip.setStatus(request.getStatus());
        healthTip.setCreatedAt(new Date());

        repository.save(healthTip);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_CREATED_SUCCESS, null, locale)));
    }

    public ResponseEntity<Response> updateHealthTip(Integer id, HealthTipRequest request, Locale locale) {
        HealthTip healthTip = repository.findById(id).orElse(null);
        if (healthTip == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_NOT_FOUND, null, locale)));
        }

        HealthTipCategoryMaster category = categoryRepository.findById(request.getCategoryId())
                .orElse(null);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_NOT_FOUND, null, locale)));
        }

        String photoFileName = uploadFile(request.getPhoto());
        String videoFileName = uploadFile(request.getVideo());
        String videoThumbFileName = uploadFile(request.getVideoThumb());

        healthTip.setHealthTipCategory(category);
        healthTip.setTopic(request.getTopic());
        healthTip.setDescription(request.getDescription());
        healthTip.setPhoto(photoFileName);
        healthTip.setVideo(videoFileName);
        healthTip.setVideoThumb(videoThumbFileName);
        healthTip.setStatus(request.getStatus());
        healthTip.setUpdatedAt(new Date());

        repository.save(healthTip);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_UPDATED_SUCCESS, null, locale)));
    }

    public ResponseEntity<PaginationResponse<HealthTip>> searchHealthTips(HealthTipSearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<HealthTip> page = repository.findByTopicContainingAndStatus(request.getTopic(), request.getStatus(), pageable);

        return ResponseEntity.ok(new PaginationResponse<>(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_FETCHED_SUCCESS, null, locale),
                page.getContent(), page.getTotalElements(), (long) page.getSize(), (long) page.getNumber()));
    }

    public ResponseEntity<Response> deleteHealthTip(Integer id, Locale locale) {
        HealthTip healthTip = repository.findById(id).orElse(null);
        if (healthTip == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_NOT_FOUND, null, locale)));
        }

        repository.delete(healthTip);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_DELETED_SUCCESS, null, locale)));
    }

    public ResponseEntity<Response> updateStatus(Integer id, StatusAI status, Locale locale) {
        HealthTip healthTip = repository.findById(id).orElse(null);
        if (healthTip == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_NOT_FOUND, null, locale)));
        }

        healthTip.setStatus(status);
        healthTip.setUpdatedAt(new Date());
        repository.save(healthTip);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_STATUS_UPDATED_SUCCESS, null, locale)));
    }

    private String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        return UUID.randomUUID() + "_" + file.getOriginalFilename();
    }
}

