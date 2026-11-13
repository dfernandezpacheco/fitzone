package es.safareyes.fitzone.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record CrearTurnoRecepcionDto(

        @NotBlank(message = "El nombre de la actividad es obligatorio")
        String nombre,

        @NotNull(message = "Debes seleccionar una fecha")
        @FutureOrPresent(message = "La fecha no puede ser anterior a hoy")
        LocalDate fecha,

        @NotNull(message = "Debes seleccionar una hora")
        LocalTime hora,

        @NotNull(message = "La duración es obligatoria")
        LocalTime duracion,

        @NotBlank(message = "La sala es obligatoria")
        String sala,

        @NotNull(message = "El aforo es obligatorio")
        @Min(value = 1, message = "El aforo mínimo es 1")
        @Max(value = 100, message = "El aforo máximo es 100")
        Integer aforoMaximo,

        @NotNull(message = "Debes seleccionar un monitor")
        Integer monitorId

) {}