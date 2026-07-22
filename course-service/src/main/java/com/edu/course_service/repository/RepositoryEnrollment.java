package com.edu.course_service.repository;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.edu.course_service.model.ModelEnrollment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepositoryEnrollment extends MongoRepository<ModelEnrollment, String> {
    List<ModelEnrollment> findByUserId(String userId);
}
