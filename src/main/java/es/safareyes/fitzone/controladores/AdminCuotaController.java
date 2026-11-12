package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.CuotaAdminDto;
import es.safareyes.fitzone.servicios.CuotaAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cuotas")
@RequiredArgsConstructor
public class AdminCuotaController {

    private final CuotaAdminService cuotaAdminService;

    @GetMapping
    public ResponseEntity<Page<CuotaAdminDto>> listarCuotas(
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(
                cuotaAdminService.listarCuotas(pageable)
        );
    }
}