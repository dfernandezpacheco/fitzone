package es.safareyes.fitzone.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CuotaAdminDto(
        Integer id,
        Integer mes,
        Integer anio,
        BigDecimal importe,
        String estado,
        LocalDateTime fechaPago,
        Integer socioId,
        String nombreSocio,
        String apellidosSocio,
        String dni,
        String nombrePlan
) {}