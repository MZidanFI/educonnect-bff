package com.edu.course_service.service;

import com.edu.course_service.dto.EnrolledCourseDTO;
import com.edu.course_service.dto.RequestEnrollmentDTO;
import com.edu.course_service.model.ModelCourse;
import com.edu.course_service.model.ModelEnrollment;
import com.edu.course_service.repository.RepositoryCourse;
import com.edu.course_service.repository.RepositoryEnrollment;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceCourse {

    @Autowired
    private RepositoryCourse repositoryCourse;

    @Autowired
    private RepositoryEnrollment repositoryEnrollment;

    public List<ModelCourse> getAllCourses() {
        return repositoryCourse.findAll();
    }

    public ModelCourse createCourse(ModelCourse course) {
        return repositoryCourse.save(course);
    }

    public List<EnrolledCourseDTO> getEnrolledCoursesByUser(String userId) {
        List<ModelEnrollment> enrollments = repositoryEnrollment.findByUserId(userId);

        return enrollments.stream()
                .map(enrollment -> {
                    Optional<ModelCourse> course = repositoryCourse.findById(enrollment.getCourseId());
                    return course.map(c -> new EnrolledCourseDTO(
                            c.getJudul(),
                            c.getKategori(),
                            enrollment.getProgress())).orElse(null);
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    public ModelEnrollment createEnrollment(RequestEnrollmentDTO request) {
        ModelEnrollment enrollment = new ModelEnrollment();
        enrollment.setUserId(request.getUserId());
        enrollment.setCourseId(request.getCourseId());
        enrollment.setProgress(request.getProgress());
        return repositoryEnrollment.save(enrollment);
    }
}