package com.smarty.domain.student.service;

import com.smarty.domain.account.entity.Account;
import com.smarty.domain.account.enums.Role;
import com.smarty.domain.account.model.AccountRequestDTO;
import com.smarty.domain.account.model.AccountResponseDTO;
import com.smarty.domain.account.service.AccountService;
import com.smarty.domain.major.entity.Major;
import com.smarty.domain.major.model.MajorResponseDTO;
import com.smarty.domain.major.service.MajorService;
import com.smarty.domain.status.entity.Status;
import com.smarty.domain.status.service.StatusService;
import com.smarty.domain.student.entity.Student;
import com.smarty.domain.student.model.StudentRequestDTO;
import com.smarty.domain.student.model.StudentResponseDTO;
import com.smarty.domain.student.repository.StudentRepository;
import com.smarty.infrastructure.email.EmailNotificationService;
import com.smarty.infrastructure.exception.exceptions.BadRequestException;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.StudentMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    Account account;

    Major major;

    Status status;

    Student student;

    Page<Student> students;

    @InjectMocks
    StudentServiceImpl studentService;

    @Mock
    StudentRepository studentRepository;

    @Mock
    StudentMapperImpl studentMapper;

    @Mock
    AccountService accountService;

    @Mock
    MajorService majorService;

    @Mock
    StatusService statusService;

    @Mock
    EmailNotificationService emailNotificationService;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setEmail("sasa.stanisic.4377@metropolitan.ac.rs");
        account.setPassword("@Password123$");
        account.setRole(Role.STUDENT);

        major = new Major();
        major.setId(1L);
        major.setCode("SE");
        major.setFullName("Software engineering");
        major.setDescription("Software engineering major");
        major.setDuration(4);

        status = new Status();
        status.setId(1L);
        status.setType("Traditional");

        student = new Student();
        student.setId(1L);
        student.setName("Sasa");
        student.setSurname("Stanisic");
        student.setIndex(4377);
        student.setYear(4);
        student.setSemester(7);
        student.setAccount(account);
        student.setMajor(major);
        student.setStatus(status);

        List<Student> studentList = new ArrayList<>();
        studentList.add(student);
        students = new PageImpl<>(studentList);

        studentService.setPasswordEncoder(passwordEncoder);
    }

    @Test
    void testCreateStudent() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$");
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("Sasa", "Stanisic", 4377, 4, 7, accountRequestDTO, 1L, 1L);

        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 7, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        when(studentMapper.toStudent(studentRequestDTO)).thenReturn(student);
        when(majorService.getById(studentRequestDTO.majorId())).thenReturn(major);
        when(statusService.getStatusById(studentRequestDTO.statusId())).thenReturn(status);
        when(passwordEncoder.encode(studentRequestDTO.account().password())).thenReturn(accountResponseDTO.password());
        when(studentRepository.existsByIndex(studentRequestDTO.index())).thenReturn(false);
        doNothing().when(accountService).validateEmail(studentRequestDTO.account().email());
        doNothing().when(emailNotificationService).sendConfirmation(studentRequestDTO.account().email(), studentRequestDTO.name());
        when(studentRepository.save(student)).thenReturn(student);
        doReturn(studentResponseDTO).when(studentMapper).toStudentResponseDTO(student);

        var createdStudentDTO = studentService.createStudent(studentRequestDTO);

        assertThat(studentResponseDTO).isEqualTo(createdStudentDTO);
    }

    @Test
    void testStudentExistsByIndex() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$");
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("Sasa", "Stanisic", 4377, 4, 7, accountRequestDTO, 1L, 1L);

        when(studentMapper.toStudent(studentRequestDTO)).thenReturn(student);
        doReturn(false).when(studentRepository).existsByIndex(4377);

        Assertions.assertDoesNotThrow(() -> studentService.createStudent(studentRequestDTO));
    }

    @Test
    void testStudentExistsByIndex_NotValid() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$");
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("Sasa", "Stanisic", 4377, 4, 7, accountRequestDTO, 1L, 1L);

        when(studentMapper.toStudent(studentRequestDTO)).thenReturn(student);
        doReturn(true).when(studentRepository).existsByIndex(4377);

        Assertions.assertThrows(ConflictException.class, () -> studentService.createStudent(studentRequestDTO));
    }

    @Test
    void testStudentByYearAndSemesterCombination_NotValid() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$");
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("Sasa", "Stanisic", 4377, 4, 6, accountRequestDTO, 1L, 1L);

        Assertions.assertThrows(BadRequestException.class, () -> studentService.createStudent(studentRequestDTO));
    }

    @Test
    void testStudentExists() {
        doReturn(true).when(studentRepository).existsById(1L);
        Assertions.assertDoesNotThrow(() -> studentService.existsById(1L));
        verify(studentRepository, times(1)).existsById(1L);
    }

    @Test
    void testStudentExists_NotFound() {
        doReturn(false).when(studentRepository).existsById(1L);
        Assertions.assertThrows(NotFoundException.class, () -> studentService.existsById(1L));
    }

}
