package es.safareyes.fitzone.dto.response;

public record ResumenCuotasRecepcionDto(
        long totalCuotas,
        long cuotasPagadas,
        long cuotasPendientes,
        long cuotasCanceladas
) {}