package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.request.LabSubCategoryRequest;
import com.mhealth.admin.dto.request.LabSubCategorySearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.LabCategoryMaster;
import com.mhealth.admin.model.LabSubCategoryMaster;
import com.mhealth.admin.repository.LabCategoryMasterRepository;
import com.mhealth.admin.repository.LabSubCategoryMasterRepository;
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
import java.util.Optional;

@Service
public class LabSubCategoryService {

    @Autowired
    private LabSubCategoryMasterRepository repository;

    @Autowired
    private LabCategoryMasterRepository labCategoryRepository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> addLabSubCategory(LabSubCategoryRequest request, Locale locale) {
        Optional<LabCategoryMaster> labCategoryOpt = labCategoryRepository.findById(request.getCatId());
        if (labCategoryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.LAB_CATEGORY_NOT_FOUND, null, locale)));
        }
        Optional<LabSubCategoryMaster> existLabSubCategory = repository.findBySubCatName(request.getSubCatName());
        if (existLabSubCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(Status.FAILED, Constants.CONFLICT_CODE,
                            messageSource.getMessage(Constants.LAB_SUB_CATEGORY_EXISTS, null, locale)));
        }

        LabSubCategoryMaster labSubCategory = new LabSubCategoryMaster();
        labSubCategory.setLabCategory(labCategoryOpt.get());
        labSubCategory.setSubCatName(request.getSubCatName());
        labSubCategory.setSubCatNameSl(request.getSubCatNameSl());
        labSubCategory.setSubCatStatus(request.getSubCatStatus() != null ? request.getSubCatStatus() :
                CategoryStatus.Active);
        labSubCategory.setIsHomeConsultantAvailable(request.getIsHomeConsultantAvailable() != null ?
                request.getIsHomeConsultantAvailable() : YesNo.No);
        labSubCategory.setSubCatCreatedAt(LocalDateTime.now());
        labSubCategory.setSubCatUpdatedAt(LocalDateTime.now());

        repository.save(labSubCategory);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_SUB_CATEGORY_ADDED, null, locale)));
    }

    public ResponseEntity<Response> updateLabSubCategory(Integer id, LabSubCategoryRequest request, Locale locale) {
        LabSubCategoryMaster labSubCategory = repository.findById(id).orElse(null);
        if (labSubCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.LAB_SUB_CATEGORY_NOT_FOUND, null, locale)));
        }

        if (request.getSubCatName() != null) labSubCategory.setSubCatName(request.getSubCatName());
        if (request.getSubCatNameSl() != null) labSubCategory.setSubCatNameSl(request.getSubCatNameSl());
        if (request.getSubCatStatus() != null) labSubCategory.setSubCatStatus(request.getSubCatStatus());
        if (request.getIsHomeConsultantAvailable() != null) labSubCategory.setIsHomeConsultantAvailable(request.getIsHomeConsultantAvailable());

        labSubCategory.setSubCatUpdatedAt(LocalDateTime.now());
        repository.save(labSubCategory);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_SUB_CATEGORY_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> changeStatus(Integer id, CategoryStatus status, Locale locale) {
        LabSubCategoryMaster labSubCategory = repository.findById(id).orElse(null);
        if (labSubCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.LAB_SUB_CATEGORY_NOT_FOUND, null, locale)));
        }

        labSubCategory.setSubCatStatus(status);
        labSubCategory.setSubCatUpdatedAt(LocalDateTime.now());
        repository.save(labSubCategory);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.STATUS_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> deleteLabSubCategory(Integer id, Locale locale) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.LAB_SUB_CATEGORY_NOT_FOUND, null, locale)));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_SUB_CATEGORY_DELETED, null, locale)));
    }

    public ResponseEntity<PaginationResponse<LabSubCategoryMaster>> searchLabSubCategories(LabSubCategorySearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<LabSubCategoryMaster> page = repository.findBySubCatNameContainingAndLabCategory_CatIdAndSubCatStatus(
                request.getSubCatName(), request.getCatId(), request.getSubCatStatus(), pageable);

        PaginationResponse<LabSubCategoryMaster> response = new PaginationResponse<>(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_SUB_CATEGORY_FETCHED, null, locale),
                page.getContent(),
                page.getTotalElements(),
                (long) page.getSize(),
                (long) page.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> findLabSubCategoryById(Integer id, Locale locale) {
        LabSubCategoryMaster labSubCategory = repository.findById(id).orElse(null);
        if (labSubCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.LAB_SUB_CATEGORY_NOT_FOUND, null, locale)));
        }

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_SUB_CATEGORY_FETCHED, null, locale), labSubCategory));
    }
}