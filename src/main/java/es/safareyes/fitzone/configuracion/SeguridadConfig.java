package es.safareyes.fitzone.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    // Codificador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Generador de tokens JWT
    @Bean
    public JwtEncoder jwtEncoder(
            @Value("${app.jwt.secret}") String secret) {

        SecretKey key = crearClave(secret);

        return NimbusJwtEncoder.withSecretKey(key)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    // Validador de tokens JWT
    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${app.jwt.secret}") String secret) {

        SecretKey key = crearClave(secret);

        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // Configuración principal de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        return http

                // Desactivamos CSRF porque trabajamos con una API REST
                // autenticada mediante tokens Bearer
                .csrf(AbstractHttpConfigurer::disable)

                // Configuramos CORS
                .cors(Customizer.withDefaults())

                // No utilizamos sesiones tradicionales
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // LOGIN Y REGISTRO PÚBLICOS
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/login",
                                "/auth/registro",
                                "/api/v1/auth/login",
                                "/api/v1/auth/registro"
                        ).permitAll()


                        // CONSULTA PÚBLICA DE PLANES
                        .requestMatchers(
                                HttpMethod.GET,
                                "/planes",
                                "/api/v1/planes"
                        ).permitAll()


                        // PETICIONES OPTIONS
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // ZONA PERSONAL DEL SOCIO
                        .requestMatchers(
                                "/socios/mi-perfil",
                                "/socios/mi-perfil/**",
                                "/socios/mi-resumen"
                        ).hasRole("SOCIO")


                        // ADMINISTRACIÓN
                        .requestMatchers(
                                "/admin/**"
                        ).hasRole("ADMIN")


                        // RECEPCIÓN
                        .requestMatchers(
                                "/recepcion/**"
                        ).hasAnyRole("ADMIN", "RECEPCION")


                        // GESTIÓN DE SOCIOS
                        .requestMatchers(
                                "/socios/**"
                        ).hasAnyRole("ADMIN", "RECEPCION")


                        // RESTO DE RUTAS AUTENTICADAS
                        .anyRequest().authenticated()

                )

                // Validación automática de JWT
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                )

                .build();
    }

    // Configuración CORS para el frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:4200}")
            String allowedOrigins) {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
                Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .toList()
        );

        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // Convierte la clave Base64 en una clave criptográfica
    private static SecretKey crearClave(String secret) {

        byte[] keyBytes = Base64.getDecoder().decode(secret);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "La clave JWT debe tener al menos 32 bytes"
            );
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Integer rol = jwt.getClaim("rol");

            if (rol == null) {
                return List.of();
            }

            String autoridad = switch (rol) {

                case 1 -> "ROLE_ADMIN";

                case 2 -> "ROLE_RECEPCION";

                case 3 -> "ROLE_SOCIO";

                default -> null;
            };

            if (autoridad == null) {
                return List.of();
            }

            return List.of(
                    new SimpleGrantedAuthority(autoridad)
            );

        });

        return converter;
    }
}
