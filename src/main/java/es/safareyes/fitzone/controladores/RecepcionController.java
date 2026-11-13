package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.RecepcionResumenDto;
import es.safareyes.fitzone.servicios.RecepcionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recepcion")
@RequiredArgsConstructor
public class RecepcionController {

    private final RecepcionService recepcionService;

    @GetMapping("/resumen")
    public ResponseEntity<RecepcionResumenDto> obtenerResumen() {

        return ResponseEntity.ok(
                recepcionService.obtenerResumen()
        );
    }
}