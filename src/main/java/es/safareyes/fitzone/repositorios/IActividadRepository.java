package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import es.safareyes.fitzone.dto.TurnoRecepcionDto;
import java.time.LocalDateTime;
import java.util.Optional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IActividadRepository extends JpaRepository<Actividad, Integer> {

    @Query("SELECT a FROM Actividad a " +
            "WHERE (SELECT COUNT(r) FROM Reserva r WHERE r.actividad = a AND r.fechaReserva BETWEEN :inicioDay AND :finDay) < a.aforoMaximo")
    List<Actividad> findActividadesConPlazasLibres(@Param("inicioDay") LocalDateTime inicioDay,
                                                   @Param("finDay") LocalDateTime finDay);

    long countByDiaGreaterThanEqualAndDiaLessThan(
            LocalDateTime inicio,
            LocalDateTime fin
    );

    @Query("""
    SELECT new es.safareyes.fitzone.dto.TurnoRecepcionDto(a.id,a.nombre,a.dia,a.hora,a.duracion,a.sala,a.aforoMaximo,m.id, CONCAT(m.nombre, ' ', m.apellido),
        (            SELECT COUNT(r)
            FROM Reserva r
            WHERE r.actividad = a
              AND r.estado = true
        )
    )
    FROM Actividad a
    LEFT JOIN a.monitor m
    WHERE a.dia >= :inicio
      AND a.dia < :fin
    ORDER BY a.dia ASC, a.hora ASC
    """)
    List<TurnoRecepcionDto> listarTurnosPorFecha(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    boolean existsByDiaAndSalaIgnoreCase(
            LocalDateTime dia,
            String sala
    );

    boolean existsByDiaAndMonitor_Id(
            LocalDateTime dia,
            Integer monitorId
    );
}
