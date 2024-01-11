package com.smarty.domain.exam.service;

import com.smarty.domain.course.service.CourseService;
import com.smarty.domain.exam.domain.Exam;
import com.smarty.domain.exam.model.ExamRequestDTO;
import com.smarty.domain.exam.model.ExamResponseDTO;
import com.smarty.domain.exam.model.ExamUpdateDTO;
import com.smarty.domain.exam.repository.ExamRepository;
import com.smarty.domain.student.service.StudentService;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.ExamMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExamServiceImpl implements ExamService {

    private static final String EXAM_NOT_EXISTS = "Exam with id %d doesn't exist";

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final StudentService studentService;
    private final CourseService courseService;

    public ExamServiceImpl(ExamRepository examRepository,
                           ExamMapper examMapper,
                           StudentService studentService,
                           CourseService courseService) {
        this.examRepository = examRepository;
        this.examMapper = examMapper;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @Override
    public ExamResponseDTO createExam(ExamRequestDTO examRequestDTO) {
        Exam exam = examMapper.toExam(examRequestDTO);
        var student = studentService.getById(examRequestDTO.studentId());
        var course = courseService.getById(examRequestDTO.courseId());

        exam.setDateOfExamination(LocalDate.now());
        exam.setStudent(student);
        exam.setCourse(course);
        examRepository.save(exam);

        return examMapper.toExamResponseDTO(exam);
    }

    @Override
    public Page<ExamResponseDTO> getAllExams(Pageable pageable) {
        return examRepository.findAll(pageable).map(examMapper::toExamResponseDTO);
    }

    @Override
    public ExamResponseDTO getExamById(Long id) {
        return examMapper.toExamResponseDTO(getById(id));
    }

    private Exam getById(Long id) {
        return examRepository.findById(id).orElseThrow(() -> new NotFoundException(EXAM_NOT_EXISTS.formatted(id)));
    }

    @Override
    public ExamResponseDTO updateExam(Long id, ExamUpdateDTO examUpdateDTO) {
        Exam exam = getById(id);
        examMapper.updateExamFromDTO(examUpdateDTO, exam);

        examRepository.save(exam);

        return examMapper.toExamResponseDTO(exam);
    }

    @Override
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new NotFoundException(EXAM_NOT_EXISTS.formatted(id));
        }

        examRepository.deleteById(id);
    }

}
