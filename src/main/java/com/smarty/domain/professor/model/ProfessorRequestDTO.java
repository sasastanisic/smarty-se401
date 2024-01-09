package com.smarty.domain.professor.model;

import com.smarty.domain.account.model.AccountRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProfessorRequestDTO(

        @NotBlank(message = "Name of professor can't be blank")
        String name,

        @NotBlank(message = "Surname of professor can't be blank")
        String surname,

        @PositiveOrZero(message = "Years of experience can't be negative")
        int yearsOfExperience,

        @Valid
        AccountRequestDTO account,

        @NotBlank(message = "Role can't be blank")
        String role

) { }
