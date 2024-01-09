package com.smarty.domain.professor.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProfessorUpdateDTO(

        @NotBlank(message = "Name of professor can't be blank")
        String name,

        @NotBlank(message = "Surname of professor can't be blank")
        String surname,

        @PositiveOrZero(message = "Years of experience can't be negative")
        int yearsOfExperience,

        @NotBlank(message = "Role can't be blank")
        String role

) { }
