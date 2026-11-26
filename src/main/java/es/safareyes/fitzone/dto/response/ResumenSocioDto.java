package es.safareyes.fitzone.dto.response;

import java.util.List;

public record ResumenSocioDto(
        Integer clasesEsteMes,
        Integer reservasFuturas,
        String estadoCuota,
        ActividadResumenDto proximaActividad,
        List<ActividadResumenDto> actividadesRecientes
) {}
