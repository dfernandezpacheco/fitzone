package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.dto.*;
import es.safareyes.fitzone.servicios.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import es.safareyes.fitzone.dto.ReservaSocioDto;
import java.util.List;

@RestController
@RequestMapping("/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;

    // Endpoints 1: POST
    @PostMapping
    public ResponseEntity<SocioResponseDto> crearSocio(@Valid @RequestBody CrearSocioDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(socioService.crearSocio(dto));
    }

    // Endpoint 2: GET
    @GetMapping
    public ResponseEntity<Page<SocioResponseDto>> listarSocios(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(socioService.obtenerTodosPaginados(pageable));
    }

    // Endpoint 3: GET
    @GetMapping("/{id}")
    public ResponseEntity<SocioResponseDto> obtenerSocio(@PathVariable Integer id) {
        return ResponseEntity.ok(socioService.obtenerPorId(id));
    }

    // Endpoint 4: PUT
    @PutMapping("/{id}")
    public ResponseEntity<SocioResponseDto> actualizarSocio(@PathVariable Integer id, @Valid @RequestBody ActualizarSocioDto dto) {
        return ResponseEntity.ok(socioService.actualizarSocio(id, dto));
    }

    // Endpoint 5: PATCH
    @PatchMapping("/{id}/estado")
    public ResponseEntity<SocioResponseDto> cambiarEstado(@PathVariable Integer id, @RequestParam Boolean activo) {
        return ResponseEntity.ok(socioService.cambiarEstado(id, activo));
    }

    // Obtener datos del perfil
    @GetMapping("/mi-perfil")
    public ResponseEntity<MiPerfilSocioDto> obtenerMiPerfil(
            @AuthenticationPrincipal Jwt jwt) {

        String nombreUsuario = jwt.getSubject();

        return ResponseEntity.ok(
                socioService.obtenerMiPerfil(nombreUsuario)
        );
    }
    // Obtener resumen socio
    @GetMapping("/mi-resumen")
    public ResponseEntity<ResumenSocioDto> obtenerMiResumen(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(socioService.obtenerResumen(jwt.getSubject()));
    }

    // obtener reservas por id
    @PostMapping("/mis-reservas/{actividadId}")
    public ResponseEntity<ReservaSocioDto> reservarActividad(
            @PathVariable Integer actividadId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                socioService.reservarActividad(
                        jwt.getSubject(),
                        actividadId
                )
        );
    }
    // ontener reservas por socio
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaSocioDto>> obtenerMisReservas(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                socioService.obtenerMisReservas(jwt.getSubject())
        );
    }

    // obtener actividades disponibles por socio
    @GetMapping("/actividades-disponibles")
    public ResponseEntity<List<ActividadDisponibleSocioDto>>
    obtenerActividadesDisponibles() {

        return ResponseEntity.ok(
                socioService.obtenerActividadesDisponibles()
        );
    }
}
