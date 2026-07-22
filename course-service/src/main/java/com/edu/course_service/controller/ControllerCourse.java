package com.edu.course_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.course_service.service.ServiceCourse;
import com.edu.course_service.dto.EnrolledCourseDTO;
import com.edu.course_service.model.ModelCourse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/courses")
public class ControllerCourse {

    @Autowired
    private ServiceCourse serviceCourse;

    @GetMapping
    public List<ModelCourse> getAllCourses() {
        return serviceCourse.getAllCourses();
    }

    @PostMapping
    public ModelCourse createCourse(@RequestBody ModelCourse course) {
        return serviceCourse.createCourse(course);
    }

    @GetMapping("/enrolled")
    public List<EnrolledCourseDTO> getEnrolledCourses(@RequestParam String userId) {
        return serviceCourse.getEnrolledCoursesByUser(userId);
    }
}
