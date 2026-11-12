package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.GuardarPlanDto;
import es.safareyes.fitzone.dto.PlanResponseDto;
import es.safareyes.fitzone.servicios.PlanAdminService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/planes")
@RequiredArgsConstructor
public class AdminPlanController {

    private final PlanAdminService planAdminService;

    // CREAR PLAN
    @PostMapping
    public ResponseEntity<PlanResponseDto> crearPlan(
            @RequestBody GuardarPlanDto dto) {
        PlanResponseDto resultado =
                planAdminService.crearPlan(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resultado);
    }

    // ACTUALIZAR PLAN
    @PutMapping("/{id}")
    public ResponseEntity<PlanResponseDto> actualizarPlan(
            @PathVariable Integer id,
            @RequestBody GuardarPlanDto dto) {
        PlanResponseDto resultado =
                planAdminService.actualizarPlan(id, dto);
        return ResponseEntity.ok(resultado);
    }
}