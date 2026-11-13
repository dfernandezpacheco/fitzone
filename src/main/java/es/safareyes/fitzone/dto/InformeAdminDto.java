package es.safareyes.fitzone.dto;

import java.math.BigDecimal;
import java.util.List;

public record InformeAdminDto(
        Integer anio,
        BigDecimal ingresosAnio,
        long cuotasPagadas,
        long cuotasPendientes,
        long cuotasCanceladas,
        long totalCuotas,
        List<IngresoMensualDto> ingresosMensuales,
        long totalAltasAnio,
        List<AltaMensualDto> altasMensuales
) {}