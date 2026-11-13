package es.safareyes.fitzone.dto;

public record ResumenCuotasRecepcionDto(
        long totalCuotas,
        long cuotasPagadas,
        long cuotasPendientes,
        long cuotasCanceladas
) {}