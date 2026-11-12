package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.InformeAdminDto;
import es.safareyes.fitzone.dto.IngresoMensualDto;
import es.safareyes.fitzone.repositorios.ICuotaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InformeAdminService {

    private final ICuotaRepository cuotaRepository;

    @Transactional(readOnly = true)
    public InformeAdminDto generarInforme(Integer anio) {

        // INGRESOS MENSUALES

        List<IngresoMensualDto> ingresosMensuales =
                new ArrayList<>();

        BigDecimal ingresosAnio = BigDecimal.ZERO;

        for (int mes = 1; mes <= 12; mes++) {

            YearMonth periodo = YearMonth.of(anio, mes);

            LocalDateTime inicio = periodo
                    .atDay(1)
                    .atStartOfDay();

            LocalDateTime fin = periodo
                    .plusMonths(1)
                    .atDay(1)
                    .atStartOfDay();

            BigDecimal ingresos =
                    cuotaRepository.calcularIngresosEntreFechas(
                            inicio,
                            fin
                    );
            if (ingresos == null) {
                ingresos = BigDecimal.ZERO;
            }
            ingresosMensuales.add(
                    new IngresoMensualDto(mes, ingresos)
            );
            ingresosAnio = ingresosAnio.add(ingresos);
        }
        long cuotasPagadas =
                cuotaRepository.countByAnioAndEstadoIgnoreCase(anio, "PAGADA")
                        + cuotaRepository.countByAnioAndEstadoIgnoreCase(anio, "PAGADO");

        long cuotasPendientes =
                cuotaRepository.countByAnioAndEstadoIgnoreCase(
                        anio,
                        "PENDIENTE"
                );
        long cuotasCanceladas =
                cuotaRepository.countByAnioAndEstadoIgnoreCase(
                        anio,
                        "CANCELADO"
                );
        long totalCuotas =
                cuotaRepository.countByAnio(anio);

        return new InformeAdminDto(
                anio,
                ingresosAnio,
                cuotasPagadas,
                cuotasPendientes,
                cuotasCanceladas,
                totalCuotas,
                ingresosMensuales
        );
    }
}