package es.safareyes.fitzone.dto;

import java.math.BigDecimal;

public record AdminResumenDto(
        long totalSocios,
        long sociosActivos,
        long sociosSuspendidos,
        long totalPlanes,
        BigDecimal ingresosMes
) {}