package com.edu.auth_service.repository;

import com.edu.auth_service.model.ModelAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RepositoryAccount extends MongoRepository<ModelAccount, String> {
    Optional<ModelAccount> findByUsername(String username);
}