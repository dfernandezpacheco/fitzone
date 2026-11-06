package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.conversores.SocioMapper;
import es.safareyes.fitzone.dto.ActualizarSocioDto;
import es.safareyes.fitzone.dto.CrearSocioDto;
import es.safareyes.fitzone.dto.SocioResponseDto;
import es.safareyes.fitzone.exception.RecursoNoEncontradoException;
import es.safareyes.fitzone.exception.ReglaNegocioException;
import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.repositorios.IPlanRepository;
import es.safareyes.fitzone.repositorios.IReservaRepository;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.safareyes.fitzone.dto.MiPerfilSocioDto;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.dto.ReservaSocioDto;
import es.safareyes.fitzone.modelos.Reserva;
import es.safareyes.fitzone.modelos.Actividad;
import es.safareyes.fitzone.modelos.Monitor;
import es.safareyes.fitzone.repositorios.IReservaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocioService {

    private final ISocioRepository socioRepository;
    private final IPlanRepository planRepository;
    private final SocioMapper socioMapper;
    private final IReservaRepository reservaRepository;

    @Transactional
    public SocioResponseDto crearSocio(CrearSocioDto dto) {
        if (socioRepository.existsByDni(dto.dni())) {
            throw new ReglaNegocioException("Ya existe un socio registrado con el DNI: " + dto.dni());
        }
        if (socioRepository.existsByEmail(dto.email())) {
            throw new ReglaNegocioException("Ya existe un socio registrado con el Email: " + dto.email());
        }

        Plan plan = planRepository.findById(dto.planId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Plan no encontrado con ID: " + dto.planId()));

        Socio socio = socioMapper.toEntity(dto, plan);
        return socioMapper.toDto(socioRepository.save(socio));
    }

    @Transactional(readOnly = true)
    public Page<SocioResponseDto> obtenerTodosPaginados(Pageable pageable) {
        return socioRepository.findAll(pageable).map(socioMapper::toDto);
    }

    @Transactional(readOnly = true)
    public SocioResponseDto obtenerPorId(Integer id) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado con ID: " + id));
        return socioMapper.toDto(socio);
    }

    @Transactional
    public SocioResponseDto actualizarSocio(Integer id, ActualizarSocioDto dto) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado con ID: " + id));

        Plan plan = planRepository.findById(dto.planId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Plan no encontrado con ID: " + dto.planId()));

        socio.setNombre(dto.nombre());
        socio.setApellidos(dto.apellidos());
        socio.setEmail(dto.email());
        socio.setTelefono(dto.telefono());
        socio.setMatricula(dto.matricula());
        socio.setPlan(plan);

        return socioMapper.toDto(socioRepository.save(socio));
    }

    @Transactional
    public SocioResponseDto cambiarEstado(Integer id, Boolean activo) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado con ID: " + id));
        socio.setActivo(activo);
        return socioMapper.toDto(socioRepository.save(socio));
    }

    @Transactional(readOnly = true)
    public MiPerfilSocioDto obtenerMiPerfil(String nombreUsuario) {
        Socio socio = socioRepository
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe un socio asociado a este usuario"
                        )
                );
        Plan plan = socio.getPlan();
        return new MiPerfilSocioDto(
                socio.getId(),
                socio.getNombre(),
                socio.getApellidos(),
                nombreUsuario,
                socio.getEmail(),
                socio.getTelefono(),
                socio.getFechaAlta(),
                socio.getActivo(),
                plan != null ? plan.getId() : null,
                plan != null ? plan.getNombre() : null,
                plan != null ? plan.getPrecioMesual() : null
        );
    }

    @Transactional(readOnly = true)
    public List<ReservaSocioDto> obtenerMisReservas(String nombreUsuario) {

        Socio socio = socioRepository
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe un socio asociado a este usuario"
                        )
                );

        List<Reserva> reservas = reservaRepository
                .findReservasBySocioId(socio.getId());
        return reservas.stream().map(reserva -> {

            Actividad actividad = reserva.getActividad();

            Monitor monitor = actividad.getMonitor();

            String nombreMonitor = null;

            if (monitor != null) {

                nombreMonitor = monitor.getNombre();

                if (monitor.getApellido() != null) {
                    nombreMonitor += " " + monitor.getApellido();
                }
            }
            return new ReservaSocioDto(
                    reserva.getId(),
                    reserva.getEstado(),
                    reserva.getFechaReserva(),
                    actividad.getId(),
                    actividad.getNombre(),
                    actividad.getDia(),
                    actividad.getHora(),
                    actividad.getDuracion(),
                    actividad.getSala(),
                    nombreMonitor
            );
        }).collect(Collectors.toList());
    }
}