package com.mhealth.admin.repository;

import com.mhealth.admin.model.VideoGallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoGalleryRepository extends JpaRepository<VideoGallery, Integer> {

    Page<VideoGallery> findByNameContainingIgnoreCase(String name, Pageable pageable);

    VideoGallery findByName(String fileName);
}