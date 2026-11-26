package es.safareyes.fitzone.dto.response;

import java.time.LocalDateTime;

public record AccesoResponseDto(
        Integer id,
        String dni,
        String nombreSocio,
        String resultado,
        Integer codigoMotivo,
        String motivoDenegacion,
        LocalDateTime fechaHora
) {}