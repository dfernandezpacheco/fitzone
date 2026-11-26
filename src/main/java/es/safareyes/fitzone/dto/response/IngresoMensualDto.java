package es.safareyes.fitzone.dto.response;

import java.math.BigDecimal;

public record IngresoMensualDto(
        Integer mes,
        BigDecimal ingresos
) {}