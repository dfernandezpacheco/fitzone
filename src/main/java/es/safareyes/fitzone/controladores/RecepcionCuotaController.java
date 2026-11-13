package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.CuotaAdminDto;
import es.safareyes.fitzone.dto.ResumenCuotasRecepcionDto;
import es.safareyes.fitzone.servicios.RecepcionCuotaService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recepcion/cuotas")
@RequiredArgsConstructor
public class RecepcionCuotaController {

    private final RecepcionCuotaService recepcionCuotaService;

    @GetMapping
    public ResponseEntity<Page<CuotaAdminDto>> listarCuotas(
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(
                recepcionCuotaService.listarCuotas(pageable)
        );
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenCuotasRecepcionDto> obtenerResumen() {

        return ResponseEntity.ok(
                recepcionCuotaService.obtenerResumen()
        );
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<CuotaAdminDto> registrarPago(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                recepcionCuotaService.registrarPago(id)
        );
    }
}