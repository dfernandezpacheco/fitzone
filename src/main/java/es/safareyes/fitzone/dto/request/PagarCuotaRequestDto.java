package es.safareyes.fitzone.dto.request;

import jakarta.validation.constraints.NotNull;

public record PagarCuotaRequestDto(
        @NotNull(message = "El ID de la cuota es obligatorio")
        Integer cuotaId
) {}