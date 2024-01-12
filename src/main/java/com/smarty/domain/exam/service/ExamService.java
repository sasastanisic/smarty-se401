package com.smarty.domain.exam.service;

import com.smarty.domain.course.domain.Course;
import com.smarty.domain.exam.model.ExamRequestDTO;
import com.smarty.domain.exam.model.ExamResponseDTO;
import com.smarty.domain.exam.model.ExamUpdateDTO;
import com.smarty.domain.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExamService {

    ExamResponseDTO createExam(ExamRequestDTO examRequestDTO);

    void checkCourseAndStudentYear(Student student, Course course);

    void isExamAlreadyPassed(Student student, Course course);

    Page<ExamResponseDTO> getAllExams(Pageable pageable);

    ExamResponseDTO getExamById(Long id);

    ExamResponseDTO updateExam(Long id, ExamUpdateDTO examUpdateDTO);

    void deleteExam(Long id);

}
