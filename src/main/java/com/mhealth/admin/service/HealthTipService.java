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
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class HealthTipService {

    @Autowired
    private HealthTipRepository repository;

    @Autowired
    private HealthTipCategoryMasterRepository categoryRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FileService fileService;
    @Value("${storage.location}")
    private String uploadDirectoryLocation;

    @Transactional
    public ResponseEntity<Response> createHealthTip(HealthTipRequest request, Locale locale) throws Exception {
        HealthTipCategoryMaster category = categoryRepository.findById(request.getCategoryId())
                .orElse(null);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.HEALTH_TIP_CATEGORY_NOT_FOUND, null, locale)));
        }

        //String videoThumbFileName = uploadFile(request.getVideoThumb());

        HealthTip healthTip = new HealthTip();
        healthTip.setHealthTipCategory(category);
        healthTip.setTopic(request.getTopic());
        healthTip.setDescription(request.getDescription());
        healthTip.setVideo(request.getVideo());
        //healthTip.setVideoThumb(videoThumbFileName);
        healthTip.setStatus(request.getStatus());
        healthTip.setCreatedAt(new Date());

        String newFileName = null;

        if (request.getPhoto() != null) {
            String extension = fileService.getFileExtension(Objects.requireNonNull(request.getPhoto().getOriginalFilename()));
            newFileName = UUID.randomUUID() + "." + extension;

            healthTip.setPhoto(newFileName);
        } else {
            healthTip.setPhoto("");
        }

        String newFileNameThumb = null;

        if (request.getVideoThumb() != null) {
            String extension = fileService.getFileExtension(Objects.requireNonNull(request.getVideoThumb().getOriginalFilename()));
            newFileNameThumb = UUID.randomUUID() + "." + extension;

            healthTip.setVideoThumb(newFileNameThumb);
        }

        healthTip = repository.save(healthTip);

        String filePath = null;

        if (request.getPhoto() != null) {
            filePath = com.mhealth.admin.constants.Constants.HEALTH_TIPS + healthTip.getHealthTipId();

            // Save the file
            fileService.saveFile(request.getPhoto(), filePath, newFileName);
        }

        if (request.getVideoThumb() != null) {
            filePath = com.mhealth.admin.constants.Constants.HEALTH_TIPS + healthTip.getHealthTipId() + com.mhealth.admin.constants.Constants.HEALTH_TIPS_VIDEO_THUMB;

            // Save the file
            fileService.saveFile(request.getVideoThumb(), filePath, newFileNameThumb);
        }

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_CREATED_SUCCESS, null, locale)));
    }

    public ResponseEntity<Response> updateHealthTip(Integer id, HealthTipRequest request, Locale locale) throws Exception {
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

//        String photoFileName = uploadFile(request.getPhoto());
//        String videoThumbFileName = uploadFile(request.getVideoThumb());

        healthTip.setHealthTipCategory(category);
        healthTip.setTopic(request.getTopic());
        healthTip.setDescription(request.getDescription());
        //healthTip.setPhoto(StringUtils.isEmpty(photoFileName) ? "" : photoFileName);
        healthTip.setVideo(request.getVideo());
        //healthTip.setVideoThumb(videoThumbFileName);
        healthTip.setStatus(request.getStatus());
        healthTip.setUpdatedAt(new Date());

        String filePath = null;
        if (request.getPhoto() != null) {

            filePath = com.mhealth.admin.constants.Constants.HEALTH_TIPS + healthTip.getHealthTipId();

            // delete exist old profile
            if (!StringUtils.isEmpty(healthTip.getPhoto())) {
                fileService.deleteFile(filePath, healthTip.getPhoto());
            }

            // Extract the file extension
            String extension = fileService.getFileExtension(Objects.requireNonNull(request.getPhoto().getOriginalFilename()));

            // Generate a random file name
            String fileName = UUID.randomUUID() + "." + extension;

            // Save the file
            fileService.saveFile(request.getPhoto(), filePath, fileName);

            healthTip.setPhoto(fileName);
        }

        if (request.getVideoThumb() != null) {

            filePath = com.mhealth.admin.constants.Constants.HEALTH_TIPS + healthTip.getHealthTipId() + com.mhealth.admin.constants.Constants.HEALTH_TIPS_VIDEO_THUMB;

            // delete exist old profile
            if (healthTip.getVideoThumb() != null) {
                fileService.deleteFile(filePath.trim(), healthTip.getVideoThumb());
            }

            // Extract the file extension
            String extension = fileService.getFileExtension(Objects.requireNonNull(request.getVideoThumb().getOriginalFilename()));

            // Generate a random file name
            String fileName = UUID.randomUUID() + "." + extension;

            // Save the file
            fileService.saveFile(request.getVideoThumb(), filePath.trim(), fileName);

            healthTip.setVideoThumb(fileName);
        }

        repository.save(healthTip);



        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_UPDATED_SUCCESS, null, locale)));
    }

    public ResponseEntity<PaginationResponse<HealthTip>> searchHealthTips(HealthTipSearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize() != null ? request.getSize() : Constants.DEFAULT_PAGE_SIZE);
        StatusAI status = StringUtils.isEmpty(request.getStatus()) ? null : StatusAI.valueOf(request.getStatus());
        Page<HealthTip> page = repository.findByTopicContainingAndStatus(request.getTopic(), status, pageable);

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

