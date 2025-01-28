package com.mhealth.admin.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.service.ImageGalleryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Image Gallery", description = "APIs For Handling Image Gallery Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/image/gallery")
public class ImageGalleryController {

    @Autowired
    private ImageGalleryService imageGalleryService;

    @Autowired
    private ObjectMapper objectMapper;

    // Upload Image
    @PostMapping(value = "/create")
    public ResponseEntity<?> uploadImage(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale, @RequestParam("file") MultipartFile file) {
        try {
            log.info("Request Received For /api/v1/admin/image/gallery/create");
            log.info("Request Param {}", file.getOriginalFilename());

            Object response = imageGalleryService.saveImage(file, locale);

            log.info("Response Sent For /api/v1/admin/image/gallery/create: {}", objectMapper.writeValueAsString(response));

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteImage(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                      @RequestParam Integer id) {
        try {
            log.info("Request Received For /api/v1/admin/media/gallery/delete");
            log.info("Request Parameter: id={}", id);

            Object response = imageGalleryService.deleteImage(locale, id);

            log.info("Response Sent For /api/v1/admin/media/gallery/delete: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> getImageGalleryList(
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page, // Adjusted default value
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            log.info("Request Received For /api/v1/admin/image/gallery/list");
            log.info("Request Parameters: name={}, page={}, size={}, sortBy={}", name, page, size, sortBy);

            Object response = imageGalleryService.getImageGallery(locale, sortBy, name, page, size);

            log.info("Response Sent For /api/v1/admin/image/gallery/list: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
