package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.*;
import es.safareyes.fitzone.servicios.RecepcionTurnosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/recepcion/turnos")
@RequiredArgsConstructor
public class RecepcionTurnosController {

    private final RecepcionTurnosService recepcionTurnosService;

    @GetMapping
    public ResponseEntity<List<TurnoRecepcionDto>> listarTurnos(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha
    ) {

        return ResponseEntity.ok(
                recepcionTurnosService.listarTurnos(fecha)
        );
    }
    @GetMapping("/monitores")
    public ResponseEntity<List<MonitorRecepcionDto>> listarMonitores() {

        return ResponseEntity.ok(
                recepcionTurnosService.listarMonitores()
        );
    }
    @PostMapping
    public ResponseEntity<TurnoRecepcionDto> crearTurno(
            @Valid @RequestBody CrearTurnoRecepcionDto dto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(recepcionTurnosService.crearTurno(dto));
    }

    @GetMapping("/{actividadId}/reservas")
    public ResponseEntity<List<ReservaRecepcionDto>> listarReservasActividad(
            @PathVariable Integer actividadId
    ) {
        return ResponseEntity.ok(
                recepcionTurnosService.listarReservasActividad(actividadId)
        );
    }

    @PutMapping("/{actividadId}/reservas/{reservaId}/asistencia")
    public ResponseEntity<Void> actualizarAsistencia(
            @PathVariable Integer actividadId,
            @PathVariable Integer reservaId,
            @Valid @RequestBody ActualizarAsistenciaDto dto
    ) {
        recepcionTurnosService.actualizarAsistencia(actividadId, reservaId, dto.asistio());
        return ResponseEntity.noContent().build();
    }
}