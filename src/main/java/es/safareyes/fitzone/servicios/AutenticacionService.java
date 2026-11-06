package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.IniciarSesionDto;
import es.safareyes.fitzone.dto.SesionIniciadaDto;
import es.safareyes.fitzone.modelos.Usuario;
import es.safareyes.fitzone.repositorios.IUsuarioRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AutenticacionService {

    private final IUsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;


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