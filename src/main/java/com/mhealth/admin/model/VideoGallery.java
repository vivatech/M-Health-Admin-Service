package com.mhealth.admin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "mh_chunk_video")
public class VideoGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Name cannot be blank") // Ensures the field is not empty or null
    @Pattern(regexp = ".*\\.mp4$", message = "File name must end with .mp4") // Ensures the file name ends with .mp4
    @Size(max = 255, message = "File name cannot exceed 255 characters") // Limits the maximum length of the file name
    private String name;
}
