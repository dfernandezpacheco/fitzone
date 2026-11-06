package es.safareyes.fitzone.dto;

public record SesionIniciadaDto(

        Integer id,
        String usuario,
        Integer rol,
        String token

) {}