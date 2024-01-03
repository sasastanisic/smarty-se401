package com.smarty.domain.course.service;

import com.smarty.domain.course.domain.Course;
import com.smarty.domain.course.model.CourseRequestDTO;
import com.smarty.domain.course.model.CourseResponseDTO;
import com.smarty.domain.course.model.CourseUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO);

    Page<CourseResponseDTO> getAllCourses(Pageable pageable);

    CourseResponseDTO getCourseById(Long id);

    Course getById(Long id);

    CourseResponseDTO updateCourse(Long id, CourseUpdateDTO courseUpdateDTO);

    void deleteCourse(Long id);

}
