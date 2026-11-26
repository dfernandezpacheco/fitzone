package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.response.AccesoResponseDto;
import es.safareyes.fitzone.dto.response.AforoResponseDto;
import es.safareyes.fitzone.exception.RecursoNoEncontradoException;
import es.safareyes.fitzone.exception.ReglaNegocioException;
import es.safareyes.fitzone.modelos.Acceso;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.repositorios.IAccesoRepository;
import es.safareyes.fitzone.repositorios.ICuotaRepository;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccesoService {

    private static final int AFORO_MAXIMO = 150;

    private final IAccesoRepository accesoRepository;
    private final ISocioRepository socioRepository;
    private final ICuotaRepository cuotaRepository;

    @Transactional
    public AccesoResponseDto registrarEntrada(String dni) {
        // RN-06: Un DNI inexistente devuelve 404 y no se registra
        Socio socio = socioRepository.findByDni(dni.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún socio con DNI " + dni));

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();

        Integer codigoMotivo = null;
        String textoMotivo = null;

        // Reglas RN-05
        if (!Boolean.TRUE.equals(socio.getActivo())) {
            codigoMotivo = 1;
            textoMotivo = "INACTIVO";
        } else if (!cuotaRepository.tieneCuotaPagada(socio.getId(), ahora.getMonthValue(), ahora.getYear())) {
            codigoMotivo = 2;
            textoMotivo = "CUOTA_PENDIENTE";
        } else if (accesoRepository.tieneEntradaAbiertaHoy(socio.getId(), inicioDia)) {
            codigoMotivo = 3;
            textoMotivo = "YA_DENTRO";
        } else if (accesoRepository.contarPersonasDentro(inicioDia) >= AFORO_MAXIMO) {
            codigoMotivo = 4;
            textoMotivo = "AFORO_COMPLETO";
        }

        Acceso acceso = new Acceso();
        acceso.setSocio(socio);
        acceso.setFechaHoraEntrada(ahora);

        if (codigoMotivo == null) {
            acceso.setTipoResultado("PERMITIDO");
            acceso.setTicketParking(socio.getMatricula() != null && !socio.getMatricula().isBlank());
            log.info("Entrada PERMITIDA para el socio {}", socio.getDni());
        } else {
            acceso.setTipoResultado("DENEGADO");
            acceso.setMotivoDenegacion(codigoMotivo);
            acceso.setTicketParking(false);
            log.warn("Entrada DENEGADA para el socio {}. Motivo: {}", socio.getDni(), textoMotivo);
        }

        Acceso guardado = accesoRepository.save(acceso);

        return new AccesoResponseDto(
                guardado.getId(),
                socio.getDni(),
                socio.getNombre() + " " + socio.getApellidos(),
                guardado.getTipoResultado(),
                guardado.getMotivoDenegacion(),
                textoMotivo,
                guardado.getFechaHoraEntrada()
        );
    }

    @Transactional
    public AccesoResponseDto registrarSalida(String dni) {
        Socio socio = socioRepository.findByDni(dni.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún socio con DNI " + dni));

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();

        // RN-07: Una salida sin entrada abierta devuelve 422
        Acceso accesoAbierto = accesoRepository.buscarEntradaAbiertaHoy(socio.getId(), inicioDia)
                .orElseThrow(() -> new ReglaNegocioException("El socio no tiene ninguna entrada abierta hoy"));

        accesoAbierto.setHoraSalida(LocalDateTime.now());
        Acceso actualizado = accesoRepository.save(accesoAbierto);
        log.info("Salida registrada para el socio {}", socio.getDni());

        return new AccesoResponseDto(
                actualizado.getId(),
                socio.getDni(),
                socio.getNombre() + " " + socio.getApellidos(),
                actualizado.getTipoResultado(),
                null,
                null,
                actualizado.getHoraSalida()
        );
    }

    @Transactional(readOnly = true)
    public AforoResponseDto consultarAforo() {
        long dentro = accesoRepository.contarPersonasDentro(LocalDate.now().atStartOfDay());
        return new AforoResponseDto(dentro, AFORO_MAXIMO);
    }
}