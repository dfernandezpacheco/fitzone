package es.safareyes.fitzone.repositorios;

import es.safareyes.fitzone.modelos.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAccesoRepository extends JpaRepository<Acceso, Integer> {

    List<Acceso> findByTipoResultadoEquals(String nombre);

    @Query("SELECT COUNT(a) FROM Acceso a WHERE a.HoraSalida IS NULL")
    long cuentaPersonasActivas();

    // Comprueba si el socio tiene una entrada permitida hoy sin salida registrada (RN-05, RN-07)
    @Query("SELECT COUNT(a) > 0 FROM Acceso a WHERE a.socio.id = :socioId " +
            "AND a.tipoResultado = 'PERMITIDO' AND a.HoraSalida IS NULL " +
            "AND a.fechaHoraEntrada >= :inicioDia")
    boolean tieneEntradaAbiertaHoy(@Param("socioId") Integer socioId, @Param("inicioDia") LocalDateTime inicioDia);

    // Obtiene la entrada abierta de hoy para registrar la salida (RN-07)
    @Query("SELECT a FROM Acceso a WHERE a.socio.id = :socioId " +
            "AND a.tipoResultado = 'PERMITIDO' AND a.HoraSalida IS NULL " +
            "AND a.fechaHoraEntrada >= :inicioDia")
    Optional<Acceso> buscarEntradaAbiertaHoy(@Param("socioId") Integer socioId, @Param("inicioDia") LocalDateTime inicioDia);

    // Aforo actual: entradas permitidas del día sin salida registrada (Q2 - RN-07)
    @Query("SELECT COUNT(a) FROM Acceso a WHERE a.tipoResultado = 'PERMITIDO' " +
            "AND a.HoraSalida IS NULL AND a.fechaHoraEntrada >= :inicioDia")
    long contarPersonasDentro(@Param("inicioDia") LocalDateTime inicioDia);
}