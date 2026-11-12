package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.InformeAdminDto;
import es.safareyes.fitzone.servicios.InformeAdminService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/informes")
@RequiredArgsConstructor
public class AdminInformeController {

    private final InformeAdminService informeAdminService;

    @GetMapping
    public ResponseEntity<InformeAdminDto> obtenerInforme(
            @RequestParam(required = false) Integer anio) {

        int anioConsulta = (anio != null)
                ? anio
                : LocalDate.now().getYear();

        return ResponseEntity.ok(
                informeAdminService.generarInforme(anioConsulta)
        );
    }
}