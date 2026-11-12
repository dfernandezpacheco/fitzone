package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.modelos.TipoPlan;

public record PlanResponseDto(
        Integer id,
        String nombre,
        Double precioMensual,
        TipoPlan tipo
) {}