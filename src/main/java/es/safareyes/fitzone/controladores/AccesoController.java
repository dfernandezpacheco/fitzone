package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.request.AccesoRequestDto;
import es.safareyes.fitzone.dto.response.AccesoResponseDto;
import es.safareyes.fitzone.dto.response.AforoResponseDto;
import es.safareyes.fitzone.servicios.AccesoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accesos")
@RequiredArgsConstructor
public class AccesoController {

    private final AccesoService accesoService;

    // Endpoint 11: POST /accesos/entrada (Torno de entrada - RN-05, RN-06)
    @PostMapping("/entrada")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<AccesoResponseDto> registrarEntrada(@Valid @RequestBody AccesoRequestDto dto) {
        return ResponseEntity.ok(accesoService.registrarEntrada(dto.dni()));
    }

    // Endpoint 12: POST /accesos/salida (Torno de salida - RN-07)
    @PostMapping("/salida")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<AccesoResponseDto> registrarSalida(@Valid @RequestBody AccesoRequestDto dto) {
        return ResponseEntity.ok(accesoService.registrarSalida(dto.dni()));
    }

    // Endpoint 13: GET /accesos/aforo (Personas dentro y aforo máximo - RF-05)
    @GetMapping("/aforo")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<AforoResponseDto> consultarAforo() {
        return ResponseEntity.ok(accesoService.consultarAforo());
    }
}