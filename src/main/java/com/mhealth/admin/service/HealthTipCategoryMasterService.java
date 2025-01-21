package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipCategoryRequest;
import com.mhealth.admin.dto.request.HealthTipCategorySearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.HealthTip;
import com.mhealth.admin.model.HealthTipCategoryMaster;
import com.mhealth.admin.model.HealthTipPackageCategories;
import com.mhealth.admin.repository.HealthTipCategoryMasterRepository;
import com.mhealth.admin.repository.HealthTipPackageCategoriesRepository;
import com.mhealth.admin.repository.HealthTipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class HealthTipCategoryMasterService {

    @Autowired
    private HealthTipCategoryMasterRepository repository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private HealthTipPackageCategoriesRepository healthTipPackageCategoriesRepository;
    @Autowired
    private HealthTipRepository healthTipRepository;

    public ResponseEntity<Response> addCategory(HealthTipCategoryRequest request, Locale locale) {
        String newFileName = uploadFile(request.getPhoto());
        HealthTipCategoryMaster category = new HealthTipCategoryMaster();
        category.setName(request.getName());
        category.setNameSl(request.getNameSl());
        category.setDescription(request.getDescription());
        category.setDescriptionSl(request.getDescriptionSl());
        category.setPhoto(newFileName);
        category.setStatus(request.getStatus());
        category.setIsFeatured(request.getIsFeatured());
        category.setPriority(request.getPriority());
        category.setCreatedAt(LocalDateTime.now());

        repository.save(category);

        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_ADDED_SUCCESSFULLY, null, locale)));
    }

    public ResponseEntity<Response> updateCategory(Integer id, HealthTipCategoryRequest request, Locale locale) {
        HealthTipCategoryMaster category = repository.findById(id).orElse(null);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_NOT_FOUND, null, locale)));
        }

        String newFileName = uploadFile(request.getPhoto());
        category.setName(request.getName());
        category.setNameSl(request.getNameSl());
        category.setDescription(request.getDescription());
        category.setDescriptionSl(request.getDescriptionSl());
        category.setPhoto(newFileName);
        category.setStatus(request.getStatus());
        category.setIsFeatured(request.getIsFeatured());
        category.setPriority(request.getPriority());
        category.setUpdatedAt(LocalDateTime.now());

        repository.save(category);

        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_UPDATED_SUCCESSFULLY, null, locale)));
    }

    public ResponseEntity<Response> changeStatus(Integer id, StatusAI status, Locale locale) {
        HealthTipCategoryMaster category = repository.findById(id).orElse(null);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_NOT_FOUND, null, locale)));
        }

        category.setStatus(status);
        category.setUpdatedAt(LocalDateTime.now());
        repository.save(category);

        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_STATUS_UPDATED_SUCCESSFULLY, null, locale)));
    }

    public ResponseEntity<PaginationResponse<HealthTipCategoryMaster>> searchCategories(
            HealthTipCategorySearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize() != null ? request.getSize() : Constants.DEFAULT_PAGE_SIZE);
        Page<HealthTipCategoryMaster> page = repository.findByNameContainingAndStatus(
                request.getName(), ObjectUtils.isEmpty(request.getStatus()) ? null : StatusAI.valueOf(request.getStatus()), pageable);

        if (page.getContent().isEmpty()) {
            return ResponseEntity.ok(new PaginationResponse<>(
                    Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.HEALTH_TIP_CATEGORIES_FETCHED_EMPTY, null, locale),
                    page.getContent(), page.getTotalElements(),
                    Long.valueOf(page.getSize()), Long.valueOf(page.getNumber())));
        }

        return ResponseEntity.ok(new PaginationResponse<>(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_CATEGORIES_FETCHED_SUCCESSFULLY, null, locale),
                page.getContent(), page.getTotalElements(),
                Long.valueOf(page.getSize()), Long.valueOf(page.getNumber())));
    }

    public ResponseEntity<Response> deleteCategory(Integer id, Locale locale) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_NOT_FOUND, null, locale)));
        }
        HealthTipCategoryMaster category = repository.findById(id).orElse(null);

        // verify if category is used in any health tip package
        HealthTipPackageCategories healthTipPackageCategories = healthTipPackageCategoriesRepository.findByHealthTipCategoryMaster(category);
        if (healthTipPackageCategories != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(Status.FAILED, Constants.FAILED_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_USED_IN_HEALTH_TIP_PACKAGE, null, locale)));
        }

        List<HealthTip> healthTip = healthTipRepository.findByCategory(category);
        if(!healthTip.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(Status.FAILED, Constants.FAILED_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_USED_IN_HEALTH_TIP_PACKAGE, null, locale)));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_DELETED_SUCCESSFULLY, null, locale)));
    }

    private String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        String newFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // Handle file upload here
        return newFileName;
    }
}