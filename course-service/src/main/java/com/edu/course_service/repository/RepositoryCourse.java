package com.edu.course_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.edu.course_service.model.ModelCourse;

public interface RepositoryCourse extends MongoRepository<ModelCourse, String> {

}
