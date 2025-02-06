package com.mhealth.admin.service;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.GalleryResponseDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.ImageGallery;
import com.mhealth.admin.repository.ImageGalleryRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ImageGalleryService {

    @Autowired
    private ImageGalleryRepository imageGalleryRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private MessageSource messageSource;

    public Object getImageGallery(Locale locale, String sortBy, String name, int page, int size) {
        // Default to sorting by "id" if sortBy is null or empty
        String defaultSortBy = "id";
        Sort sort = (sortBy != null && !sortBy.isEmpty()) ? Sort.by(sortBy) : Sort.by(defaultSortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ImageGallery> result;
        if (name != null && !name.isEmpty()) {
            result = imageGalleryRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            result = imageGalleryRepository.findAll(pageable);
        }

        // Map ImageGallery entities to ImageGalleryDto objects
        List<GalleryResponseDto> dtoList = result.getContent().stream()
                .map(image -> new GalleryResponseDto(
                        image.getId(),
                        image.getName(),
                        Constants.IMAGE_GALLERY + image.getId() + "/" + image.getName() // Assuming file location
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
    public Object deleteImage(Locale locale, Integer id) throws Exception {
        Response response = new Response();

        imageGalleryRepository.deleteById(id);

        String filePath = Constants.IMAGE_GALLERY + id;

        // delete file and folder
        fileService.deleteFileOrFolder(filePath);
        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.FILE_DELETED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }

    public Object saveImage(MultipartFile file, Locale locale) throws IOException {
        Response response = new Response();

        if (file.isEmpty()) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.FILE_EMPTY, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Check the file size (1 MB = 1048576 bytes)
        if (file.getSize() > 1048576) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.IMAGE_SIZE, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Check the file extension
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".png")) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.IMAGE_EXTENSION, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        ImageGallery existingImage = imageGalleryRepository.findByName(fileName);

        if (existingImage != null) {
            response.setCode(Constants.CODE_O);
            response.setMessage(messageSource.getMessage(Messages.FILE_NAME_EXISTS, null, locale));
            response.setStatus(Status.FAILED);
            return response;
        }

        // Optionally, save the file details to the database
        ImageGallery imageGallery = new ImageGallery();
        imageGallery.setName(fileName);
        imageGallery = imageGalleryRepository.save(imageGallery);

        // upload Image gallery
        String filePath = Constants.IMAGE_GALLERY + imageGallery.getId();

        // Save the file
        fileService.saveFile(file, filePath, fileName);

        // Prepare success response
        response.setCode(Constants.CODE_1);
        response.setMessage(messageSource.getMessage(Messages.FILE_CREATED, null, locale));
        response.setStatus(Status.SUCCESS);

        return response;
    }
}
