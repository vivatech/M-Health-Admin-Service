package com.mhealth.admin.repository;

import com.mhealth.admin.model.ImageGallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageGalleryRepository extends JpaRepository<ImageGallery, Integer> {

    Page<ImageGallery> findByNameContainingIgnoreCase(String name, Pageable pageable);

    ImageGallery findByName(String fileName);
}