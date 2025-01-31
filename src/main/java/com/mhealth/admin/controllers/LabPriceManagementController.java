package com.mhealth.admin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.labUserDto.LabUserRequestDto;
import com.mhealth.admin.dto.labpricedto.LabPriceRequestDto;
import com.mhealth.admin.service.LabPriceManagementService;
import com.mhealth.admin.service.LabUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Lab Price Management Controller", description = "APIs For Handling Lab price by lab id's Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/user/lab/lab-price")
public class LabPriceManagementController {

    @Autowired
    private LabPriceManagementService labPriceManagementService;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * Fetches a list of Lab price based on category and sub category list and the provided filters.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getListOfLabPricesByLabId(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                  @RequestParam Integer labId,
                                                  @RequestParam(required = false) Integer categoryId,
                                                  @RequestParam(required = false) Integer subCategoryId,
                                                  @RequestParam(required = false, defaultValue = "labPriceComment") String sortField,
                                                  @RequestParam(required = false, defaultValue = "1") String sortBy,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/lab-price/labId? api");
            log.info("Request Parameters: labId={}, categoryId={}, subCategoryId={}, sortField={}, sortBy={}, page={}, size={}"
                    , labId, categoryId, subCategoryId, sortField, sortBy, page, size);

            Object response = labPriceManagementService.getFilteredLabPrice(locale, labId, categoryId, subCategoryId, sortField, sortBy, page, size);

            log.info("Response Sent For /api/v1/admin/user/lab/lab-price/labId? by lab ID: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Creating new Lab price
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createLabUser(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                           @RequestParam Integer labId,
                                           @RequestBody LabPriceRequestDto labPriceRequestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/lab-price/create?labId=? ");
            log.info("Request Parameter: labId={}", labId);
            log.info("Request Body: {}", labPriceRequestDto);

            Object response = labPriceManagementService.createLabPrice(locale, labId, labPriceRequestDto);

            log.info("Response Sent For /api/v1/admin/user/lab/lab-price/create?labId=? : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Updating Lab price User
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateLabPrice(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                            @RequestParam Integer labId,
                                            @RequestBody LabPriceRequestDto labPriceRequestDto) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/lab-price/update ");
            log.info("Request Parameter: labId={}", labId);
            log.info("Request Body: {}", labPriceRequestDto);

            Object response = labPriceManagementService.updateLabPrice(locale, labId, labPriceRequestDto);

            log.info("Response Sent For /api/v1/admin/user/lab/lab-price/update: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Fetch Lab Management details by labId and labPriceId
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<?> getLabPriceDetails(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                @RequestParam(required = false) Integer labId,
                                                @RequestParam(required = false) Integer labPriceId) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/lab-price/get?labId=?&labPriceId=? ");
            log.info("Request Parameter: labId={}, labPriceId={}", labId, labPriceId);

            Object response = labPriceManagementService.getLabPriceDetails(locale, labId, labPriceId);

            log.info("Response Sent For /api/v1/admin/user/lab/lab-price/get?labId=?&labPriceId=? : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   /**
     * Delete labPrice management by labId
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteLabPrice(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                            @RequestParam Integer labId,
                                            @RequestParam Integer labPriceId) {
        try {
            log.info("Request Received For /api/v1/admin/user/lab/lab-price/delete ");
            log.info("Request Parameter: labId={}, labPriceId={}", labId, labPriceId);

            Object response = labPriceManagementService.deleteLabPrice(locale, labId, labPriceId);

            log.info("Response Sent For /api/v1/admin/user/lab/lab-price/delete  : {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
