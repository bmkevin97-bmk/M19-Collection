package com.m19collection.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateQuantityRequest(
        @NotNull @Min(1) Integer cantidad
) {

}