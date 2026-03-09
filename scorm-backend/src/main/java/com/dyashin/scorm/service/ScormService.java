package com.dyashin.scorm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dyashin.scorm.entity.ScormCourse;

public interface ScormService {
	public void upload(MultipartFile file) throws Exception;

	List<ScormCourse> getAllCourses();

	ScormCourse getCourseById(String id);
}
