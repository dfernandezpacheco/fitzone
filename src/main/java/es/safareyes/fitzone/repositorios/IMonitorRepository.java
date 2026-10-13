package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMonitorRepository extends JpaRepository<Monitor, Integer> {
}
