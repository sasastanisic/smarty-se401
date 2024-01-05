package com.smarty.domain.task.model;

import com.smarty.domain.task.enums.Type;
import com.smarty.infrastructure.validation.EnumValidation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequestDTO(

        @NotBlank(message = "Task type can't be blank")
        @EnumValidation(value = Type.class)
        String type,

        @Min(value = 1, message = "Minimum value for points is 1")
        @Max(value = 30, message = "Maximum value for points is 30")
        double maxPoints,

        @Min(value = 1, message = "Minimum number of tasks is 1")
        @Max(value = 15, message = "Maximum number of tasks is 15")
        int numberOfTasks,

        @NotNull(message = "Course can't be null")
        Long courseId

) { }
