package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.SpecializationRequest;
import com.mhealth.admin.dto.request.SpecializationSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Specialization;
import com.mhealth.admin.repository.SpecializationRepository;
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
public class SpecializationService {

    @Autowired
    private SpecializationRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> addSpecialization(SpecializationRequest request, Locale locale) {
        if (repository.findByName(request.getName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(Status.FAILED, Constants.CONFLICT_CODE,
                            messageSource.getMessage(Constants.SPECIALIZATION_EXISTS, null, locale)));
        }

        Specialization specialization = Specialization.builder()
                .name(request.getName())
                .nameSl(request.getNameSl())
                .photo(request.getPhoto())
                .description(request.getDescription())
                .descriptionSl(request.getDescriptionSl())
                .status(request.getStatus())
                .isFeatured(request.getIsFeatured())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(specialization);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SPECIALIZATION_ADDED, null, locale)));
    }

    public ResponseEntity<Response> updateSpecialization(Integer id, SpecializationRequest request, Locale locale) {
        Specialization specialization = repository.findById(id).orElse(null);
        if (specialization == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.SPECIALIZATION_NOT_FOUND, null, locale)));
        }

        specialization.setName(request.getName());
        specialization.setNameSl(request.getNameSl());
        specialization.setPhoto(request.getPhoto());
        specialization.setDescription(request.getDescription());
        specialization.setDescriptionSl(request.getDescriptionSl());
        specialization.setIsFeatured(request.getIsFeatured());
        specialization.setStatus(request.getStatus());
        specialization.setUpdatedAt(LocalDateTime.now());

        repository.save(specialization);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SPECIALIZATION_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> changeStatus(Integer id, StatusAI status, Locale locale) {
        Specialization specialization = repository.findById(id).orElse(null);
        if (specialization == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.SPECIALIZATION_NOT_FOUND, null, locale)));
        }

        specialization.setStatus(status);
        specialization.setUpdatedAt(LocalDateTime.now());
        repository.save(specialization);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.STATUS_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> deleteSpecialization(Integer id, Locale locale) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.SPECIALIZATION_NOT_FOUND, null, locale)));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SPECIALIZATION_DELETED, null, locale)));
    }

    public ResponseEntity<PaginationResponse<Specialization>> searchSpecializations(SpecializationSearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Specialization> page = repository.findByNameContainingAndStatus(
                request.getName(), request.getStatus(), pageable);

        PaginationResponse<Specialization> response = new PaginationResponse<>(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SPECIALIZATION_FETCHED, null, locale),
                page.getContent(),
                page.getTotalElements(),
                (long) page.getSize(),
                (long) page.getNumber()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> findSpecializationById(Integer id, Locale locale) {
        Specialization specialization = repository.findById(id).orElse(null);
        if (specialization == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.SPECIALIZATION_NOT_FOUND, null, locale)));
        }

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SPECIALIZATION_FETCHED, null, locale), specialization));
    }
}