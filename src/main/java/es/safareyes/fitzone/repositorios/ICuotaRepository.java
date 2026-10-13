package es.safareyes.fitzone.repositorios;


import es.safareyes.fitzone.modelos.Cuota;
import es.safareyes.fitzone.modelos.Socio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICuotaRepository extends JpaRepository<Cuota, Integer> {

    Page<Cuota> findAllBySocioId(Integer socioId, Pageable pageable);

    List<Cuota> findByMesAndAnioAndEstado(Integer mes, Integer anio, String estado);

    @Query("SELECT c.mes, p.nombre, SUM(c.importe) " +
            "FROM Cuota c " +
            "JOIN c.socio s " +
            "JOIN s.plan p " +
            "WHERE c.anio = :anio " +
            "GROUP BY c.mes, p.nombre " +
            "ORDER BY c.mes ASC, p.nombre ASC")
        List<Object[]> findIngresosPorMesYPlan(@Param("anio") Integer anio);

}
