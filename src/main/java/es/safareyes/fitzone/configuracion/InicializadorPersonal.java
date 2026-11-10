package es.safareyes.fitzone.configuracion;

import es.safareyes.fitzone.modelos.Usuario;
import es.safareyes.fitzone.repositorios.IUsuarioRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
@ConditionalOnProperty(
        prefix = "fitzone.personal.bootstrap",
        name = "enabled",
        havingValue = "true"
)
public class InicializadorPersonal implements ApplicationRunner {

    private static final int ROL_ADMIN = 1;
    private static final int ROL_RECEPCION = 2;

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private final String adminUsuario;
    private final String adminPassword;

    private final String recepcionUsuario;
    private final String recepcionPassword;


    public InicializadorPersonal(
            IUsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,

            @Value("${fitzone.personal.admin.usuario}")
            String adminUsuario,

            @Value("${fitzone.personal.admin.password}")
            String adminPassword,

            @Value("${fitzone.personal.recepcion.usuario}")
            String recepcionUsuario,

            @Value("${fitzone.personal.recepcion.password}")
            String recepcionPassword
    ) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;

        this.adminUsuario = adminUsuario;
        this.adminPassword = adminPassword;

        this.recepcionUsuario = recepcionUsuario;
        this.recepcionPassword = recepcionPassword;
    }


    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (adminUsuario.equalsIgnoreCase(recepcionUsuario)) {
            throw new IllegalStateException(
                    "El administrador y recepción deben tener usuarios diferentes"
            );
        }

        crearCuenta(
                adminUsuario,
                adminPassword,
                ROL_ADMIN
        );

        crearCuenta(
                recepcionUsuario,
                recepcionPassword,
                ROL_RECEPCION
        );
    }


    private void crearCuenta(
            String nombreUsuario,
            String password,
            int rol
    ) {

        if (nombreUsuario == null ||
                !nombreUsuario.matches("[a-zA-Z0-9._-]{4,40}")) {

            throw new IllegalArgumentException(
                    "Nombre de usuario no válido"
            );
        }

        if (password == null ||
                password.length() < 12 ||
                password.getBytes(StandardCharsets.UTF_8).length > 72) {

            throw new IllegalArgumentException(
                    "La contraseña debe tener entre 12 y 72 bytes UTF-8"
            );
        }

        var existente = usuarioRepository.findByUsuario(nombreUsuario);

        if (existente.isPresent()) {

            if (!existente.get().getRol().equals(rol)) {
                throw new IllegalStateException(
                        "El usuario " + nombreUsuario +
                                " ya existe con otro rol. No se ha modificado."
                );
            }

            System.out.println(
                    "La cuenta " + nombreUsuario + " ya existe."
            );

            return;
        }

        Usuario usuario = new Usuario();

        usuario.setUsuario(nombreUsuario);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);

        usuarioRepository.save(usuario);

        System.out.println(
                "Cuenta creada correctamente: " +
                        nombreUsuario + " | Rol: " + rol
        );
    }
}