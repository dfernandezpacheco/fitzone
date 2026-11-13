package es.safareyes.fitzone.repositorios;


import es.safareyes.fitzone.modelos.Actividad;
import es.safareyes.fitzone.modelos.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IReservaRepository extends JpaRepository<Reserva, Integer> {

    @Query("SELECT a FROM Actividad a " +
            "WHERE (SELECT COUNT(r) FROM Reserva r WHERE r.actividad = a AND r.fechaReserva = :fecha) < a.aforoMaximo")
    List<Actividad> findActividadesConPlazasPorFecha(@Param("fecha") LocalDate fecha);

    @Query("""
    SELECT r
    FROM Reserva r
    JOIN FETCH r.actividad a
    LEFT JOIN FETCH a.monitor
    WHERE r.socio.id = :socioId
    ORDER BY a.dia ASC, a.hora ASC
""")
    List<Reserva> findReservasBySocioId(
            @Param("socioId") Integer socioId
    );

    @Query("""
    SELECT COUNT(r)
    FROM Reserva r
    WHERE r.actividad.dia >= :inicio
      AND r.actividad.dia < :fin
    """)
    long contarReservasHoy(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}
