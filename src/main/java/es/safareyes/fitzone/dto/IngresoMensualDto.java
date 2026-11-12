package es.safareyes.fitzone.dto;

import java.math.BigDecimal;

public record IngresoMensualDto(
        Integer mes,
        BigDecimal ingresos
) {}