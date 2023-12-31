package com.smarty.domain.professor.model;

import com.smarty.domain.account.model.AccountUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProfessorUpdateDTO(

        @NotBlank(message = "Name of professor can't be blank")
        String name,

        @NotBlank(message = "Surname of professor can't be blank")
        String surname,

        @Min(value = 0, message = "Years of experience can't be negative")
        int yearsOfExperience,

        @Valid
        AccountUpdateDTO account,

        @NotBlank(message = "Role can't be blank")
        String role

) { }
