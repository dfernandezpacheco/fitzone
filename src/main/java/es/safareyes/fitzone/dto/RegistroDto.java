package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.validaciones.ValidDni;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroDto(
        @NotBlank @ValidDni String dni,
        @NotBlank @Size(max = 120) String nombreCompleto,
        @NotBlank @Email String correo,
        @NotBlank @Size(max = 30) String telefono,
        @Pattern(regexp = "^$|^[0-9]{4}[B-DF-HJ-NP-TV-Zb-df-hj-np-tv-z]{3}$", message = "La matrícula debe tener formato español (1234BCD)") String matricula,
        @NotBlank @Size(min = 8, max = 72) String contrasena,
        @NotBlank String confirmarContrasena,
        @NotNull Integer planId
) {}
