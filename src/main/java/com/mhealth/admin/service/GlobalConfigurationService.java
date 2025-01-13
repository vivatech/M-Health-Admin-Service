package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.GlobalConfigurationRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.GlobalConfiguration;
import com.mhealth.admin.repository.GlobalConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class GlobalConfigurationService {

    @Autowired
    private GlobalConfigurationRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> getConfigurationById(Integer id,Locale locale) {
        GlobalConfiguration configuration = repository.findById(id)
                .orElseThrow(null);
        if(configuration!=null){
            Response response = new Response(
                    Status.SUCCESS, Constants.SUCCESS_CODE,
                    messageSource.getMessage(Constants.GLOBAL_CONFIG_FETCH_SUCCESSFULLY,null,locale), configuration);
            return ResponseEntity.ok(response);
        }else{
            Response response = new Response(
                    Status.FAILED,
                    Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.GLOBAL_CONFIG_NOT_FOUND, null, locale)
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    public ResponseEntity<Response> deleteConfigurationById(Integer id,Locale locale) {
        if (!repository.existsById(id)) {
            Response response = new Response(
                    Status.FAILED,
                    Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.GLOBAL_CONFIG_NOT_FOUND, null, locale)
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        repository.deleteById(id);
        Response response = new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.GLOBAL_CONFIG_DELETED_SUCCESSFULLY,null,locale) );
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> createConfiguration(GlobalConfigurationRequest request, Locale locale) {
        GlobalConfiguration config = new GlobalConfiguration(
                null,
                request.getKey(),
                request.getValue(),
                request.getDescription(),
                request.getDisplayOrder()
        );

        if (repository.findByKey(config.getKey()).orElse(null)!=null) {
            Response response = new Response(
                    Status.FAILED,
                    Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.GLOBAL_CONFIG_NOT_FOUND, null, locale)
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }else{
            config = repository.save(config);
            Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                    messageSource.getMessage(Constants.GLOBAL_CONFIG_CREATED_SUCCESSFULLY,null,locale), config);
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<Response> updateConfiguration(Integer id, GlobalConfigurationRequest request, Locale locale) {
        Optional<GlobalConfiguration> optionalConfig = repository.findById(id);
        if (!optionalConfig.isPresent()) {
            Response response = new Response(
                    Status.FAILED,
                    Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.GLOBAL_CONFIG_NOT_FOUND, null, locale)
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        GlobalConfiguration existingConfig = optionalConfig.get();
        existingConfig.setKey(request.getKey());
        existingConfig.setValue(request.getValue());
        existingConfig.setDescription(request.getDescription());
        existingConfig.setDisplayOrder(request.getDisplayOrder());

        repository.save(existingConfig);

        Response response = new Response(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.GLOBAL_CONFIG_UPDATED_SUCCESSFULLY, null, locale),
                existingConfig
        );
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> searchConfiguration(String key, String value, Locale locale) {
        key = (key==null)?"":key;
        value = (value==null)?"":value;
        List<GlobalConfiguration> configurations = repository.findAllByDisplayOrderKeyAndValue(key,value);
        Response response = new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.GLOBAL_CONFIG_FETCH_SUCCESSFULLY,null,locale), configurations);
        return ResponseEntity.ok(response);
    }
}
