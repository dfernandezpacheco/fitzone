package es.safareyes.fitzone.repositorios;
import es.safareyes.fitzone.modelos.Socio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import es.safareyes.fitzone.dto.DistribucionPlanDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISocioRepository extends JpaRepository<Socio, Integer> {

    List<Socio> findByFechaAltaAndActivoAndMatricula(LocalDate fechaAlta, Boolean activo, String matricula);

    Optional<Socio> findById(Integer id);

    Page<Socio> findAll(Pageable pageable);

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);


    @Query("SELECT s FROM Socio s " +
            "LEFT JOIN FETCH s.plan " +
            "WHERE s.usuario.usuario = :usuario")
    Optional<Socio> findByNombreUsuario(
            @Param("usuario") String usuario
    );

    long countByActivoTrue();

    long countByActivoFalse();

    @Query("""
    SELECT new es.safareyes.fitzone.dto.DistribucionPlanDto(
        COALESCE(p.nombre, 'Sin plan'),
        COUNT(s.id)
    )
    FROM Socio s
    LEFT JOIN s.plan p
    GROUP BY p.nombre
    ORDER BY p.nombre
    """)
    List<DistribucionPlanDto> obtenerDistribucionSociosPorPlan();

    long countByFechaAltaGreaterThanEqualAndFechaAltaLessThan(
            LocalDate inicio,
            LocalDate fin
    );
    List<Socio> findTop5ByOrderByFechaAltaDesc();
}
