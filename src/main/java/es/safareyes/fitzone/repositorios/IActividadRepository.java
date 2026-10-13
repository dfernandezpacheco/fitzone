package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IActividadRepository extends JpaRepository<Actividad, Integer> {

    @Query("SELECT a FROM Actividad a " +
            "WHERE (SELECT COUNT(r) FROM Reserva r WHERE r.actividad = a AND r.fechaReserva BETWEEN :inicioDay AND :finDay) < a.aforoMaximo")
    List<Actividad> findActividadesConPlazasLibres(@Param("inicioDay") LocalDateTime inicioDay,
                                                   @Param("finDay") LocalDateTime finDay);
}
