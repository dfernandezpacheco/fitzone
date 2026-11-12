package es.safareyes.fitzone.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record ActividadResumenDto(
        String nombre,
        LocalDateTime fecha,
        LocalTime hora,
        String sala,
        String monitor
) {}
