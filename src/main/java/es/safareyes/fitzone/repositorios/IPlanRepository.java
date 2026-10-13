package es.safareyes.fitzone.repositorios;


import es.safareyes.fitzone.modelos.Plan;
import es.safareyes.fitzone.modelos.TipoPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPlanRepository extends JpaRepository<Plan, Integer> {

    List<Plan> findByTipo(TipoPlan tipo);
    List<Plan> findAll ();
}
