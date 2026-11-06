package es.safareyes.fitzone.dto;

import java.time.LocalDate;

public record MiPerfilSocioDto(
        Integer id,
        String nombre,
        String apellidos,
        String usuario,
        String email,
        String telefono,
        LocalDate fechaAlta,
        Boolean activo,
        Integer planId,
        String planNombre,
        Double precioMensual
) {}