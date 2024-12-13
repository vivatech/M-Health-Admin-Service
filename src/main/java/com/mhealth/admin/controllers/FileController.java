package com.mhealth.admin.controllers;

import com.mhealth.admin.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequestMapping("/mobile")
public class FileController {

    @Autowired
    StorageService storageService;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @GetMapping("/file")
    @ResponseBody
    public ResponseEntity<Resource> serveFiles(@RequestParam String filename) {
        logger.info("fetching file");
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
