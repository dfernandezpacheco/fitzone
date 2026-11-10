package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.IniciarSesionDto;
import es.safareyes.fitzone.dto.RegistroDto;
import es.safareyes.fitzone.dto.SesionIniciadaDto;
import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.modelos.Usuario;
import es.safareyes.fitzone.repositorios.IPlanRepository;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import es.safareyes.fitzone.repositorios.IUsuarioRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AutenticacionService {

    private final IUsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;
    private final ISocioRepository socioRepository;
    private final IPlanRepository planRepository;

    // El volcado muestra el valor 3 en usuarios de prueba; contrastar el nombre en la tabla de roles.
    private static final int ROL_SOCIO = 3;

    @Transactional
    public SesionIniciadaDto registrar(RegistroDto dto) {
        String correo = dto.correo().trim().toLowerCase(Locale.ROOT);
        String dni = dto.dni().trim().toUpperCase();
        if (!dto.contrasena().equals(dto.confirmarContrasena())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden");
        }
        if (usuarioRepository.existsByUsuario(correo) || socioRepository.existsByEmailIgnoreCase(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo");
        }
        if (socioRepository.existsByDni(dni)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un socio con ese DNI");
        }

        Plan plan = planRepository.findById(dto.planId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "El plan seleccionado no existe"));
        String[] partesNombre = dto.nombreCompleto().trim().split("\\s+", 2);
        if (partesNombre.length < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Introduce nombre y apellidos");
        }

        Usuario usuario = usuarioRepository.save(new Usuario(null, correo,
                passwordEncoder.encode(dto.contrasena()), ROL_SOCIO));
        Socio socio = new Socio();
        socio.setDni(dni);
        socio.setNombre(partesNombre[0]);
        socio.setApellidos(partesNombre[1]);
        socio.setEmail(correo);
        socio.setTelefono(dto.telefono().trim());
        socio.setMatricula(dto.matricula() == null || dto.matricula().isBlank()
                ? null : dto.matricula().trim().toUpperCase(Locale.ROOT));
        socio.setFechaAlta(LocalDate.now());
        socio.setActivo(true);
        socio.setUsuario(usuario);
        socio.setPlan(plan);
        socioRepository.save(socio);
        return new SesionIniciadaDto(usuario.getId(), usuario.getUsuario(), usuario.getRol(), tokenService.generarToken(usuario));
    }


    @Transactional
    public SesionIniciadaDto iniciarSesion(IniciarSesionDto dto) {
        Usuario usuario = usuarioRepository
                .findByUsuario(dto.usuario().trim())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Credenciales incorrectas"
                        )
                );
        String passwordGuardada = usuario.getPassword();
        boolean estaCifrada = passwordGuardada != null &&
                (passwordGuardada.startsWith("$2a$") ||
                        passwordGuardada.startsWith("$2b$") ||
                        passwordGuardada.startsWith("$2y$"));

        boolean contrasenaCorrecta;
        if (estaCifrada) {
            contrasenaCorrecta = passwordEncoder.matches(
                    dto.clave(),
                    passwordGuardada
            );
        } else {

            contrasenaCorrecta = passwordGuardada != null &&
                    passwordGuardada.equals(dto.clave());
        }
        if (!contrasenaCorrecta) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas"
            );
        }
        if (!estaCifrada) {
            String nuevaPassword = passwordEncoder.encode(
                    dto.clave()
            );
            usuario.setPassword(nuevaPassword);
            usuarioRepository.save(usuario);
        }
        String token = tokenService.generarToken(usuario);
        return new SesionIniciadaDto(
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getRol(),
                token
        );
    }
}
