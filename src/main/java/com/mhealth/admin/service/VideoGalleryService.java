package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.GalleryResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.VideoGallery;
import com.mhealth.admin.repository.VideoGalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class VideoGalleryService {

    @Autowired
    private VideoGalleryRepository videoGalleryRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private MessageSource messageSource;

    public Object getVideos(Locale locale,String sortBy, String name, int page, int size) {

        // Default to sorting by "id" if sortBy is null or empty
        String defaultSortBy = "id";
        Sort sort = (sortBy != null && !sortBy.isEmpty()) ? Sort.by(sortBy) : Sort.by(defaultSortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VideoGallery> result;
        if (name != null && !name.isEmpty()) {
            result = videoGalleryRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            result = videoGalleryRepository.findAll(pageable);
        }

        List<GalleryResponseDto> dtoList = result.getContent().stream()
                .map(image -> new GalleryResponseDto(
                        image.getId(),
                        image.getName(),
                        "/" + Constants.VIDEO_GALLERY + image.getId() + "/" + image.getName() // Assuming file location
                ))
                .toList();

        // Build response
        Map<String, Object> data = new HashMap<>();
        data.put("data", dtoList);
        data.put("totalCount", result.getTotalElements());

        Response response = new Response();
        response.setCode(Constants.CODE_1);
        response.setData(data);
        response.setMessage(messageSource.getMessage(Messages.GALLERY_LIST_FETCHED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }


    @Transactional
    public Object deleteVideo(Locale locale, Integer id) throws Exception {
        Response response = new Response();

        videoGalleryRepository.deleteById(id);

        String filePath = Constants.VIDEO_GALLERY + id;

        // delete file and folder
        fileService.deleteFileOrFolder(filePath);
        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.FILE_DELETED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object saveVideo(MultipartFile file, Locale locale) throws IOException {
        Response response = new Response();

        // Save the file locally
        String fileName = file.getOriginalFilename();

        VideoGallery existingVideo = videoGalleryRepository.findByName(fileName);

        if (existingVideo != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.FILE_NAME_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Optionally, save the file details to the database
        VideoGallery videoGallery = new VideoGallery();
        videoGallery.setName(fileName);
        videoGallery = videoGalleryRepository.save(videoGallery);

        // upload video gallery
        String filePath = Constants.VIDEO_GALLERY + videoGallery.getId();

        // Save the file
        fileService.saveFile(file, filePath, fileName);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.FILE_CREATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }
}
