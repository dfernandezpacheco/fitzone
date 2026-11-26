package es.safareyes.fitzone.dto.request;

import jakarta.validation.constraints.NotNull;

public record ActualizarAsistenciaDto(
        @NotNull Boolean asistio
) {}