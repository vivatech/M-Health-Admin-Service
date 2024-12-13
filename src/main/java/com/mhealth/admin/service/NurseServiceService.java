package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.NurseServiceRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.NurseService;
import com.mhealth.admin.repository.NurseServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class NurseServiceService {
    @Autowired
    private NurseServiceRepository repository;

    @Autowired
    private MessageSource messageSource;



    public ResponseEntity<Response> updateNurseServiceStatus(Integer id, String status, Locale locale) {
        Optional<NurseService> optionalNurseService = repository.findById(id);

        if (optionalNurseService.isEmpty()) {
            Response response = new Response(
                    Status.FAILED,
                    Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.NURSE_SERVICE_NOT_FOUND, null, locale)
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        NurseService nurseService = optionalNurseService.get();
        nurseService.setStatus(status);
        nurseService.setUpdatedAt(LocalDateTime.now());
        repository.save(nurseService);

        Response response = new Response(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.NURSE_SERVICE_STATUS_UPDATED, null, locale),
                nurseService
        );
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> createNurseService(NurseServiceRequest request, Locale locale) {
        NurseService nurseService = new NurseService(
                null,
                request.getSeviceName(),
                request.getServiceImage(),
                request.getServicePrice(),
                request.getSeviceNameSl(),
                request.getDescriptionSl(),
                request.getAdminCommission(),
                request.getTotalServicePrice(),
                request.getCommissionType(),
                request.getStatus(),
                request.getDescription(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        repository.save(nurseService);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.NURSE_SERVICE_CREATED, null, locale), nurseService);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> updateNurseService(Integer id, NurseServiceRequest request, Locale locale) {
        NurseService nurseService = repository.findById(id).orElse(null);

        if (nurseService == null) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.NURSE_SERVICE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        nurseService.setSeviceName(request.getSeviceName());
        nurseService.setServiceImage(request.getServiceImage());
        nurseService.setServicePrice(request.getServicePrice());
        nurseService.setSeviceNameSl(request.getSeviceNameSl());
        nurseService.setDescriptionSl(request.getDescriptionSl());
        nurseService.setAdminCommission(request.getAdminCommission());
        nurseService.setTotalServicePrice(request.getTotalServicePrice());
        nurseService.setCommissionType(request.getCommissionType());
        nurseService.setStatus(request.getStatus());
        nurseService.setDescription(request.getDescription());
        nurseService.setUpdatedAt(LocalDateTime.now());

        repository.save(nurseService);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.NURSE_SERVICE_UPDATED, null, locale), nurseService);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> getAllNurseServices(Locale locale) {
        List<NurseService> nurseServices = repository.findAll();

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.NURSE_SERVICE_FETCHED, null, locale), nurseServices);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> getNurseServiceById(Integer id, Locale locale) {
        NurseService nurseService = repository.findById(id).orElse(null);

        if (nurseService == null) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.NURSE_SERVICE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.NURSE_SERVICE_FETCHED, null, locale), nurseService);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> deleteNurseServiceById(Integer id, Locale locale) {
        if (!repository.existsById(id)) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.NURSE_SERVICE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        repository.deleteById(id);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.NURSE_SERVICE_DELETED, null, locale));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> searchNurseServices(String seviceName, String status, Locale locale) {
        List<NurseService> nurseServices = repository
                .findBySeviceNameContainingIgnoreCaseAndStatusContainingIgnoreCase(
                Optional.ofNullable(seviceName).orElse(""),
                Optional.ofNullable(status).orElse("")
        );

        if (nurseServices.isEmpty()) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.NURSE_SERVICE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.NURSE_SERVICE_FETCHED, null, locale), nurseServices);
        return ResponseEntity.ok(response);
    }
}
