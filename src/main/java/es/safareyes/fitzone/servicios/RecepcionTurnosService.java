package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.CrearTurnoRecepcionDto;
import es.safareyes.fitzone.dto.MonitorRecepcionDto;
import es.safareyes.fitzone.dto.TurnoRecepcionDto;
import es.safareyes.fitzone.exception.RecursoNoEncontradoException;
import es.safareyes.fitzone.exception.ReglaNegocioException;
import es.safareyes.fitzone.modelos.Actividad;
import es.safareyes.fitzone.modelos.Monitor;
import es.safareyes.fitzone.repositorios.IActividadRepository;
import es.safareyes.fitzone.repositorios.IMonitorRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecepcionTurnosService {

    private final IActividadRepository actividadRepository;
    private final IMonitorRepository monitorRepository;

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

        if (actividadRepository.existsByDiaAndSalaIgnoreCase(
                fechaHora,
                sala
        )) {
            throw new ReglaNegocioException(
                    "Ya existe una actividad programada en esa sala y hora."
            );
        }

        if (actividadRepository.existsByDiaAndMonitor_Id(
                fechaHora,
                dto.monitorId()
        )) {
            throw new ReglaNegocioException(
                    "Ese monitor ya tiene una actividad asignada a esa hora."
            );
        }

        Monitor monitor = monitorRepository.findById(dto.monitorId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se ha encontrado el monitor seleccionado."
                        )
                );

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
}