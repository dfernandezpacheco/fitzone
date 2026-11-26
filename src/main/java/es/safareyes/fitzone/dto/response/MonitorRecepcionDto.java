package es.safareyes.fitzone.dto.response;

public record MonitorRecepcionDto(
        Integer id,
        String nombre,
        String apellido,
        Integer telefono
) {}