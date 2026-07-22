package com.edu.user_service.controller;

import com.edu.user_service.model.ModelUser;
import com.edu.user_service.service.ServiceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
public class ControllerUser {

    @Autowired
    private ServiceUser serviceUser;

    @GetMapping
    public List<ModelUser> getAllUsers() {
        return serviceUser.getAllUsers();
    }

    @PostMapping
    public ModelUser createUser(@RequestBody ModelUser user) {
        return serviceUser.createUser(user);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<ModelUser> getUserProfile(@PathVariable String id) {
        return serviceUser.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
