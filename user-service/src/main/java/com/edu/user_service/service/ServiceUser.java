package com.edu.user_service.service;

import com.edu.user_service.model.ModelUser;
import com.edu.user_service.repository.RepositoryUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceUser {

    @Autowired
    private RepositoryUser repositoryUser;

    // Diubah dari List<ServiceUser> menjadi List<ModelUser>
    public List<ModelUser> getAllUsers() {
        return repositoryUser.findAll();
    }

    // Diubah dari ServiceUser menjadi ModelUser untuk return dan parameter
    public ModelUser createUser(ModelUser user) {
        return repositoryUser.save(user);
    }

    // Diubah dari Optional<ServiceUser> menjadi Optional<ModelUser>
    public Optional<ModelUser> getUserById(String id) {
        return repositoryUser.findById(id);
    }
}