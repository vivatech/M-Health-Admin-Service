package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.ConsultationStatus;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.ConsultationRating;
import com.mhealth.admin.repository.ConsultationRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class ConsultationRatingService {

    @Autowired
    private ConsultationRatingRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<PaginationResponse<ConsultationRating>> getPaginatedRatings(
            int page, int size, Locale locale) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationRating> ratingsPage = repository.findAll(pageable);

        return ResponseEntity.ok(new PaginationResponse<>(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.RATINGS_FETCHED_SUCCESS,null,locale),
                ratingsPage.getContent(),
                ratingsPage.getTotalElements(),
                (long) ratingsPage.getSize(),
                (long) ratingsPage.getNumber()
        ));
    }

    public ResponseEntity<Response> changeRatingStatus(Integer id, ConsultationStatus status, Locale locale) {
        ConsultationRating rating = repository.findById(id).orElse(null);
        if (rating == null) {
            return ResponseEntity.status(404)
                    .body(new Response(
                            Status.FAILED,
                            Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.CONSULTATION_RATING_NOT_FOUND,null,locale),
                            null
                    ));
        }

        rating.setStatus(status);
        rating.setUpdatedAt(LocalDateTime.now());
        repository.save(rating);

        return ResponseEntity.ok(new Response(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.CONSULTATION_RATING_STATUS_UPDATED_SUCCESS,null,locale),
                null
        ));
    }
}
