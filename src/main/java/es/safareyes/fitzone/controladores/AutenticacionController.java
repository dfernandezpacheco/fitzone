package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.IniciarSesionDto;
import es.safareyes.fitzone.dto.SesionIniciadaDto;
import es.safareyes.fitzone.servicios.AutenticacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticacionController {

    private final AutenticacionService autenticacionService;

    @PostMapping("/login")
    public ResponseEntity<SesionIniciadaDto> iniciarSesion(@Valid @RequestBody IniciarSesionDto dto) {
        return ResponseEntity.ok(autenticacionService.iniciarSesion(dto));
    }
}