package es.safareyes.fitzone.dto;

import jakarta.validation.constraints.NotBlank;

public record IniciarSesionDto(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        String usuario,

        @NotBlank(message = "La contraseña es obligatoria")
        String clave

) {}