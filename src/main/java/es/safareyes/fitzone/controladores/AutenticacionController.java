package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.request.IniciarSesionDto;
import es.safareyes.fitzone.dto.response.SesionIniciadaDto;
import es.safareyes.fitzone.dto.request.RegistroDto;
import es.safareyes.fitzone.servicios.AutenticacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/auth", "/api/v1/auth"})
@RequiredArgsConstructor
public class AutenticacionController {

    private final AutenticacionService autenticacionService;

    @PostMapping("/login")
    public ResponseEntity<SesionIniciadaDto> iniciarSesion(@Valid @RequestBody IniciarSesionDto dto) {
        return ResponseEntity.ok(autenticacionService.iniciarSesion(dto));
    }

    @PostMapping("/registro")
    public ResponseEntity<SesionIniciadaDto> registrar(@Valid @RequestBody RegistroDto dto) {
        return ResponseEntity.status(201).body(autenticacionService.registrar(dto));
    }
}
