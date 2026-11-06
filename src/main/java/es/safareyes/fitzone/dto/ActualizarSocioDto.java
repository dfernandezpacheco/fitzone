package es.safareyes.fitzone.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ActualizarSocioDto(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email
        String email,

        String telefono,

        @Pattern(regexp = "^$|^[0-9]{4}[B-DF-HJ-NP-TV-Z]{3}$")
        String matricula,

        @NotNull(message = "El ID del plan es obligatorio")
        Integer planId
) {}