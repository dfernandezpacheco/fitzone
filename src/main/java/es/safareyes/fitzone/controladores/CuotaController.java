package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.response.CuotaAdminDto;
import es.safareyes.fitzone.dto.response.GeneracionCuotasResponseDto;
import es.safareyes.fitzone.dto.request.PagarCuotaRequestDto;
import es.safareyes.fitzone.servicios.CuotaAdminService;
import es.safareyes.fitzone.servicios.RecepcionCuotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaAdminService cuotaAdminService;
    private final RecepcionCuotaService recepcionCuotaService;

    // Endpoint 7: POST /cuotas/generar?mes=&anio= (Generación mensual - RN-04)
    @PostMapping("/generar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneracionCuotasResponseDto> generarCuotas(
            @RequestParam Integer mes,
            @RequestParam Integer anio) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cuotaAdminService.generarCuotasMensuales(mes, anio));
    }

    // Endpoint 8: POST /cuotas/pagar (Registrar pago de una cuota)
    @PostMapping("/pagar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<CuotaAdminDto> pagarCuota(@Valid @RequestBody PagarCuotaRequestDto dto) {
        return ResponseEntity.ok(recepcionCuotaService.registrarPago(dto.cuotaId()));
    }

    // Endpoint 10: GET /cuotas/pendientes?mes=&anio= (Socios morosos de un mes)
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<List<CuotaAdminDto>> listarPendientes(
            @RequestParam Integer mes,
            @RequestParam Integer anio) {
        return ResponseEntity.ok(cuotaAdminService.listarPendientes(mes, anio));
    }
}