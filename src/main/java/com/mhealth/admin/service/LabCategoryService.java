package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.dto.request.LabCategoryRequest;
import com.mhealth.admin.dto.request.LabCategorySearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.LabCategoryMaster;
import com.mhealth.admin.repository.LabCategoryMasterRepository;
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
public class LabCategoryService {

    @Autowired
    private LabCategoryMasterRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> addLabCategory(LabCategoryRequest request, Locale locale) {
        if (repository.findByCatName(request.getCatName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(Status.FAILED, Constants.CONFLICT_CODE,
                            messageSource.getMessage(Constants.LAB_CATEGORY_EXISTS, null, locale)));
        }

        LabCategoryMaster labCategory = new LabCategoryMaster();
        labCategory.setCatName(request.getCatName());
        labCategory.setCatNameSl(request.getCatNameSl());
        labCategory.setProfilePicture(request.getProfilePicture());
        labCategory.setCatStatus(request.getCatStatus() != null ? request.getCatStatus() : CategoryStatus.Active);
        labCategory.setCatCreatedAt(LocalDateTime.now());
        labCategory.setCatUpdatedAt(LocalDateTime.now());

        repository.save(labCategory);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_CATEGORY_CREATED_SUCCESSFULLY, null, locale)));
    }

    public ResponseEntity<Response> updateLabCategory(Integer id, LabCategoryRequest request, Locale locale) {
        LabCategoryMaster labCategory = repository.findById(id).orElse(null);
        if (labCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.LAB_CATEGORY_NOT_FOUND, null, locale)));
        }

        if (request.getCatName() != null) labCategory.setCatName(request.getCatName());
        if (request.getCatNameSl() != null) labCategory.setCatNameSl(request.getCatNameSl());
        if (request.getProfilePicture() != null) labCategory.setProfilePicture(request.getProfilePicture());
        if (request.getCatStatus() != null) labCategory.setCatStatus(request.getCatStatus());

        labCategory.setCatUpdatedAt(LocalDateTime.now());
        repository.save(labCategory);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_CATEGORY_UPDATED_SUCCESSFULLY, null, locale)));
    }

    public ResponseEntity<Response> changeStatus(Integer id, CategoryStatus status, Locale locale) {
        LabCategoryMaster labCategory = repository.findById(id).orElse(null);
        if (labCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.LAB_CATEGORY_NOT_FOUND, null, locale)));
        }

        labCategory.setCatStatus(status);
        labCategory.setCatUpdatedAt(LocalDateTime.now());
        repository.save(labCategory);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.STATUS_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> deleteLabCategory(Integer id, Locale locale) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.LAB_CATEGORY_NOT_FOUND, null, locale)));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_CATEGORY_DELETED_SUCCESSFULLY, null, locale)));
    }

    public ResponseEntity<PaginationResponse<LabCategoryMaster>> searchLabCategories(LabCategorySearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<LabCategoryMaster> page = repository.findByCatNameContainingAndCatStatus(
                request.getCatName(), request.getCatStatus(), pageable);

        PaginationResponse<LabCategoryMaster> response = new PaginationResponse<>(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_CATEGORY_FETCHED_SUCCESSFULLY, null, locale),
                page.getContent(),
                page.getTotalElements(),
                (long) page.getSize(),
                (long) page.getNumber()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> findLabCategoryById(Integer id, Locale locale) {
        LabCategoryMaster labCategory = repository.findById(id).orElse(null);
        if (labCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.LAB_CATEGORY_NOT_FOUND, null, locale)));
        }

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.LAB_CATEGORY_FETCHED_SUCCESSFULLY, null, locale), labCategory));
    }
}