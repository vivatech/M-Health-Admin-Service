package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.SlotTypeRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.SlotType;
import com.mhealth.admin.repository.SlotTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class SlotTypeService {
    @Autowired
    private SlotTypeRepository repository;

    @Autowired
    private MessageSource messageSource;

    public ResponseEntity<Response> updateSlotType(Integer id, SlotTypeRequest request, Locale locale) {
        SlotType slotType = repository.findById(id).orElse(null);

        if (slotType == null) {
            Response response = new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.SLOT_TYPE_NOT_FOUND, null, locale));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        slotType.setType(request.getType());
        slotType.setValue(request.getValue());
        slotType.setStatus(request.getStatus());

        repository.save(slotType);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SLOT_TYPE_UPDATED, null, locale), slotType);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> getAllSlotTypes(Locale locale) {
        List<SlotType> slotTypes = repository.findAll();

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SLOT_TYPE_FETCHED, null, locale), slotTypes);
        return ResponseEntity.ok(response);
    }
}
