package com.smarty.domain.account.model;

import jakarta.validation.constraints.NotBlank;

public record AccountUpdateDTO(

        @NotBlank(message = "Password can't be blank")
        String password

) { }
