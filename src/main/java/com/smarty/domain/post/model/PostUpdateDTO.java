package com.smarty.domain.post.model;

import jakarta.validation.constraints.NotBlank;

public record PostUpdateDTO(

        @NotBlank(message = "Title can't be blank")
        String title,

        @NotBlank(message = "Description can't be blank")
        String description

) { }
