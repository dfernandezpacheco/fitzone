package es.safareyes.fitzone.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservaListadoRecepcionDto(
        Integer reservaId,
        Integer actividadId,
        String nombreActividad,
        LocalDateTime dia,
        LocalTime hora,
        String nombreSocio,
        String dniSocio,
        String nombreMonitor,
        Boolean estado,
        Boolean asistio
) {}