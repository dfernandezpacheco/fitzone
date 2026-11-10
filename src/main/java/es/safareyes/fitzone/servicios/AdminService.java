package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.AdminResumenDto;
import es.safareyes.fitzone.repositorios.ICuotaRepository;
import es.safareyes.fitzone.repositorios.IPlanRepository;
import es.safareyes.fitzone.repositorios.ISocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ISocioRepository socioRepository;
    private final IPlanRepository planRepository;
    private final ICuotaRepository cuotaRepository;

    @Transactional(readOnly = true)
    public AdminResumenDto obtenerResumen() {
        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicio = mesActual
                .atDay(1)
                .atStartOfDay();
        LocalDateTime fin = mesActual
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();
        BigDecimal ingresosMes =
                cuotaRepository.calcularIngresosEntreFechas(inicio, fin);

        if (ingresosMes == null) {
            ingresosMes = BigDecimal.ZERO;
        }
        return new AdminResumenDto(
                socioRepository.count(),
                socioRepository.countByActivoTrue(),
                socioRepository.countByActivoFalse(),
                planRepository.count(),
                ingresosMes
        );
    }
}