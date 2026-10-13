package es.safareyes.fitzone.repositorios;


import es.safareyes.fitzone.modelos.Actividad;
import es.safareyes.fitzone.modelos.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IReservaRepository extends JpaRepository<Reserva, Integer> {

    @Query("SELECT a FROM Actividad a " +
            "WHERE (SELECT COUNT(r) FROM Reserva r WHERE r.actividad = a AND r.fechaReserva = :fecha) < a.aforoMaximo")
    List<Actividad> findActividadesConPlazasPorFecha(@Param("fecha") LocalDate fecha);
}
