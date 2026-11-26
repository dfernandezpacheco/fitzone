package es.safareyes.fitzone.repositorios;


import es.safareyes.fitzone.modelos.Cuota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import es.safareyes.fitzone.dto.response.CuotaAdminDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICuotaRepository extends JpaRepository<Cuota, Integer> {

    Page<Cuota> findAllBySocioId(Integer socioId, Pageable pageable);

    List<Cuota> findByMesAndAnioAndEstado(Integer mes, Integer anio, String estado);

    Optional<Cuota> findFirstBySocioIdAndMesAndAnioOrderByIdDesc(Integer socioId, Integer mes, Integer anio);

    @Query("SELECT c.mes, p.nombre, SUM(c.importe) " +
            "FROM Cuota c " +
            "JOIN c.socio s " +
            "JOIN s.plan p " +
            "WHERE c.anio = :anio " +
            "GROUP BY c.mes, p.nombre " +
            "ORDER BY c.mes ASC, p.nombre ASC")
        List<Object[]> findIngresosPorMesYPlan(@Param("anio") Integer anio);

    @Query("""
    SELECT COALESCE(SUM(c.importe), 0)
    FROM Cuota c
    WHERE c.fechaPago >= :inicio
      AND c.fechaPago < :fin
      AND UPPER(c.estado) IN ('PAGADA', 'PAGADO')
    """)
    BigDecimal calcularIngresosEntreFechas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    long countByEstadoIgnoreCase(String estado);

    @Query(
            value = """
        SELECT new es.safareyes.fitzone.dto.CuotaAdminDto(
            c.id,c.mes,c.anio,c.importe,c.estado,c.fechaPago,s.id,s.nombre,s.apellidos,s.dni,p.nombre
        )
        FROM Cuota c
        LEFT JOIN c.socio s
        LEFT JOIN s.plan p
        ORDER BY c.anio DESC, c.mes DESC, c.id DESC
        """,
            countQuery = "SELECT COUNT(c) FROM Cuota c"
    )
    Page<CuotaAdminDto> listarCuotasAdmin(Pageable pageable);

    long countByAnio(Integer anio);

    long countByAnioAndEstadoIgnoreCase(Integer anio, String estado);

    // Comprueba si el socio tiene pagada la cuota del mes/año (para el torno RN-05)
    @Query("SELECT COUNT(c) > 0 FROM Cuota c WHERE c.socio.id = :socioId " +
            "AND c.mes = :mes AND c.anio = :anio AND UPPER(c.estado) IN ('PAGADO', 'PAGADA')")
    boolean tieneCuotaPagada(@Param("socioId") Integer socioId, @Param("mes") Integer mes, @Param("anio") Integer anio);

    // Evita generar cuotas duplicadas (RN-03)
    boolean existsBySocioIdAndMesAndAnio(Integer socioId, Integer mes, Integer anio);

    // Endpoint 9: Historial paginado de cuotas de un socio
    Page<Cuota> findBySocioIdOrderByAnioDescMesDesc(Integer socioId, Pageable pageable);

    // Endpoint 10: Morosos de un mes (Q2 JPQL con JOIN)
    @Query("SELECT c FROM Cuota c JOIN FETCH c.socio s LEFT JOIN FETCH s.plan " +
            "WHERE c.mes = :mes AND c.anio = :anio AND UPPER(c.estado) = 'PENDIENTE'")
    List<Cuota> findPendientesByMesAndAnio(@Param("mes") Integer mes, @Param("anio") Integer anio);
}
