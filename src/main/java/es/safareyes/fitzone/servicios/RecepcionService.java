package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.conversores.SocioMapper;
import es.safareyes.fitzone.dto.RecepcionResumenDto;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import es.safareyes.fitzone.repositorios.ICuotaRepository;
import es.safareyes.fitzone.repositorios.IActividadRepository;
import es.safareyes.fitzone.repositorios.IReservaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecepcionService {

    private final ISocioRepository socioRepository;
    private final ICuotaRepository cuotaRepository;
    private final IActividadRepository actividadRepository;
    private final IReservaRepository reservaRepository;
    private final SocioMapper socioMapper;

    @Transactional(readOnly = true)
    public RecepcionResumenDto obtenerResumen() {

        LocalDate hoy = LocalDate.now();

        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();

        long sociosActivos =
                socioRepository.countByActivoTrue();

        long pagosPendientes =
                cuotaRepository.countByEstadoIgnoreCase("PENDIENTE");

        long turnosHoy =
                actividadRepository
                        .countByDiaGreaterThanEqualAndDiaLessThan(
                                inicio, fin
                        );

        long reservasHoy =
                reservaRepository.contarReservasHoy(
                        inicio, fin
                );

        var sociosRecientes =
                socioMapper.toDtoList(
                        socioRepository.findTop5ByOrderByFechaAltaDesc()
                );

        return new RecepcionResumenDto(
                sociosActivos,
                pagosPendientes,
                turnosHoy,
                reservasHoy,
                sociosRecientes
        );

    }
}