package com.smarty.domain.course.service;

import com.smarty.domain.course.domain.Course;
import com.smarty.domain.course.model.CourseRequestDTO;
import com.smarty.domain.course.model.CourseResponseDTO;
import com.smarty.domain.course.model.CourseUpdateDTO;
import com.smarty.domain.course.repository.CourseRepository;
import com.smarty.infrastructure.exception.exceptions.BadRequestException;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.CourseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        validateYearAndSemester(courseRequestDTO.year(), courseRequestDTO.semester());
        courseRepository.save(course);

        return courseMapper.toCourseResponseDTO(course);
    }

    private void validateCode(String code) {
        if (courseRepository.existsByCode(code)) {
            throw new ConflictException("Course with code %s already exists".formatted(code));
        }
    }

    private void validateYearAndSemester(int year, int semester) {
        Map<Integer, Set<Integer>> validCombinations = Map.of(
                1, Set.of(1, 2),
                2, Set.of(3, 4),
                3, Set.of(5, 6),
                4, Set.of(7, 8)
        );

        Set<Integer> allowedValues = validCombinations.get(year);

        if (!allowedValues.contains(semester)) {
            throw new BadRequestException("Combination of year %d and semester %d isn't valid".formatted(year, semester));
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

    public Course getById(Long id) {
        Optional<Course> optionalCourse = courseRepository.findById(id);

        if (optionalCourse.isEmpty()) {
            throw new NotFoundException(COURSE_NOT_EXISTS.formatted(id));
        }

        return optionalCourse.get();
    }

    @Override
    public void existsById(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NotFoundException(COURSE_NOT_EXISTS.formatted(id));
        }
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseUpdateDTO courseUpdateDTO) {
        Course course = getById(id);
        courseMapper.updateCourseFromDTO(courseUpdateDTO, course);

        validateYearAndSemester(courseUpdateDTO.year(), courseUpdateDTO.semester());
        courseRepository.save(course);

        return courseMapper.toCourseResponseDTO(course);
    }

    @Override
    public void deleteCourse(Long id) {
        existsById(id);
        courseRepository.deleteById(id);
    }

}
