package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.modelos.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;

    public String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();

        // Información que guardaremos dentro del token
        JwtClaimsSet claims = JwtClaimsSet.builder()

                .issuer("fitzone")

                // Identificador del usuario
                .subject(usuario.getUsuario())

                // ID del usuario
                .claim("id", usuario.getId())

                // Rol del usuario
                .claim("rol", usuario.getRol())

                .issuedAt(ahora)

                // El token caducará en una hora
                .expiresAt(ahora.plus(1, ChronoUnit.HOURS))

                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}