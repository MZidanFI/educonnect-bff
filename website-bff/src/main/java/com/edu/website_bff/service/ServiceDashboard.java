package com.edu.website_bff.service;

import com.edu.website_bff.dto.DTODashboard;
import com.edu.website_bff.dto.DTOEnrolledCourse;
import com.edu.website_bff.dto.DTOUserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ServiceDashboard {

        @Autowired
        private WebClient.Builder webClientBuilder;

        public Mono<DTODashboard> getDashboard(String userId, String token) {

                Mono<List<DTOEnrolledCourse>> coursesMono = webClientBuilder.build()
                                .get()
                                .uri("http://course-service/internal/courses/enrolled?userId=" + userId)
                                .header(HttpHeaders.AUTHORIZATION, token)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<List<DTOEnrolledCourse>>() {
                                });

                Mono<DTOUserProfile> userMono = webClientBuilder.build()
                                .get()
                                .uri("http://user-service/internal/users/" + userId + "/profile")
                                .header(HttpHeaders.AUTHORIZATION, token)
                                .retrieve()
                                .bodyToMono(DTOUserProfile.class);

                return Mono.zip(coursesMono, userMono)
                                .map(tuple -> {
                                        List<DTOEnrolledCourse> courses = tuple.getT1();
                                        DTOUserProfile user = tuple.getT2();

                                        return new DTODashboard(
                                                        user.getNama(),
                                                        user.getEmail(),
                                                        courses.size(),
                                                        courses);
                                });
        }
}