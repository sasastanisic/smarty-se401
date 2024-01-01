package com.smarty.domain.account.model;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateDTO(

        @NotBlank(message = "Password can't be blank")
        String password,

        @NotBlank(message = "Confirmed password can't be blank")
        String confirmedPassword

) { }
