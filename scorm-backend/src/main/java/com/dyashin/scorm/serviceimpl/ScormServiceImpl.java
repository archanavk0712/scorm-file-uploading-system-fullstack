package com.dyashin.scorm.serviceimpl;

import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dyashin.scorm.entity.ScormCourse;
import com.dyashin.scorm.repository.ScormCourseRepository;
import com.dyashin.scorm.service.ScormService;
import com.dyashin.scorm.util.ScormUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ScormServiceImpl implements ScormService {

	@Autowired
	private ScormCourseRepository repository;

	// Path where validated SCORM packages will be stored
	@Value("${scorm.storage-path}")
	private String storagePath;

	/*
	 * Saving metadata in db
	 */
	private void saveMetadata(String id, String name, String version, String launchFile, String courseTitle) {

		ScormCourse course = new ScormCourse();

		course.setId(id);
		course.setName(name);
		course.setVersion(version);
		course.setLaunchFile(launchFile);
		course.setStoragePath(storagePath + "/" + id);
		course.setUploadedAt(LocalDateTime.now());
		course.setCourseTitle(courseTitle);

		repository.save(course);
	}

	/**
	 * Main SCORM Upload Logic currently storing in the mentioned path in
	 * application.properties
	 */
	@Override
	public void upload(MultipartFile file) throws Exception {

		//
		// 1️.Basic File Validation
		// 
		if (file == null || file.isEmpty()) {
			throw new RuntimeException("Uploaded file is empty");
		}

		String fileName = file.getOriginalFilename();

		if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
			throw new RuntimeException("Only ZIP files are allowed");
		}

		log.info("Basic file validation passed");

		//
		// 2️. Validate ZIP signature
		// Protects against fake zip uploads
		// 
		InputStream signatureStream = file.getInputStream();
		ScormUtil.validateZipSignature(signatureStream);
		signatureStream.close();

		//
		// 3️. Create temporary folder
		//
		Path tempDir = Files.createTempDirectory("scorm-");

		log.info("Temporary directory created at {}", tempDir);

		try {

			// 
			// 4️. Secure unzip
			// Includes ZIP-Slip protection
			//
			ScormUtil.unzipSecure(file.getInputStream(), tempDir);

			//
			// 5️ Validate manifest file
			//
			Path manifestPath = tempDir.resolve("imsmanifest.xml");

			if (!Files.exists(manifestPath)) {
				throw new RuntimeException("imsmanifest.xml not found in SCORM package");
			}

			// 
			// 6️ Secure XML parsing
			// Protects against XXE attacks
			// 
			Document doc;

			try {
				doc = ScormUtil.parseManifestSecure(manifestPath);
			} catch (Exception e) {
				throw new RuntimeException("Invalid or corrupted imsmanifest.xml "+ e.getMessage());
			}

			// 
			// 7️ Validate SCORM structure
			//
			validateStructure(doc);

			//
			// 8️ Extract course title
			//
			String courseTitle = extractCourseTitle(doc);

			//
			// 9️ Extract launch file
			//
			String launchFile = extractLaunchFile(doc);

			Path launchPath = tempDir.resolve(launchFile);

			if (!Files.exists(launchPath)) {
				throw new RuntimeException("Launch file missing: " + launchFile);
			}

			//
			// 10 Detect SCORM version
			//
			String version = detectVersion(doc);

			//
			// 11️ Generate unique course ID
			//
			String courseId = UUID.randomUUID().toString();

			// Permanent storage directory
			Path permanentDir = Paths.get(storagePath, courseId);

			Files.createDirectories(permanentDir);

			//
			// 1️2️ Move extracted files
			//
			Files.walk(tempDir).forEach(source -> {

				try {

					Path destination = permanentDir.resolve(tempDir.relativize(source));

					if (Files.isDirectory(source)) {

						Files.createDirectories(destination);

					} else {

						Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
					}

				} catch (Exception e) {

					throw new RuntimeException("Failed to move SCORM content", e);
				}
			});

			//
			// 1️3️ Save metadata
			//
			saveMetadata(courseId, fileName, version, launchFile, courseTitle);

			log.info("SCORM stored successfully with ID {}", courseId);

		} finally {

			//
			// 1️4️ Cleanup temp directory
			//
			ScormUtil.deleteDirectory(tempDir);
		}
	}

	/**
	 * Validate required SCORM elements
	 */
	private void validateStructure(Document doc) {

		if (doc.getElementsByTagName("manifest").getLength() == 0) {
			throw new RuntimeException("Invalid SCORM: <manifest> missing");
		}

		if (doc.getElementsByTagName("organizations").getLength() == 0) {
			throw new RuntimeException("Invalid SCORM: <organizations> missing");
		}

		if (doc.getElementsByTagName("resources").getLength() == 0) {
			throw new RuntimeException("Invalid SCORM: <resources> missing");
		}
	}

	/**
	 * Extract launch file from manifest
	 */
	private String extractLaunchFile(Document doc) {

		NodeList resources = doc.getElementsByTagName("resource");

		for (int i = 0; i < resources.getLength(); i++) {

			Element el = (Element) resources.item(i);

			if ("webcontent".equalsIgnoreCase(el.getAttribute("type"))) {

				String href = el.getAttribute("href");

				if (href != null && !href.isBlank()) {

					return href;
				}
			}
		}

		throw new RuntimeException("Launch file not found in manifest");
	}

	/**
	 * Detect SCORM version
	 */
	private String detectVersion(Document doc) {

		if (doc.getElementsByTagName("imsss:sequencing").getLength() > 0) {
			return "SCORM 2004";
		}

		return "SCORM 1.2";
	}

	/**
	 * Extract course title from manifest
	 */
	private String extractCourseTitle(Document doc) {

		NodeList organizations = doc.getElementsByTagName("organization");

		if (organizations.getLength() > 0) {

			Element organization = (Element) organizations.item(0);

			NodeList titles = organization.getElementsByTagName("title");

			if (titles.getLength() > 0) {

				return titles.item(0).getTextContent().trim();
			}
		}

		return "Untitled Course";
	}

	@Override
	public List<ScormCourse> getAllCourses() {
		return repository.findAll();
	}

	@Override
	public ScormCourse getCourseById(String id) {

	    ScormCourse course = repository.findById(id)
	            .orElseThrow(() ->
	                    new RuntimeException("SCORM course not found with ID: " + id));

	    String launchUrl = "http://localhost:8080/scorm/launch/"
	            + course.getId() + "/" + course.getLaunchFile();

	    course.setLaunchUrl(launchUrl);

	    return course;
	}
}