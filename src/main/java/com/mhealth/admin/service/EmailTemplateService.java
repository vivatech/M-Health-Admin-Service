package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.EmailTemplateRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.EmailTemplate;
import com.mhealth.admin.repository.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class EmailTemplateService {
    @Autowired
    private EmailTemplateRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> createEmailTemplate(EmailTemplateRequest request, Locale locale) {
        if (repository.findByKey(request.getKey()).isPresent()) {
            Response response = new Response(
                    Status.FAILED, Constants.CONFLICT_CODE,
                    messageSource.getMessage(Constants.EMAIL_TEMPLATE_EXISTS, null, locale));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        EmailTemplate template = new EmailTemplate(
                null,
                request.getKey(),
                request.getValue(),
                request.getSubject(),
                request.getContent(),
                new Date(),
                null
        );

        repository.save(template);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.EMAIL_TEMPLATE_CREATED, null, locale), template);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> updateEmailTemplate(Integer id, EmailTemplateRequest request, Locale locale) {
        EmailTemplate template = repository.findById(id).orElse(null);

        if (template == null) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.EMAIL_TEMPLATE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        template.setKey(request.getKey());
        template.setValue(request.getValue());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setUpdatedAt(new Date());

        repository.save(template);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.EMAIL_TEMPLATE_UPDATED, null, locale), template);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> searchEmailTemplates(String key, String value, Locale locale) {
        List<EmailTemplate> templates = repository.searchByKeyAndValue(key, value);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.EMAIL_TEMPLATE_FETCHED, null, locale), templates);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> getAllEmailTemplates(Locale locale) {
        List<EmailTemplate> templates = repository.findAll();

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.EMAIL_TEMPLATE_FETCHED, null, locale), templates);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> getEmailTemplateById(Integer id, Locale locale) {
        EmailTemplate template = repository.findById(id).orElse(null);

        if (template == null) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.EMAIL_TEMPLATE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.EMAIL_TEMPLATE_FETCHED, null, locale), template);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> deleteEmailTemplateById(Integer id, Locale locale) {
        if (!repository.existsById(id)) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.EMAIL_TEMPLATE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        repository.deleteById(id);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.EMAIL_TEMPLATE_DELETED, null, locale));
        return ResponseEntity.ok(response);
    }
}
