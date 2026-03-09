package com.dyashin.scorm.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scorm_course")
@Getter
@Setter
public class ScormCourse {

    @Id
    private String id;

    private String name;

    private String version;

    private String launchFile;

    private String storagePath;

    private LocalDateTime uploadedAt;
    
    private String courseTitle;
    
    @Transient
    private String launchUrl;
}