package com.dyashin.scorm.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dyashin.scorm.service.ScormService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/scorm")
@RequiredArgsConstructor
public class ScormController {

	private final ScormService scormService;
	@Value("${scorm.storage-path}")
	private String storagePath;

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws Exception {

		scormService.upload(file);
		Map<String, Object> response=new LinkedHashMap<>();
    	response.put("error", false);
    	response.put("message", "SCORM uploaded and validated successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	 /**
     * Get all SCORM courses
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCourses() {
    	Map<String, Object> response=new LinkedHashMap<>();
    	response.put("error", false);
    	response.put("courseList", scormService.getAllCourses());
    	response.put("message", "Data fetched Successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get SCORM course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable String id) {
    	Map<String, Object> response=new LinkedHashMap<>();
    	response.put("error", false);
    	response.put("course", scormService.getCourseById(id));
    	response.put("message", "Data fetched Successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/launch/{courseId}/**")
    public ResponseEntity<Resource> launchCourse(
            @PathVariable String courseId,
            HttpServletRequest request) throws Exception {

        String uri = request.getRequestURI();

        String basePath = "/scorm/launch/" + courseId + "/";
        String filePath = uri.substring(uri.indexOf(basePath) + basePath.length());

        Path fullPath = Paths.get(storagePath, courseId, filePath);

        Resource resource = new UrlResource(fullPath.toUri());

        return ResponseEntity.ok().body(resource);
    }
}