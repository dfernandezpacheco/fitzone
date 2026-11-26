package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.response.CuotaAdminDto;
import es.safareyes.fitzone.dto.response.GeneracionCuotasResponseDto;
import es.safareyes.fitzone.exception.RecursoNoEncontradoException;
import es.safareyes.fitzone.modelos.Cuota;
import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.repositorios.ICuotaRepository;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuotaAdminService {

    private final ICuotaRepository cuotaRepository;
    private final ISocioRepository socioRepository;

    @Transactional(readOnly = true)
    public Page<CuotaAdminDto> listarCuotas(Pageable pageable) {
        return cuotaRepository.listarCuotasAdmin(pageable);
    }

    // Endpoint 7: Generación mensual de cuotas (RN-03, RN-04)
    @Transactional
    public GeneracionCuotasResponseDto generarCuotasMensuales(Integer mes, Integer anio) {
        int desactivados = socioRepository.desactivarSociosMorosos();

        List<Socio> activos = socioRepository.findByActivoTrue();
        int generadas = 0;

        for (Socio socio : activos) {
            if (socio.getPlan() != null && !cuotaRepository.existsBySocioIdAndMesAndAnio(socio.getId(), mes, anio)) {
                Cuota nueva = new Cuota();
                nueva.setSocio(socio);
                nueva.setMes(mes);
                nueva.setAnio(anio);
                Double precio = socio.getPlan().getPrecioMesual();
                nueva.setImporte(precio != null ? BigDecimal.valueOf(precio) : BigDecimal.ZERO);
                nueva.setEstado("PENDIENTE");
                cuotaRepository.save(nueva);
                generadas++;
            }
        }
        return new GeneracionCuotasResponseDto(mes, anio, generadas, desactivados);
    }

    // Endpoint 9: Historial paginado de cuotas de un socio
    @Transactional(readOnly = true)
    public Page<CuotaAdminDto> listarCuotasPorSocio(Integer socioId, Pageable pageable) {
        if (!socioRepository.existsById(socioId)) {
            throw new RecursoNoEncontradoException("No existe el socio con id " + socioId);
        }
        return cuotaRepository.findBySocioIdOrderByAnioDescMesDesc(socioId, pageable)
                .map(this::convertirDto);
    }

    // Endpoint 10: Socios morosos de un mes
    @Transactional(readOnly = true)
    public List<CuotaAdminDto> listarPendientes(Integer mes, Integer anio) {
        return cuotaRepository.findPendientesByMesAndAnio(mes, anio).stream()
                .map(this::convertirDto)
                .toList();
    }

    private CuotaAdminDto convertirDto(Cuota cuota) {
        Socio socio = cuota.getSocio();
        Plan plan = socio != null ? socio.getPlan() : null;

        return new CuotaAdminDto(
                cuota.getId(),
                cuota.getMes(),
                cuota.getAnio(),
                cuota.getImporte(),
                cuota.getEstado(),
                cuota.getFechaPago(),
                socio != null ? socio.getId() : null,
                socio != null ? socio.getNombre() : null,
                socio != null ? socio.getApellidos() : null,
                socio != null ? socio.getDni() : null,
                plan != null ? plan.getNombre() : null
        );
    }
}