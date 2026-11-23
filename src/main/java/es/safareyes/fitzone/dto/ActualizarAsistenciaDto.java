package es.safareyes.fitzone.dto;

import jakarta.validation.constraints.NotNull;

public record ActualizarAsistenciaDto(
        @NotNull Boolean asistio
) {}