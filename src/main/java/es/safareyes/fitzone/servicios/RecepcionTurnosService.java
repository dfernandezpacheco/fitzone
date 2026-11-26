package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.request.CrearTurnoRecepcionDto;
import es.safareyes.fitzone.dto.response.MonitorRecepcionDto;
import es.safareyes.fitzone.dto.response.TurnoRecepcionDto;
import es.safareyes.fitzone.exception.RecursoNoEncontradoException;
import es.safareyes.fitzone.exception.ReglaNegocioException;
import es.safareyes.fitzone.modelos.Actividad;
import es.safareyes.fitzone.modelos.Monitor;
import es.safareyes.fitzone.modelos.Reserva;
import es.safareyes.fitzone.repositorios.IActividadRepository;
import es.safareyes.fitzone.repositorios.IMonitorRepository;
import lombok.RequiredArgsConstructor;
import es.safareyes.fitzone.dto.response.ReservaRecepcionDto;
import es.safareyes.fitzone.repositorios.IReservaRepository;
import es.safareyes.fitzone.modelos.Socio;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecepcionTurnosService {

    private final IActividadRepository actividadRepository;
    private final IMonitorRepository monitorRepository;
    private final IReservaRepository reservaRepository;

    @Transactional(readOnly = true)
    public List<TurnoRecepcionDto> listarTurnos(LocalDate fecha) {

        LocalDateTime inicio = fecha.atStartOfDay();

        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

        return actividadRepository.listarTurnosPorFecha(
                inicio,
                fin
        );
    }

    @Transactional(readOnly = true)
    public List<MonitorRecepcionDto> listarMonitores() {

        return monitorRepository.findAllByOrderByNombreAscApellidoAsc()
                .stream()
                .map(monitor -> new MonitorRecepcionDto(
                        monitor.getId(),
                        monitor.getNombre(),
                        monitor.getApellido(),
                        monitor.getTelefono()
                ))
                .toList();
    }

    @Transactional
    public TurnoRecepcionDto crearTurno(
            CrearTurnoRecepcionDto dto
    ) {

        if (dto.fecha().isBefore(LocalDate.now())) {
            throw new ReglaNegocioException(
                    "No puedes programar una actividad en una fecha pasada."
            );
        }

        if (dto.duracion().equals(java.time.LocalTime.MIDNIGHT)
                || dto.duracion().isBefore(java.time.LocalTime.of(0, 15))) {
            throw new ReglaNegocioException(
                    "La duración debe ser de al menos 15 minutos."
            );
        }

        LocalDateTime fechaHora = dto.fecha().atTime(dto.hora());

        String sala = dto.sala().trim();

        Monitor monitor = monitorRepository.findById(dto.monitorId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se ha encontrado el monitor seleccionado."
                        )
                );

// Evitar que una actividad termine al día siguiente.
        int inicioNuevo = dto.hora().toSecondOfDay();
        int finNuevo = inicioNuevo + dto.duracion().toSecondOfDay();

        if (finNuevo > 24 * 60 * 60) {
            throw new ReglaNegocioException(
                    "La actividad debe finalizar antes de medianoche."
            );
        }

// Obtener las actividades del mismo día.
        LocalDateTime inicioDia = dto.fecha().atStartOfDay();
        LocalDateTime finDia = dto.fecha().plusDays(1).atStartOfDay();

        List<Actividad> actividadesDelDia =
                actividadRepository.findByDiaGreaterThanEqualAndDiaLessThan(
                        inicioDia,
                        finDia
                );

// Comprobar conflictos de horario.
        for (Actividad existente : actividadesDelDia) {

            int inicioExistente = existente.getDia()
                    .toLocalTime()
                    .toSecondOfDay();

            int finExistente = inicioExistente
                    + existente.getDuracion().toSecondOfDay();

            boolean seSolapan =
                    inicioNuevo < finExistente
                            && finNuevo > inicioExistente;

            if (seSolapan) {

                boolean mismaSala = existente.getSala() != null
                        && existente.getSala().equalsIgnoreCase(sala);

                boolean mismoMonitor = existente.getMonitor() != null
                        && existente.getMonitor().getId().equals(monitor.getId());

                if (mismaSala) {
                    throw new ReglaNegocioException(
                            "La sala ya está ocupada durante ese horario."
                    );
                }

                if (mismoMonitor) {
                    throw new ReglaNegocioException(
                            "El monitor ya tiene otra actividad durante ese horario."
                    );
                }
            }
        }

        Actividad actividad = new Actividad();

        actividad.setNombre(dto.nombre().trim());
        actividad.setDia(fechaHora);
        actividad.setHora(dto.hora());
        actividad.setDuracion(dto.duracion());
        actividad.setSala(sala);
        actividad.setAforoMaximo(dto.aforoMaximo());
        actividad.setMonitor(monitor);

        Actividad guardada = actividadRepository.save(actividad);

        return new TurnoRecepcionDto(
                guardada.getId(),
                guardada.getNombre(),
                guardada.getDia(),
                guardada.getHora(),
                guardada.getDuracion(),
                guardada.getSala(),
                guardada.getAforoMaximo(),
                monitor.getId(),
                monitor.getNombre() + " " + monitor.getApellido(),
                0L
        );
    }
    @Transactional(readOnly = true)
    public List<ReservaRecepcionDto> listarReservasActividad(
            Integer actividadId
    ) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se ha encontrado la actividad."
                        )
                );

        List<Reserva> reservas =
                reservaRepository
                        .findByActividad_IdAndEstadoTrueOrderByFechaReservaAsc(
                                actividad.getId()
                        );

        return reservas.stream()
                .map(reserva -> {
                    Socio socio = reserva.getSocio();

                    return new ReservaRecepcionDto(
                            reserva.getId(),
                            socio.getId(),
                            socio.getNombre(),
                            socio.getApellidos(),
                            reserva.getFechaReserva(),
                            reserva.getEstado(),
                            reserva.getAsistio()
                    );
                })
                .toList();
    }


    @Transactional
    public void actualizarAsistencia(
            Integer actividadId,
            Integer reservaId,
            Boolean asistio
    ) {

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se ha encontrado la actividad."
                        )
                );

        Reserva reserva = reservaRepository
                .findByIdAndActividad_Id(reservaId, actividadId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se ha encontrado la reserva de esta actividad."
                        )
                );

        if (!Boolean.TRUE.equals(reserva.getEstado())) {
            throw new ReglaNegocioException(
                    "No se puede registrar asistencia para una reserva cancelada."
            );
        }

        reserva.setAsistio(asistio);

        reservaRepository.save(reserva);
    }

    @Transactional
    public void cancelarReserva(Integer actividadId, Integer reservaId) {

        Reserva reserva = reservaRepository
                .findByIdAndActividad_Id(reservaId, actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se ha encontrado la reserva para esta actividad."));

        if (!Boolean.TRUE.equals(reserva.getEstado())) {
            throw new ReglaNegocioException("La reserva ya está cancelada.");
        }

        reserva.setEstado(false);
        reservaRepository.save(reserva);
    }


}
