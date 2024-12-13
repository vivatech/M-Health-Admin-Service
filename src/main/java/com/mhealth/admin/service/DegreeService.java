package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Degree;
import com.mhealth.admin.repository.DegreeRepository;
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
public class DegreeService {

    @Autowired
    private DegreeRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> addDegree(Degree degreeRequest, Locale locale) {
        Degree degree = new Degree();
        degree.setName(degreeRequest.getName());
        degree.setDescription(degreeRequest.getDescription());
        degree.setStatus(degreeRequest.getStatus());
        degree.setCreatedAt(LocalDateTime.now());

        repository.save(degree);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.DEGREE_ADDED, null, locale)));
    }

    public ResponseEntity<Response> updateDegree(Integer id, Degree degreeRequest, Locale locale) {
        Optional<Degree> existingDegree = repository.findById(id);
        if (existingDegree.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.DEGREE_NOT_FOUND, null, locale)));
        }

        Degree updatedDegree = existingDegree.get();
        if(degreeRequest.getName()!=null && !degreeRequest.getName().isEmpty()){
            updatedDegree.setName(degreeRequest.getName());
        }
        if(degreeRequest.getDescription()!=null && !degreeRequest.getDescription().isEmpty()){
            updatedDegree.setDescription(degreeRequest.getDescription());
        }
        if(degreeRequest.getStatus()!=null){
            updatedDegree.setStatus(degreeRequest.getStatus());
        }

        repository.save(updatedDegree);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.DEGREE_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> changeStatus(Integer id, StatusAI status, Locale locale) {
        Optional<Degree> degree = repository.findById(id);
        if (!degree.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.DEGREE_NOT_FOUND, null, locale)));
        }

        Degree updatedDegree = degree.get();
        updatedDegree.setStatus(status);
        repository.save(updatedDegree);
        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.STATUS_UPDATED, null, locale)));
    }

    public ResponseEntity<Response> deleteDegree(Integer id, Locale locale) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.DEGREE_NOT_FOUND, null, locale)));
        }

        repository.deleteById(id);
        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.DEGREE_DELETED, null, locale)));
    }

    public ResponseEntity<List<Degree>> getAllDegrees() {
        List<Degree> degrees = repository.findAll();
        return ResponseEntity.ok(degrees);
    }

    public ResponseEntity<List<Degree>> searchDegrees(String name, StatusAI status) {
        List<Degree> degrees = repository.findByNameContainingAndStatus(name, status);
        return ResponseEntity.ok(degrees);
    }
}
