package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.modelos.TipoPlan;

public record GuardarPlanDto(
        String nombre,
        Double precioMensual,
        TipoPlan tipo
) {}