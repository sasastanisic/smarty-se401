package com.smarty.domain.exam.service;

import com.smarty.domain.activity.service.ActivityService;
import com.smarty.domain.course.domain.Course;
import com.smarty.domain.course.service.CourseService;
import com.smarty.domain.exam.domain.Exam;
import com.smarty.domain.exam.model.ExamRequestDTO;
import com.smarty.domain.exam.model.ExamResponseDTO;
import com.smarty.domain.exam.model.ExamUpdateDTO;
import com.smarty.domain.exam.repository.ExamRepository;
import com.smarty.domain.student.entity.Student;
import com.smarty.domain.student.service.StudentService;
import com.smarty.infrastructure.email.EmailNotificationService;
import com.smarty.infrastructure.exception.exceptions.BadRequestException;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.ForbiddenException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.ExamMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    private static final String EXAM_NOT_EXISTS = "Exam with id %d doesn't exist";
    private static final int MIN_ACTIVITY_POINTS_REQUIRED = 35;

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final StudentService studentService;
    private final CourseService courseService;
    private final ActivityService activityService;
    private final EmailNotificationService emailNotificationService;

    public ExamServiceImpl(ExamRepository examRepository,
                           ExamMapper examMapper,
                           StudentService studentService,
                           CourseService courseService,
                           @Lazy ActivityService activityService,
                           EmailNotificationService emailNotificationService) {
        this.examRepository = examRepository;
        this.examMapper = examMapper;
        this.studentService = studentService;
        this.courseService = courseService;
        this.activityService = activityService;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public ExamResponseDTO createExam(ExamRequestDTO examRequestDTO) {
        Exam exam = examMapper.toExam(examRequestDTO);
        var student = studentService.getById(examRequestDTO.studentId());
        var course = courseService.getById(examRequestDTO.courseId());

        double totalPoints = getTotalPoints(examRequestDTO.studentId(), examRequestDTO.courseId(), examRequestDTO.points());
        int grade = calculateGrade(totalPoints);
        grade = checkGrade(examRequestDTO.points(), grade);

        checkCourseAndStudentYear(student, course);
        isExamAlreadyPassed(student, course);
        validateTotalActivityPoints(examRequestDTO.studentId(), examRequestDTO.courseId());
        activityService.isProjectFinished(examRequestDTO.studentId());
        validateExaminationPeriod(examRequestDTO.examinationPeriod());

        emailNotificationService.sendExamNotification(student.getAccount().getEmail(), course.getCode(), course.getFullName(),
                student.getName(), examRequestDTO.points(), grade);

        exam.setGrade(grade);
        exam.setDateOfExamination(LocalDate.now());
        exam.setTotalPoints(totalPoints);
        exam.setStudent(student);
        exam.setCourse(course);
        examRepository.save(exam);

        return examMapper.toExamResponseDTO(exam);
    }

    private double getTotalPoints(Long studentId, Long courseId, double examPoints) {
        var totalActivityPoints = activityService.getTotalActivityPointsByCourse(studentId, courseId);
        return totalActivityPoints == null ? 0.0 : totalActivityPoints + examPoints;
    }

    private int calculateGrade(double totalPoints) {
        int bestGrade = 10;

        return switch ((int) Math.floor(totalPoints / 10)) {
            case 10, 9 -> bestGrade;
            case 8 -> 9;
            case 7 -> 8;
            case 6 -> 7;
            case 5 -> 6;
            default -> 5;
        };
    }

    private int checkGrade(double examPoints, int grade) {
        return examPoints < 15 ? 5 : grade;
    }

    @Override
    public void checkCourseAndStudentYear(Student student, Course course) {
        if (student.getYear() < course.getYear()) {
            throw new ForbiddenException("Student %s can't take the exam because course %s is in a year higher that the student's year of study"
                    .formatted(student.getName(), course.getCode()));
        }
    }

    @Override
    public void isExamAlreadyPassed(Student student, Course course) {
        if (examRepository.isExamAlreadyPassed(student, course)) {
            throw new ConflictException("Student %s has already passed the %s exam".formatted(student.getName(), course.getCode()));
        }
    }

    private void validateTotalActivityPoints(Long studentId, Long courseId) {
        var totalActivityPoints = activityService.getTotalActivityPointsByCourse(studentId, courseId);

        totalActivityPoints = totalActivityPoints == null ? 0.0 : totalActivityPoints;

        if (totalActivityPoints < MIN_ACTIVITY_POINTS_REQUIRED) {
            throw new ForbiddenException("Student can't take the exam because he needs at least 35 points for activities. " +
                    "Right now he has %.2f points".formatted(totalActivityPoints));
        }
    }

    private void validateExaminationPeriod(String examinationPeriod) {
        List<String> validPeriods = List.of("January A", "January B", "April", "June A", "June B", "September", "October", "December");

        if (!validPeriods.contains(examinationPeriod)) {
            throw new BadRequestException("Examination period %s isn't valid".formatted(examinationPeriod));
        }
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
    public List<ExamResponseDTO> getExamHistoryByStudent(Long studentId) {
        List<Exam> examHistoryByStudent = examRepository.findExamHistoryByStudent(studentId);
        studentService.existsById(studentId);

        if (examHistoryByStudent.isEmpty()) {
            throw new NotFoundException("Exam history by student with id %d is empty".formatted(studentId));
        }

        return getExamListResponseDTO(examHistoryByStudent);
    }

    @Override
    public List<ExamResponseDTO> getExamHistoryByCourse(Long courseId) {
        List<Exam> examHistoryByCourse = examRepository.findExamHistoryByCourse(courseId);
        courseService.existsById(courseId);

        if (examHistoryByCourse.isEmpty()) {
            throw new NotFoundException("Exam history by course with id %d is empty".formatted(courseId));
        }

        return getExamListResponseDTO(examHistoryByCourse);
    }

    @Override
    public List<ExamResponseDTO> getPassedExamsByStudent(Long studentId, int year) {
        List<Exam> passedExamsByStudent = examRepository.findPassedExamsByStudent(studentId, year);
        studentService.existsById(studentId);
        courseService.existsByYear(year);

        if (passedExamsByStudent.isEmpty()) {
            throw new NotFoundException("List of passed exams is empty");
        }

        return getExamListResponseDTO(passedExamsByStudent);
    }

    private List<ExamResponseDTO> getExamListResponseDTO(List<Exam> examList) {
        return examList
                .stream()
                .map(examMapper::toExamResponseDTO)
                .toList();
    }

    @Override
    public ExamResponseDTO updateExam(Long id, ExamUpdateDTO examUpdateDTO) {
        Exam exam = getById(id);
        examMapper.updateExamFromDTO(examUpdateDTO, exam);

        validateTotalActivityPoints(exam.getStudent().getId(), exam.getCourse().getId());
        exam.setDateOfExamination(LocalDate.now());
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
