package com.smarty.domain.student.service;

import com.smarty.domain.account.model.PasswordUpdateDTO;
import com.smarty.domain.student.entity.Student;
import com.smarty.domain.student.model.StudentRequestDTO;
import com.smarty.domain.student.model.StudentResponseDTO;
import com.smarty.domain.student.model.StudentUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO);

    Page<StudentResponseDTO> getAllStudents(Pageable pageable);

    StudentResponseDTO getStudentById(Long id);

    Student getById(Long id);

    StudentResponseDTO updateStudent(Long id, StudentUpdateDTO studentUpdateDTO);

    StudentResponseDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO);

    void deleteStudent(Long id);

}
