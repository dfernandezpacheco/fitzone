package es.safareyes.fitzone.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record ActividadDisponibleSocioDto(
        Integer id,
        String nombre,
        LocalDateTime dia,
        LocalTime hora,
        LocalTime duracion,
        String sala,
        Integer aforoMaximo,
        Long plazasDisponibles,
        String monitor
) {}