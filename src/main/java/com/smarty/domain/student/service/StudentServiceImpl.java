package com.smarty.domain.student.service;

import com.smarty.domain.account.enums.Role;
import com.smarty.domain.account.model.PasswordUpdateDTO;
import com.smarty.domain.account.service.AccountService;
import com.smarty.domain.course.service.CourseService;
import com.smarty.domain.major.service.MajorService;
import com.smarty.domain.status.service.StatusService;
import com.smarty.domain.student.entity.Student;
import com.smarty.domain.student.model.StudentRequestDTO;
import com.smarty.domain.student.model.StudentResponseDTO;
import com.smarty.domain.student.model.StudentUpdateDTO;
import com.smarty.domain.student.repository.StudentRepository;
import com.smarty.infrastructure.exception.exceptions.BadRequestException;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.StudentMapper;
import com.smarty.infrastructure.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private static final String STUDENT_NOT_EXISTS = "Student with id %d doesn't exist";

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final AccountService accountService;
    private final MajorService majorService;
    private final StatusService statusService;
    private final CourseService courseService;
    private final AuthenticationService authenticationService;
    private PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository,
                              StudentMapper studentMapper,
                              AccountService accountService,
                              MajorService majorService,
                              StatusService statusService,
                              @Lazy CourseService courseService,
                              AuthenticationService authenticationService) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.accountService = accountService;
        this.majorService = majorService;
        this.statusService = statusService;
        this.courseService = courseService;
        this.authenticationService = authenticationService;
    }

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) {
        Student student = studentMapper.toStudent(studentRequestDTO);
        var major = majorService.getById(studentRequestDTO.majorId());
        var status = statusService.getStatusById(studentRequestDTO.statusId());
        var encryptedPassword = encodePassword(studentRequestDTO.account().password());

        validateIndex(studentRequestDTO.index());
        validateYearAndSemester(studentRequestDTO.year(), studentRequestDTO.semester());
        accountService.validateEmail(studentRequestDTO.account().email());

        student.getAccount().setPassword(encryptedPassword);
        student.getAccount().setRole(Role.STUDENT);
        student.setMajor(major);
        student.setStatus(status);
        studentRepository.save(student);

        return studentMapper.toStudentResponseDTO(student);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void validateIndex(int index) {
        if (studentRepository.existsByIndex(index)) {
            throw new ConflictException("Student with index %d already exists".formatted(index));
        }
    }

    private void validateYearAndSemester(int year, int semester) {
        Map<Integer, List<Integer>> validCombinations = Map.of(
                1, List.of(1, 2),
                2, List.of(3, 4),
                3, List.of(5, 6),
                4, List.of(7, 8)
        );

        List<Integer> allowedValues = validCombinations.get(year);

        if (!allowedValues.contains(semester)) {
            throw new BadRequestException("Combination of year %d and semester %d isn't valid".formatted(year, semester));
        }
    }

    @Override
    public Page<StudentResponseDTO> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).map(studentMapper::toStudentResponseDTO);
    }

    @Override
    public StudentResponseDTO getStudentById(Long id) {
        return studentMapper.toStudentResponseDTO(getById(id));
    }

    public Student getById(Long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isEmpty()) {
            throw new NotFoundException(STUDENT_NOT_EXISTS.formatted(id));
        }

        return optionalStudent.get();
    }

    @Override
    public StudentResponseDTO getStudentByEmail(String email) {
        Student student = studentRepository.findByAccount_Email(email);

        if (student == null) {
            throw new NotFoundException("Student with email %s doesn't exist".formatted(email));
        }

        return studentMapper.toStudentResponseDTO(student);
    }

    @Override
    public void existsById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new NotFoundException(STUDENT_NOT_EXISTS.formatted(id));
        }
    }

    @Override
    public List<StudentResponseDTO> getStudentsByMajor(Long majorId) {
        List<Student> studentsByMajor = studentRepository.findStudentsByMajor_Id(majorId);
        majorService.existsById(majorId);

        if (studentsByMajor.isEmpty()) {
            throw new NotFoundException("List of students by major is empty");
        }

        return getStudentListResponseDTO(studentsByMajor);
    }

    @Override
    public List<StudentResponseDTO> getStudentsByStudyStatus(Long statusId) {
        List<Student> studentsByStudyStatus = studentRepository.findStudentsByStatus_Id(statusId);
        statusService.existsById(statusId);

        if (studentsByStudyStatus.isEmpty()) {
            throw new NotFoundException("List of students by study status is empty");
        }

        return getStudentListResponseDTO(studentsByStudyStatus);
    }

    @Override
    public List<StudentResponseDTO> getStudentsWhoPassedCertainCourse(Long courseId) {
        List<Student> studentsWhoPassedCertainCourse = studentRepository.findStudentsWhoPassedCertainCourse(courseId);
        courseService.existsById(courseId);

        if (studentsWhoPassedCertainCourse.isEmpty()) {
            throw new NotFoundException("There are 0 students that passed course with id %d".formatted(courseId));
        }

        return getStudentListResponseDTO(studentsWhoPassedCertainCourse);
    }

    private List<StudentResponseDTO> getStudentListResponseDTO(List<Student> studentList) {
        return studentList
                .stream()
                .map(studentMapper::toStudentResponseDTO)
                .toList();
    }

    @Override
    public Map<String, Object> getAverageGradeOfStudent(Long id) {
        Student student = getById(id);
        var averageGrade = studentRepository.findAverageGradeOfStudent(id);

        return Map.of(
                "student", studentMapper.toStudentResponseDTO(student),
                "student's GPA", averageGrade == null ? 0.0 : averageGrade
        );
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentUpdateDTO studentUpdateDTO) {
        Student student = getById(id);
        var major = majorService.getById(studentUpdateDTO.majorId());
        var status = statusService.getStatusById(studentUpdateDTO.statusId());
        studentMapper.updateStudentFromDTO(studentUpdateDTO, student);

        validateYearAndSemester(studentUpdateDTO.year(), studentUpdateDTO.semester());
        student.setMajor(major);
        student.setStatus(status);
        studentRepository.save(student);

        return studentMapper.toStudentResponseDTO(student);
    }

    @Override
    public StudentResponseDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO) {
        Student student = getById(id);
        var encryptedPassword = encodePassword(passwordUpdateDTO.password());

        authenticationService.canUpdatePassword(student.getAccount().getEmail());
        arePasswordsMatching(passwordUpdateDTO.password(), passwordUpdateDTO.confirmedPassword());
        student.getAccount().setPassword(encryptedPassword);
        studentRepository.save(student);

        return studentMapper.toStudentResponseDTO(student);
    }

    private void arePasswordsMatching(String password, String confirmedPassword) {
        if (!password.matches(confirmedPassword)) {
            throw new BadRequestException("Passwords aren't matching");
        }
    }

    @Override
    public void deleteStudent(Long id) {
        existsById(id);
        studentRepository.deleteById(id);
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

}
