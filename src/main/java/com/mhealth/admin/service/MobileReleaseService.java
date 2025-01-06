package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.MobileReleaseRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.MobileRelease;
import com.mhealth.admin.repository.MobileReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class MobileReleaseService {

    @Autowired
    private MobileReleaseRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> createMobileRelease(MobileReleaseRequest request, Locale locale) {
        MobileRelease mobileRelease = new MobileRelease(
                null,
                request.getAppVersion(),
                request.getClientName(),
                request.getIsDeprecated(),
                request.getIsTerminated(),
                request.getMessage(),
                request.getUserType(),
                request.getDeviceType(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        repository.save(mobileRelease);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.MOBILE_RELEASE_CREATED, null, locale), mobileRelease);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> updateMobileRelease(Integer id, MobileReleaseRequest request, Locale locale) {
        MobileRelease mobileRelease = repository.findById(id).orElse(null);

        if (mobileRelease == null) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.MOBILE_RELEASE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        mobileRelease.setAppVersion(request.getAppVersion());
        mobileRelease.setClientName(request.getClientName());
        mobileRelease.setIsDeprecated(request.getIsDeprecated());
        mobileRelease.setIsTerminated(request.getIsTerminated());
        mobileRelease.setMessage(request.getMessage());
        mobileRelease.setUserType(request.getUserType());
        mobileRelease.setDeviceType(request.getDeviceType());
        mobileRelease.setUpdatedAt(LocalDateTime.now());

        repository.save(mobileRelease);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.MOBILE_RELEASE_UPDATED, null, locale), mobileRelease);
        return ResponseEntity.ok(response);
    }


    public ResponseEntity<Response> getMobileReleaseById(Integer id, Locale locale) {
        MobileRelease mobileRelease = repository.findById(id).orElse(null);

        if (mobileRelease == null) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.MOBILE_RELEASE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.MOBILE_RELEASE_FETCHED, null, locale), mobileRelease);
        return ResponseEntity.ok(response);
    }


    public ResponseEntity<Response> searchMobileReleaseByAppVersion(String appVersion, Locale locale, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MobileRelease> mobileReleases = repository.findByAppVersionContainingIgnoreCase(appVersion, pageable);

        if (mobileReleases.isEmpty()) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.MOBILE_RELEASE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Create a simplified response with only content and totalElements
        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.MOBILE_RELEASE_FETCHED, null, locale),
                mobileReleases.getContent(), mobileReleases.getTotalElements());

        return ResponseEntity.ok(response);
    }


}
