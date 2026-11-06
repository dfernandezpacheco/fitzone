package es.safareyes.fitzone.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservaSocioDto(
        Integer id,
        Boolean estado,
        LocalDateTime fechaReserva,
        Integer actividadId,
        String actividadNombre,
        LocalDateTime fechaActividad,
        LocalTime hora,
        LocalTime duracion,
        String sala,
        String monitor
) {}