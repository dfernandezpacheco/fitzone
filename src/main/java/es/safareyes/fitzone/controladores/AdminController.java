package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.AdminResumenDto;
import es.safareyes.fitzone.servicios.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/resumen")
    public ResponseEntity<AdminResumenDto> obtenerResumen() {
        return ResponseEntity.ok(adminService.obtenerResumen());
    }
}