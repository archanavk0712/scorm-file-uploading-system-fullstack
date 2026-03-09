package com.dyashin.scorm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class ScormApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScormApplication.class, args);
		log.info("Server is Running");
	}

}
