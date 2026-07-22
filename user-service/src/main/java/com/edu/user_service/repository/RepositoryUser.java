package com.edu.user_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.edu.user_service.model.ModelUser;

public interface RepositoryUser extends MongoRepository<ModelUser, String> {

}
