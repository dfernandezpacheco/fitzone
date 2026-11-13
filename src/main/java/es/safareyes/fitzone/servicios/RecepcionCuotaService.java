package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.CuotaAdminDto;
import es.safareyes.fitzone.dto.ResumenCuotasRecepcionDto;
import es.safareyes.fitzone.modelos.Cuota;
import es.safareyes.fitzone.modelos.Socio;
import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.repositorios.ICuotaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecepcionCuotaService {

    private final ICuotaRepository cuotaRepository;

    @Transactional(readOnly = true)
    public Page<CuotaAdminDto> listarCuotas(Pageable pageable) {

        return cuotaRepository.listarCuotasAdmin(pageable);
    }

    @Transactional(readOnly = true)
    public ResumenCuotasRecepcionDto obtenerResumen() {

        return new ResumenCuotasRecepcionDto(
                cuotaRepository.count(),
                cuotaRepository.countByEstadoIgnoreCase("PAGADO"),
                cuotaRepository.countByEstadoIgnoreCase("PENDIENTE"),
                cuotaRepository.countByEstadoIgnoreCase("CANCELADO")
        );
    }

    @Transactional
    public CuotaAdminDto registrarPago(Integer id) {

        Cuota cuota = cuotaRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No se ha encontrado la cuota."
                        )
                );

        String estado = cuota.getEstado().trim().toUpperCase();

        if ("PAGADO".equals(estado)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta cuota ya está pagada."
            );
        }

        if ("CANCELADO".equals(estado)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede cobrar una cuota cancelada."
            );
        }

        if (!"PENDIENTE".equals(estado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado de la cuota no permite registrar el cobro."
            );
        }

        cuota.setEstado("PAGADO");
        cuota.setFechaPago(LocalDateTime.now());

        Cuota guardada = cuotaRepository.save(cuota);

        return convertirDto(guardada);
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