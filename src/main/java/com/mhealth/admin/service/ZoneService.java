package com.mhealth.admin.service;
import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.ZoneRequest;
import com.mhealth.admin.dto.request.ZoneSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Zone;
import com.mhealth.admin.repository.ZoneRepository;
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
public class ZoneService {

    @Autowired
    private ZoneRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> addZone(ZoneRequest request, Locale locale) {
        if (repository.findByName(request.getName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(Status.FAILED, Constants.CONFLICT_CODE,
                            messageSource.getMessage(Constants.ZONE_EXISTS, null, locale)));
        }

        Zone zone = new Zone();
        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setStatus(request.getStatus() != null ? request.getStatus() : StatusAI.A);
        zone.setCreatedAt(LocalDateTime.now());
        repository.save(zone);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.ZONE_ADDED, null, locale)));
    }

    public ResponseEntity<Response> updateZone(Integer id, ZoneRequest request, Locale locale) {
        Zone zone = repository.findById(id).orElse(null);
        if (zone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.ZONE_NOT_FOUND, null, locale)));
        }

        if (request.getName() != null) zone.setName(request.getName());
        if (request.getDescription() != null) zone.setDescription(request.getDescription());
        if (request.getStatus() != null) zone.setStatus(request.getStatus());

        repository.save(zone);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.ZONE_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> changeStatus(Integer id, StatusAI status, Locale locale) {
        Zone zone = repository.findById(id).orElse(null);
        if (zone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.ZONE_NOT_FOUND, null, locale)));
        }

        zone.setStatus(status);
        repository.save(zone);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.STATUS_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> deleteZone(Integer id, Locale locale) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.ZONE_NOT_FOUND, null, locale)));
        }

        repository.deleteById(id);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.ZONE_DELETED, null, locale)));
    }

    public ResponseEntity<PaginationResponse<Zone>> searchZones(ZoneSearchRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Zone> page = repository.findByNameContainingAndStatus(
                request.getName(), request.getStatus(), pageable);

        PaginationResponse<Zone> response = new PaginationResponse<>(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.ZONE_FETCHED, null, locale),
                page.getContent(),
                page.getTotalElements(),
                (long) page.getSize(),
                (long) page.getNumber()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> findZoneById(Integer id, Locale locale) {
        Zone zone = repository.findById(id).orElse(null);
        if (zone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.ZONE_NOT_FOUND, null, locale)));
        }

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.ZONE_FETCHED, null, locale), zone));
    }
}