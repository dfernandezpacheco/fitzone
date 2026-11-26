package es.safareyes.fitzone.dto.response;

public record GeneracionCuotasResponseDto(
        int mes,
        int anio,
        int cuotasGeneradas,
        int sociosDesactivados
) {}