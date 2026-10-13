package es.safareyes.fitzone;

import es.safareyes.fitzone.modelos.*;
import es.safareyes.fitzone.repositorios.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestRepositorio {


    @Autowired
    private IPlanRepository planRepository;

    @Autowired
    private ISocioRepository socioRepository;

    @Autowired
    private ICuotaRepository cuotaRepository;

    @Autowired
    private IAccesoRepository accesoRepository;

    @Autowired
    private IActividadRepository actividadRepository;


// Get 3
    @Test
    public void socioPlan() {
        Optional<Socio> socios = socioRepository.findById(5);
        socios.ifPresent(s -> System.out.println(s.getPlan()));

    }
// Get 6
    @Test
    public void mostrarPlanPrecio(){
        List<Plan> planes =planRepository.findAll();
        planes.forEach(p -> System.out.println(p.getNombre() + p.getPrecioMesual()));
    }
// Get 2
    @Test
    public void mostrarPageSocio(){
        Page<Socio> socio = socioRepository.findAll(PageRequest.of(0,10));
        socio.forEach(s -> System.out.println(s.getNombre()));
    }

// Get 9
    @Test
    public void mostrarPageCuota(){

        Page<Cuota> socio = cuotaRepository.findAllBySocioId(3,PageRequest.of(0,10));
        socio.forEach(s -> System.out.printf("Importe: %.2f € - Fecha: %s%n", s.getImporte(), s.getFechaPago()));
    }

// Get 13
    @Test
    public void mostrarAforo(){
        long personasDentro = accesoRepository.cuentaPersonasActivas();
        int aforoMaximo = 150;
        System.out.println("Personas dentro: " + personasDentro + " | Aforo Maximo: " + aforoMaximo);
    }

// Get 10
    @Test
    public void mostrarMorosos(){
        int mes = 5;
        int anio = 2025;

        List<Cuota> cuotasPendientes = cuotaRepository.findByMesAndAnioAndEstado(mes, anio, "PENDIENTE");

        cuotasPendientes.forEach(c -> System.out.printf(
                "Socio: %s %s - Estado: %s - Importe: %.2f €%n",
                c.getSocio().getNombre(),
                c.getSocio().getApellidos(),
                c.getEstado(),
                c.getImporte()
        ));
    }

// Get 14
    @Test
    public void mostrarActividadesLibres() {
        LocalDate fecha = LocalDate.of(2024, 10, 15);
        LocalDateTime inicioDay = fecha.atStartOfDay();
        LocalDateTime finDay = fecha.atTime(LocalTime.MAX);

        List<Actividad> actividades = actividadRepository.findActividadesConPlazasLibres(inicioDay, finDay);

        actividades.forEach(a -> System.out.printf(
                "Actividad: %s | Hora: %s | Sala: %s | Aforo Máx: %d%n",
                a.getNombre(),
                a.getHora(),
                a.getSala(),
                a.getAforoMaximo()
        ));
    }

// Get 16
    @Test
    public void mostrarIngresosPorMesYPlan() {
        int anioBusqueda = 2024;

        List<Object[]> resultados = cuotaRepository.findIngresosPorMesYPlan(anioBusqueda);

        resultados.forEach(fila -> {
            Integer mes = (Integer) fila[0];
            String nombrePlan = (String) fila[1];

            Double totalIngresos = (fila[2] != null) ? ((Number) fila[2]).doubleValue() : 0.0;

            System.out.printf("Mes: %d | Plan: %s | Total Ingresos: %.2f €%n",
                    mes, nombrePlan, totalIngresos);
        });
    }

// Consulta Extra
    @Test
    public void testFindSociosByName() {
        List<Socio> socios = socioRepository.findByFechaAltaAndActivoAndMatricula(LocalDate.of(2022, 06, 18), true, "7396IZZ");
        socios.forEach(s -> {
            System.out.println(s.getNombre());
        });
    }

}
