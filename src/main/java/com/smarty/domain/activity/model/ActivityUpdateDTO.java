package com.smarty.domain.activity.model;

import jakarta.validation.constraints.PositiveOrZero;

public record ActivityUpdateDTO(

        @PositiveOrZero(message = "Number of points can't be negative")
        double points,

        String comment

) { }
