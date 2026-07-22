package com.edu.website_bff.controller;

import com.edu.website_bff.dto.DTODashboard;
import com.edu.website_bff.service.ServiceDashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/web")
public class ControllerDashboard {

    @Autowired
    private ServiceDashboard serviceDashboard;

    @GetMapping("/dashboard/{userId}")
    public Mono<DTODashboard> getDashboard(
            @PathVariable String userId,
            @RequestHeader("Authorization") String token) {
        return serviceDashboard.getDashboard(userId, token);
    }
}