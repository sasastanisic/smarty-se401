package com.smarty.domain.exam.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExamRequestDTO(

        @NotBlank(message = "Examination period can't be blank")
        String examinationPeriod,

        @Min(value = 0, message = "Minimum number of points is 0")
        @Max(value = 30, message = "Maximum number of points is 30")
        double points,

        String comment,

        @NotNull(message = "Student can't be null")
        Long studentId,

        @NotNull(message = "Course can't be null")
        Long courseId

) { }
