package es.safareyes.fitzone.dto;

public record PlanResponseDto(
        Integer id,
        String nombre,
        Double precioMensual
) {}