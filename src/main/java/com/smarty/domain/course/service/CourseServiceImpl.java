package com.smarty.domain.course.service;

import com.smarty.domain.course.domain.Course;
import com.smarty.domain.course.model.CourseRequestDTO;
import com.smarty.domain.course.model.CourseResponseDTO;
import com.smarty.domain.course.model.CourseUpdateDTO;
import com.smarty.domain.course.repository.CourseRepository;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.CourseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    private static final String COURSE_NOT_EXISTS = "Course with id %d doesn't exist";

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        Course course = courseMapper.toCourse(courseRequestDTO);

        validateCode(courseRequestDTO.code());
        courseRepository.save(course);

        return courseMapper.toCourseResponseDTO(course);
    }

    private void validateCode(String code) {
        if (courseRepository.existsByCode(code)) {
            throw new ConflictException("Course with code %s already exists".formatted(code));
        }
    }

    @Override
    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(courseMapper::toCourseResponseDTO);
    }

    @Override
    public CourseResponseDTO getCourseById(Long id) {
        return courseMapper.toCourseResponseDTO(getById(id));
    }

    private Course getById(Long id) {
        Optional<Course> optionalCourse = courseRepository.findById(id);

        if (optionalCourse.isEmpty()) {
            throw new NotFoundException(COURSE_NOT_EXISTS.formatted(id));
        }

        return optionalCourse.get();
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseUpdateDTO courseUpdateDTO) {
        Course course = getById(id);
        courseMapper.updateCourseFromDTO(courseUpdateDTO, course);

        courseRepository.save(course);

        return courseMapper.toCourseResponseDTO(course);
    }

    @Override
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NotFoundException(COURSE_NOT_EXISTS.formatted(id));
        }

        courseRepository.deleteById(id);
    }
    // TODO: additional check -> year & semester

}
