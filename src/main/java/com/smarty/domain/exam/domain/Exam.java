package com.smarty.domain.exam.domain;

import com.smarty.domain.course.domain.Course;
import com.smarty.domain.student.entity.Student;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity(name = "exam")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String examinationPeriod;

    @Column(nullable = false)
    private int grade;

    @Column(nullable = false)
    private double points;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfExamination;

    @Column
    private String comment;

    @Column(nullable = false)
    private double totalPoints;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Exam() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExaminationPeriod() {
        return examinationPeriod;
    }

    public void setExaminationPeriod(String examinationPeriod) {
        this.examinationPeriod = examinationPeriod;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public LocalDate getDateOfExamination() {
        return dateOfExamination;
    }

    public void setDateOfExamination(LocalDate dateOfExamination) {
        this.dateOfExamination = dateOfExamination;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(double totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

}
