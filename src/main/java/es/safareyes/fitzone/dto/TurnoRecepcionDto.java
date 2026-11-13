package es.safareyes.fitzone.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record TurnoRecepcionDto(
        Integer id,
        String nombre,
        LocalDateTime dia,
        LocalTime hora,
        LocalTime duracion,
        String sala,
        Integer aforoMaximo,
        Integer monitorId,
        String nombreMonitor,
        Long reservasConfirmadas
) {}