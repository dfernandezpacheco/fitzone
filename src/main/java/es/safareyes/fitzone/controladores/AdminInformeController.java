package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.InformeAdminDto;
import es.safareyes.fitzone.servicios.InformeAdminService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

@RestController
@RequestMapping("/admin/informes")
@RequiredArgsConstructor
public class AdminInformeController {

    private final InformeAdminService informeAdminService;

    @GetMapping
    public ResponseEntity<InformeAdminDto> obtenerInforme(
            @RequestParam(required = false) Integer anio) {

        int anioSolicitado = anio != null
                ? anio
                : Year.now().getValue();

        return ResponseEntity.ok(
                informeAdminService.generarInforme(anioSolicitado)
        );
    }
}