package es.safareyes.fitzone.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AccesoRequestDto(
        @NotBlank(message = "El DNI es obligatorio")
        String dni
) {}