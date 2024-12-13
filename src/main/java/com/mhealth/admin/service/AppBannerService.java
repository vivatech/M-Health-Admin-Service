package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.AppBannerRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.AppBanner;
import com.mhealth.admin.repository.AppBannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class AppBannerService {

    @Autowired
    private AppBannerRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> createAppBanner(AppBannerRequest request, Locale locale) {
        AppBanner banner = new AppBanner(
                null,
                request.getType(),
                request.getIname(),
                request.getVname(),
                request.getSortOrder(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        repository.save(banner);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE, messageSource.getMessage(Constants.APP_BANNER_CREATED_SUCCESSFULLY,null,locale), banner);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> updateAppBanner(Integer id, AppBannerRequest request,Locale locale) {
        AppBanner banner = repository.findById(id).orElse(null);

        if (banner == null) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE, messageSource.getMessage(Constants.APP_BANNER_NOT_FOUND,null,locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        banner.setType(request.getType());
        banner.setIname(request.getIname());
        banner.setVname(request.getVname());
        banner.setSortOrder(request.getSortOrder());
        banner.setUpdatedAt(LocalDateTime.now());

        repository.save(banner);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE, messageSource.getMessage(Constants.APP_BANNER_UPDATED_SUCCESSFULLY,null,locale), banner);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> getAllAppBanners(Locale locale) {
        List<AppBanner> banners = repository.findAll();

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE, messageSource.getMessage(Constants.APP_BANNER_FETCHED_SUCCESSFULLY,null,locale), banners);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> getAppBannerById(Integer id,Locale locale) {
        AppBanner banner = repository.findById(id).orElse(null);

        if (banner == null) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE, messageSource.getMessage(Constants.APP_BANNER_NOT_FOUND,null,locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE, messageSource.getMessage(Constants.APP_BANNER_FETCHED_SUCCESSFULLY,null,locale), banner);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> deleteAppBannerById(Integer id,Locale locale) {
        if (!repository.existsById(id)) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE, messageSource.getMessage(Constants.APP_BANNER_NOT_FOUND,null,locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        repository.deleteById(id);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE, messageSource.getMessage(Constants.APP_BANNER_DELETED_SUCCESSFULLY,null,locale));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> searchAppBanners(String iname,Locale locale) {
        List<AppBanner> banners = repository.searchByIname(iname);

        if (banners.isEmpty()) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE, messageSource.getMessage(Constants.APP_BANNER_NOT_FOUND,null,locale));
            return ResponseEntity.ok().body(response);
        }

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE, messageSource.getMessage(Constants.APP_BANNER_FETCHED_SUCCESSFULLY,null,locale), banners);
        return ResponseEntity.ok(response);
    }
}
