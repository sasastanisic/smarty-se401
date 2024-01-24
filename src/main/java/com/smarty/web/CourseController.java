package com.smarty.web;

import com.smarty.domain.course.model.CourseRequestDTO;
import com.smarty.domain.course.model.CourseResponseDTO;
import com.smarty.domain.course.model.CourseUpdateDTO;
import com.smarty.domain.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO courseRequestDTO) {
        return ResponseEntity.ok(courseService.createCourse(courseRequestDTO));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> getAllCourses(Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping("/by-year")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByYear(@RequestParam int year) {
        return ResponseEntity.ok(courseService.getCoursesByYear(year));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping("/by-semester")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesBySemester(@RequestParam int semester) {
        return ResponseEntity.ok(courseService.getCoursesBySemester(semester));
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @GetMapping("/by-professor/{professorId}")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(courseService.getCoursesByProfessor(professorId));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<CourseResponseDTO>> getTakenCoursesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(courseService.getTakenCoursesByStudent(studentId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseUpdateDTO courseUpdateDTO) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseUpdateDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

}
