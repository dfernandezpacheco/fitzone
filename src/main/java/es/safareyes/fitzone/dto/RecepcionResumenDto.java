package es.safareyes.fitzone.dto;

import java.util.List;

public record RecepcionResumenDto(
        long sociosActivos,
        long pagosPendientes,
        long turnosHoy,
        long reservasHoy,
        List<SocioResponseDto> sociosRecientes
) {}