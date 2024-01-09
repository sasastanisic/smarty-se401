package com.smarty.domain.activity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ActivityRequestDTO(

        @NotBlank(message = "Activity name can't be blank")
        String activityName,

        @PositiveOrZero(message = "Number of points can't be negative")
        double points,

        String comment,

        @NotNull(message = "Task can't be null")
        Long taskId,

        @NotNull(message = "Student can't be null")
        Long studentId

) { }
