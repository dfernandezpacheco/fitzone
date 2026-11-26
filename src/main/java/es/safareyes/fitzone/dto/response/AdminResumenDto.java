package es.safareyes.fitzone.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record AdminResumenDto(
        long totalSocios,
        long sociosActivos,
        long sociosSuspendidos,
        long totalPlanes,
        BigDecimal ingresosMes,
        long cuotasPendientes,
        List<DistribucionPlanDto> distribucionSociosPorPlan
) {}