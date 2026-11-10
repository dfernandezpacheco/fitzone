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
import es.safareyes.fitzone.dto.ActividadResumenDto;
import es.safareyes.fitzone.dto.ResumenSocioDto;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.dto.ReservaSocioDto;
import es.safareyes.fitzone.modelos.Reserva;
import es.safareyes.fitzone.modelos.Actividad;
import es.safareyes.fitzone.modelos.Cuota;
import es.safareyes.fitzone.modelos.Monitor;
import es.safareyes.fitzone.repositorios.IReservaRepository;
import es.safareyes.fitzone.repositorios.ICuotaRepository;

import java.util.List;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocioService {

    private final ISocioRepository socioRepository;
    private final IPlanRepository planRepository;
    private final SocioMapper socioMapper;
    private final IReservaRepository reservaRepository;
    private final ICuotaRepository cuotaRepository;

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
    public ResumenSocioDto obtenerResumen(String nombreUsuario) {

        // 1. Buscar al socio autenticado
        Socio socio = socioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe un socio asociado a este usuario"
                        )
                );

        // 2. Obtener fecha y mes actuales
        LocalDateTime ahora = LocalDateTime.now();

        YearMonth mesActual = YearMonth.now();

        // 3. Recuperar todas las reservas del socio
        List<Reserva> reservas = reservaRepository
                .findReservasBySocioId(socio.getId());

        // 4. Filtrar las reservas activas
        // Consideramos true = reserva vigente
        List<Reserva> reservasActivas = reservas.stream()
                .filter(reserva -> Boolean.TRUE.equals(reserva.getEstado()))
                .toList();

        // 5. Obtener reservas futuras
        List<Reserva> futuras = reservasActivas.stream()

                .filter(reserva ->
                        reserva.getActividad() != null
                )

                .filter(reserva ->
                        reserva.getActividad().getDia() != null
                )

                .filter(reserva ->
                        !reserva.getActividad().getDia().isBefore(ahora)
                )

                .sorted(
                        Comparator.comparing(
                                reserva -> reserva.getActividad().getDia()
                        )
                )

                .toList();


        // 6. Contar las clases del mes actual
        int clasesEsteMes = (int) reservasActivas.stream()

                .filter(reserva ->
                        reserva.getActividad() != null
                )

                .filter(reserva ->
                        reserva.getActividad().getDia() != null
                )

                .filter(reserva ->
                        YearMonth.from(reserva.getActividad().getDia())
                                .equals(mesActual)
                )

                .count();


        // 7. Obtener las actividades recientes
        List<ActividadResumenDto> recientes = reservasActivas.stream()

                .filter(reserva ->
                        reserva.getActividad() != null
                )

                .filter(reserva ->
                        reserva.getActividad().getDia() != null
                )

                .filter(reserva ->
                        reserva.getActividad().getDia().isBefore(ahora)
                )

                .sorted(
                        Comparator.comparing(
                                (Reserva reserva) ->
                                        reserva.getActividad().getDia()
                        ).reversed()
                )

                .limit(3)

                .map(this::aActividadResumen)

                .toList();


        // 8. Consultar cuota del mes actual
        Optional<Cuota> cuota =
                cuotaRepository.findFirstBySocioIdAndMesAndAnioOrderByIdDesc(
                        socio.getId(),
                        mesActual.getMonthValue(),
                        mesActual.getYear()
                );


        // 9. Comprobar estado de la cuota
        String estadoCuota = cuota
                .map(c ->
                        esCuotaPagada(c.getEstado())
                                ? "Al día"
                                : "Pendiente"
                )
                .orElse("Sin cuota generada");


        // 10. Construir respuesta
        return new ResumenSocioDto(

                clasesEsteMes,

                futuras.size(),

                estadoCuota,

                futuras.isEmpty()
                        ? null
                        : aActividadResumen(futuras.get(0)),

                recientes

        );

    }

    private ActividadResumenDto aActividadResumen(Reserva reserva) {
        Actividad actividad = reserva.getActividad();
        Monitor monitor = actividad.getMonitor();
        String nombreMonitor = monitor == null ? null : monitor.getNombre()
                + (monitor.getApellido() == null ? "" : " " + monitor.getApellido());
        return new ActividadResumenDto(actividad.getNombre(), actividad.getDia(), actividad.getHora(),
                actividad.getSala(), nombreMonitor);
    }

    private boolean esCuotaPagada(String estado) {
        return "PAGADA".equalsIgnoreCase(estado) || "PAGADO".equalsIgnoreCase(estado);
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
