package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.response.ReservaSocioDto;
import es.safareyes.fitzone.dto.response.TurnoRecepcionDto;
import es.safareyes.fitzone.servicios.RecepcionTurnosService;
import es.safareyes.fitzone.servicios.SocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/actividades")
@RequiredArgsConstructor
public class ActividadController {

    private final RecepcionTurnosService recepcionTurnosService;
    private final SocioService socioService;

    // Endpoint 14: GET /actividades?fecha= (Actividades del día con plazas libres - Público)
    @GetMapping
    public ResponseEntity<List<TurnoRecepcionDto>> listarActividadesPorFecha(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDate fechaConsulta = (fecha != null) ? fecha : LocalDate.now();
        return ResponseEntity.ok(recepcionTurnosService.listarTurnos(fechaConsulta));
    }

    // Endpoint 15: POST /actividades/{id}/reservas (Reservar plaza - RN-08)
    @PostMapping("/{id}/reservas")
    @PreAuthorize("hasRole('SOCIO')")
    public ResponseEntity<ReservaSocioDto> reservarPlaza(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(socioService.reservarActividad(jwt.getSubject(), id));
    }
}