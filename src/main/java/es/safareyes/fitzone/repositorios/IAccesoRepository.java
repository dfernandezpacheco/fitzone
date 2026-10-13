package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface IAccesoRepository extends JpaRepository<Acceso, Integer> {

    List<Acceso> findByTipoResultadoEquals(String nombre);

    @Query("SELECT COUNT(a) FROM Acceso a WHERE a.HoraSalida IS NULL")
    long cuentaPersonasActivas();
}
