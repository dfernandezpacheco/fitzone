package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.validaciones.ValidDni;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CrearSocioDto(
        @NotBlank(message = "El DNI es obligatorio")
        @ValidDni
        String dni,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato de email no es válido")
        String email,

        String telefono,

        @Pattern(regexp = "^$|^[0-9]{4}[B-DF-HJ-NP-TV-Z]{3}$", message = "La matrícula debe tener formato español (1234BCD)")
        String matricula,

        @NotNull(message = "El ID del plan es obligatorio")
        Integer planId
) {}