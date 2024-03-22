package com.smarty.domain.student.service;

import com.smarty.domain.account.entity.Account;
import com.smarty.domain.account.enums.Role;
import com.smarty.domain.account.model.AccountRequestDTO;
import com.smarty.domain.account.model.AccountResponseDTO;
import com.smarty.domain.account.model.PasswordUpdateDTO;
import com.smarty.domain.account.service.AccountService;
import com.smarty.domain.course.domain.Course;
import com.smarty.domain.course.service.CourseService;
import com.smarty.domain.major.entity.Major;
import com.smarty.domain.major.model.MajorResponseDTO;
import com.smarty.domain.major.service.MajorService;
import com.smarty.domain.status.entity.Status;
import com.smarty.domain.status.service.StatusService;
import com.smarty.domain.student.entity.Student;
import com.smarty.domain.student.model.StudentRequestDTO;
import com.smarty.domain.student.model.StudentResponseDTO;
import com.smarty.domain.student.model.StudentUpdateDTO;
import com.smarty.domain.student.repository.StudentRepository;
import com.smarty.infrastructure.email.EmailNotificationService;
import com.smarty.infrastructure.exception.exceptions.BadRequestException;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.StudentMapperImpl;
import com.smarty.infrastructure.security.service.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    CourseService courseService;

    @Mock
    AuthenticationService authenticationService;

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
    void testGetAllStudents() {
        Pageable pageable = mock(Pageable.class);
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 7, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        when(studentMapper.toStudentResponseDTO(student)).thenReturn(studentResponseDTO);
        var expectedStudents = students.map(student -> studentMapper.toStudentResponseDTO(student));
        doReturn(students).when(studentRepository).findAll(pageable);
        var studentPage = studentService.getAllStudents(pageable);

        Assertions.assertEquals(expectedStudents, studentPage);

        for (var i = 0; i < studentPage.getContent().size(); i++) {
            compareStudentDTO(expectedStudents.getContent().get(i), studentPage.getContent().get(i));
        }
    }

    private void compareStudentDTO(StudentResponseDTO expectedStudent, StudentResponseDTO returnedStudent) {
        Assertions.assertEquals(expectedStudent.id(), returnedStudent.id());
        Assertions.assertSame(expectedStudent.name(), returnedStudent.name());
        Assertions.assertSame(expectedStudent.surname(), returnedStudent.surname());
        Assertions.assertEquals(expectedStudent.index(), returnedStudent.index());
        Assertions.assertEquals(expectedStudent.year(), returnedStudent.year());
        Assertions.assertEquals(expectedStudent.semester(), returnedStudent.semester());
        Assertions.assertEquals(expectedStudent.account(), returnedStudent.account());
        Assertions.assertEquals(expectedStudent.major(), returnedStudent.major());
        Assertions.assertEquals(expectedStudent.status(), returnedStudent.status());
    }

    @Test
    void testGetStudentById() {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 7, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        when(studentMapper.toStudentResponseDTO(student)).thenReturn(studentResponseDTO);
        var expectedStudent = studentMapper.toStudentResponseDTO(student);
        doReturn(Optional.of(student)).when(studentRepository).findById(1L);
        var returnedStudent = studentService.getStudentById(1L);

        Assertions.assertEquals(expectedStudent, returnedStudent);
    }

    @Test
    void testGetStudentById_NotFound() {
        doReturn(Optional.empty()).when(studentRepository).findById(1L);
        Assertions.assertThrows(NotFoundException.class, () -> studentService.getStudentById(1L));
    }

    @Test
    void testGetStudentByEmail() {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 7, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        when(studentMapper.toStudentResponseDTO(student)).thenReturn(studentResponseDTO);
        var expectedStudent = studentMapper.toStudentResponseDTO(student);
        doReturn(student).when(studentRepository).findByAccount_Email(student.getAccount().getEmail());
        var returnedStudent = studentService.getStudentByEmail(student.getAccount().getEmail());

        Assertions.assertEquals(expectedStudent, returnedStudent);
    }

    @Test
    void testGetStudentByEmail_NotFound() {
        doReturn(null).when(studentRepository).findByAccount_Email(student.getAccount().getEmail());
        Assertions.assertThrows(NotFoundException.class, () -> studentService.getStudentByEmail(student.getAccount().getEmail()));
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

    @Test
    void testGetStudentsByMajor() {
        List<Student> studentsByMajor = List.of(student);
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 7, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        when(studentMapper.toStudentResponseDTO(student)).thenReturn(studentResponseDTO);
        var expectedList = getStudentListResponseDTO(studentsByMajor);
        doReturn(studentsByMajor).when(studentRepository).findStudentsByMajor_Id(major.getId());
        doNothing().when(majorService).existsById(major.getId());
        var returnedList = studentService.getStudentsByMajor(major.getId());

        Assertions.assertEquals(expectedList, returnedList);
        Assertions.assertTrue(studentsByMajor.contains(student));
    }

    @Test
    void testGetStudentsByStudyStatus() {
        List<Student> studentsByStudyStatus = List.of(student);
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 7, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        when(studentMapper.toStudentResponseDTO(student)).thenReturn(studentResponseDTO);
        var expectedList = getStudentListResponseDTO(studentsByStudyStatus);
        doReturn(studentsByStudyStatus).when(studentRepository).findStudentsByStatus_Id(status.getId());
        doNothing().when(statusService).existsById(status.getId());
        var returnedList = studentService.getStudentsByStudyStatus(status.getId());

        Assertions.assertEquals(expectedList, returnedList);
        Assertions.assertFalse(returnedList.isEmpty());
    }

    @Test
    void testGetStudentsWhoPassedCertainCourse() {
        List<Student> studentsWhoPassedCertainCourse = List.of(student);
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 7, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        when(studentMapper.toStudentResponseDTO(student)).thenReturn(studentResponseDTO);
        var expectedList = getStudentListResponseDTO(studentsWhoPassedCertainCourse);
        doReturn(studentsWhoPassedCertainCourse).when(studentRepository).findStudentsWhoPassedCertainCourse(new Course().getId());
        doNothing().when(courseService).existsById(new Course().getId());
        var returnedList = studentService.getStudentsWhoPassedCertainCourse(new Course().getId());

        assertThat(expectedList).isEqualTo(returnedList);
    }

    private List<StudentResponseDTO> getStudentListResponseDTO(List<Student> studentList) {
        return studentList
                .stream()
                .map(studentMapper::toStudentResponseDTO)
                .toList();
    }

    @Test
    void testGetAverageGradeOfStudent() {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 7, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        var expectedMap = Map.of(
                "student", studentResponseDTO,
                "student's GPA", 9.67
        );

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.findAverageGradeOfStudent(1L)).thenReturn(9.67);
        when(studentMapper.toStudentResponseDTO(student)).thenReturn(studentResponseDTO);

        var returnedMap = studentService.getAverageGradeOfStudent(1L);

        Assertions.assertEquals(expectedMap, returnedMap);
        Assertions.assertTrue(expectedMap.containsKey("student's GPA"));
        Assertions.assertTrue(returnedMap.containsValue(9.67));
    }

    @Test
    void testUpdateStudent() {
        StudentUpdateDTO studentUpdateDTO = new StudentUpdateDTO("Sasa", "Stanisic", 4, 8, 1L, 1L);
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "@Password123$", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 8, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(majorService.getById(studentUpdateDTO.majorId())).thenReturn(major);
        when(statusService.getStatusById(studentUpdateDTO.statusId())).thenReturn(status);
        doCallRealMethod().when(studentMapper).updateStudentFromDTO(studentUpdateDTO, student);
        when(studentRepository.save(student)).thenReturn(student);
        doReturn(studentResponseDTO).when(studentMapper).toStudentResponseDTO(student);

        var updatedStudentDTO = studentService.updateStudent(1L, studentUpdateDTO);

        assertThat(studentResponseDTO).isEqualTo(updatedStudentDTO);
    }

    @Test
    void testUpdatePassword() {
        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO("!Password123", "!Password123");
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO("sasa.stanisic.4377@metropolitan.ac.rs", "!Password123", Role.STUDENT);
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO(1L, "Sasa", "Stanisic", 4377, 4, 8, accountResponseDTO,
                new MajorResponseDTO(1L, "SE", "Software engineering", "Software engineering major", 4),
                new Status(1L, "Traditional"));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(passwordEncoder.encode(passwordUpdateDTO.password())).thenReturn(accountResponseDTO.password());
        doNothing().when(authenticationService).canUpdatePassword(student.getAccount().getEmail());
        when(studentRepository.save(student)).thenReturn(student);
        doReturn(studentResponseDTO).when(studentMapper).toStudentResponseDTO(student);

        var updatedStudentDTO = studentService.updatePassword(1L, passwordUpdateDTO);

        assertThat(studentResponseDTO).isEqualTo(updatedStudentDTO);
    }

    @Test
    void testDeleteStudent() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);
        Assertions.assertDoesNotThrow(() -> studentService.deleteStudent(1L));
    }

    @Test
    void testDeleteStudent_NotFound() {
        doReturn(false).when(studentRepository).existsById(1L);
        Assertions.assertThrows(NotFoundException.class, () -> studentService.deleteStudent(1L));
    }

}
