package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
@Slf4j
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin")
public class AdminFileController {

    @Value("${storage.location}")
    private String path;


    //get file from the given path
    @GetMapping("/download-file")
    public Response downloadFile(@RequestParam String filePath) {
        Response responseDto = new Response();
        try {
            // Get the file bytes
            Path fullPath = Paths.get(path + filePath);

            // Read the file from the given file path
            byte[] fileBytes = Files.readAllBytes(fullPath);

            // Detect the file type (MIME type)
            String mimeType = Files.probeContentType(fullPath);
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // Fallback if MIME type is unknown
            }

            // Convert file bytes to Base64 format
            String base64File = Base64.getEncoder().encodeToString(fileBytes);

            // Combine MIME type with Base64 data
            String dataWithMimeType = "data:" + mimeType + ";base64," + base64File;

            // Return the Base64 encoded file
            responseDto.setStatus(Status.SUCCESS);
            responseDto.setMessage("File Found");
            responseDto.setData(dataWithMimeType);
            responseDto.setCode(Constants.SUCCESS_CODE);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in fetching file : {}", e);
            responseDto.setStatus(Status.NO_CONTENT);
            responseDto.setMessage("Internal Server Error");
            responseDto.setCode(Constants.INTERNAL_SERVER_ERROR);
        }
        return responseDto;
    }
}
