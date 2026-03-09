package com.dyashin.scorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dyashin.scorm.entity.ScormCourse;

public interface ScormCourseRepository extends JpaRepository<ScormCourse,String >{
	
	

}
