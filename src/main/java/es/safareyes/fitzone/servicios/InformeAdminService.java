package es.safareyes.fitzone.servicios;

import es.safareyes.fitzone.dto.AltaMensualDto;
import es.safareyes.fitzone.dto.InformeAdminDto;
import es.safareyes.fitzone.dto.IngresoMensualDto;
import es.safareyes.fitzone.repositorios.ICuotaRepository;
import es.safareyes.fitzone.repositorios.ISocioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InformeAdminService {

    private final ICuotaRepository cuotaRepository;
    private final ISocioRepository socioRepository;

    @Transactional(readOnly = true)
    public InformeAdminDto generarInforme(Integer anio) {

        List<IngresoMensualDto> ingresosMensuales =
                new ArrayList<>();

        List<AltaMensualDto> altasMensuales =
                new ArrayList<>();

        BigDecimal ingresosAnio = BigDecimal.ZERO;
        long totalAltasAnio = 0;

        // RECORRER LOS DOCE MESES

        for (int mes = 1; mes <= 12; mes++) {

            YearMonth periodo = YearMonth.of(anio, mes);

            LocalDateTime inicio =
                    periodo.atDay(1).atStartOfDay();

            LocalDateTime fin =
                    periodo.plusMonths(1).atDay(1).atStartOfDay();

            // INGRESOS DEL MES

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

            // ALTAS DE SOCIOS DEL MES

            LocalDate inicioMes = periodo.atDay(1);

            LocalDate finMes =
                    periodo.plusMonths(1).atDay(1);

            long altas =
                    socioRepository
                            .countByFechaAltaGreaterThanEqualAndFechaAltaLessThan(
                                    inicioMes,
                                    finMes
                            );

            altasMensuales.add(
                    new AltaMensualDto(mes, altas)
            );

            totalAltasAnio += altas;
        }

        // ESTADOS DE LAS CUOTAS

        long cuotasPagadas =
                cuotaRepository.countByAnioAndEstadoIgnoreCase(
                        anio, "PAGADO"
                );

        long cuotasPendientes =
                cuotaRepository.countByAnioAndEstadoIgnoreCase(
                        anio, "PENDIENTE"
                );

        long cuotasCanceladas =
                cuotaRepository.countByAnioAndEstadoIgnoreCase(
                        anio, "CANCELADO"
                );

        long totalCuotas =
                cuotaRepository.countByAnio(anio);

        // DEVOLVER INFORME

        return new InformeAdminDto(
                anio,
                ingresosAnio,
                cuotasPagadas,
                cuotasPendientes,
                cuotasCanceladas,
                totalCuotas,
                ingresosMensuales,
                totalAltasAnio,
                altasMensuales
        );
    }
}