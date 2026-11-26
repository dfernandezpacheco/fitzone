package es.safareyes.fitzone.dto.response;

public record SesionIniciadaDto(

        Integer id,
        String usuario,
        Integer rol,
        String token

) {}