package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.LabPriceDto;
import com.mhealth.admin.dto.request.LabPriceRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.LabCategoryMaster;
import com.mhealth.admin.model.LabPrice;
import com.mhealth.admin.model.LabSubCategoryMaster;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.LabCategoryMasterRepository;
import com.mhealth.admin.repository.LabPriceRepository;
import com.mhealth.admin.repository.LabSubCategoryMasterRepository;
import com.mhealth.admin.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class LabPriceService {

    @Autowired
    private LabPriceRepository labPriceRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LabCategoryMasterRepository labCategoryMasterRepository;

    @Autowired
    private LabSubCategoryMasterRepository labSubCategoryMasterRepository;

    @Autowired
    private UsersRepository usersRepository;

    public ResponseEntity<Response> updateLabPrice(Integer id, LabPriceRequest request, Locale locale) {
        LabPrice labPrice = labPriceRepository.findById(id).orElse(null);
        if (labPrice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage("lab.price.not.found", null, locale)));
        }

        if (request.getCategoryId() != null) {
            LabCategoryMaster category = labCategoryMasterRepository.findById(request.getCategoryId()).orElse(null);
            if (category != null) {
                labPrice.setCatId(category);
            }
        }

        if (request.getSubCategoryId() != null) {
            LabSubCategoryMaster subCategory = labSubCategoryMasterRepository.findById(request.getSubCategoryId()).orElse(null);
            if (subCategory != null) {
                labPrice.setSubCatId(subCategory);
            }
        }

        if (request.getLabPrice() != null) {
            labPrice.setLabPrice(request.getLabPrice());
        }

        if (request.getLabPriceComment() != null && !request.getLabPriceComment().isEmpty()) {
            labPrice.setLabPriceComment(request.getLabPriceComment());
        }

        labPrice.setLabPriceUpdatedAt(LocalDateTime.now());
        labPriceRepository.save(labPrice);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage("lab.price.updated.success", null, locale)));
    }

    public ResponseEntity<Response> createLabPrice(LabPriceRequest request, Locale locale) {
        LabCategoryMaster category = labCategoryMasterRepository.findById(request.getCategoryId())
                .orElse(null);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage("lab.category.not.found", null, locale)));
        }

        LabSubCategoryMaster subCategory = labSubCategoryMasterRepository.findById(request.getSubCategoryId())
                .orElse(null);
        if (subCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage("lab.subcategory.not.found", null, locale)));
        }

        Users labUser = usersRepository.findById(request.getLabUserId()).orElse(null);
        if (labUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage("lab.user.not.found", null, locale)));
        }

        LabPrice labPrice = new LabPrice();
        labPrice.setLabUser(labUser);
        labPrice.setCatId(category);
        labPrice.setSubCatId(subCategory);
        labPrice.setLabPrice(request.getLabPrice());
        labPrice.setLabPriceComment(request.getLabPriceComment());
        labPrice.setLabPriceCreatedAt(LocalDateTime.now());
        labPrice.setLabPriceUpdatedAt(LocalDateTime.now());

        labPriceRepository.save(labPrice);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage("lab.price.created.success", null, locale)));
    }

    public ResponseEntity<Response> findLabPriceById(Integer id, Locale locale) {
        LabPrice labPrice = labPriceRepository.findById(id).orElse(null);
        if (labPrice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage("lab.price.not.found", null, locale)));
        }
        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage("lab.price.fetched.success", null, locale)));
    }

    public ResponseEntity<PaginationResponse<LabPriceDto>> searchLabPrices(
            Integer categoryId, Integer subCategoryId, int page, int size, Locale locale) {

        Pageable pageable = PageRequest.of(page, size);
        Page<LabPrice> labPrices = labPriceRepository.findByCategoryAndSubCategory(categoryId, subCategoryId, pageable);

        if (labPrices.isEmpty()) {
            return ResponseEntity.ok(new PaginationResponse<>(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage("lab.price.no.records", null, locale),
                    Collections.emptyList(), labPrices.getTotalElements(), (long) labPrices.getSize(), (long) labPrices.getNumber()));
        }

        // Map LabPrice to LabPriceDto
        List<LabPriceDto> labPriceDtos = labPrices.getContent().stream()
                .map(labPrice -> new LabPriceDto(
                        labPrice.getLabPriceId(),
                        labPrice.getCatId().getCatName(),
                        labPrice.getSubCatId().getSubCatName(),
                        labPrice.getLabPrice(),
                        labPrice.getLabPriceComment(),
                        labPrice.getCatId().getCatId(),
                        labPrice.getSubCatId().getSubCatId()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PaginationResponse<>(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage("lab.price.records.fetched", null, locale),
                labPriceDtos, labPrices.getTotalElements(), (long) labPrices.getSize(), (long) labPrices.getNumber()));
    }

    public ResponseEntity<Response> deleteLabPrice(Integer id, Locale locale) {
        LabPrice labPrice = labPriceRepository.findById(id).orElse(null);
        if (labPrice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage("lab.price.not.found", null, locale)));
        }

        labPriceRepository.delete(labPrice);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage("lab.price.deleted.success", null, locale)));
    }

}
