package es.safareyes.fitzone.dto.response;

import java.time.LocalDateTime;

public record ReservaRecepcionDto(
        Integer reservaId,
        Integer socioId,
        String nombreSocio,
        String apellidoSocio,
        LocalDateTime fechaReserva,
        Boolean estado,
        Boolean asistio
) {}