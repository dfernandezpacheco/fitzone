package es.safareyes.fitzone.dto;

import java.time.LocalDate;

public record SocioResponseDto(
        Integer id,
        String dni,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        String matricula,
        Boolean activo,
        LocalDate fechaAlta,
        PlanResponseDto plan
) {}