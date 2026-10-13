package es.safareyes.fitzone.repositorios;


import es.safareyes.fitzone.modelos.Socio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISocioRepository extends JpaRepository<Socio, Integer> {

    List<Socio> findByFechaAltaAndActivoAndMatricula(LocalDate fechaAlta, Boolean activo, String matricula);

    Optional<Socio> findById(Integer id);

    Page<Socio> findAll(Pageable pageable);

}
