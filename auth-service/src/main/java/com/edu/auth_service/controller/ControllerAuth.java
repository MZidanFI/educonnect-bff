package com.edu.auth_service.controller;

import com.edu.auth_service.dto.RequestLoginDTO;
import com.edu.auth_service.dto.ResponseLoginDTO;
import com.edu.auth_service.model.ModelAccount;
import com.edu.auth_service.service.ServiceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class ControllerAuth {

    @Autowired
    private ServiceAuth serviceAuth;

    @PostMapping("/register")
    public ModelAccount register(@RequestBody ModelAccount account) {
        return serviceAuth.register(account);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDTO> login(@RequestBody RequestLoginDTO request) {
        return serviceAuth.login(request.getUsername(), request.getPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }
}