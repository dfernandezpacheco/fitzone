package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

    @Entity
    @Table(schema = "fitzone", name = "actividad")
    @Getter @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "hora")
    private LocalTime hora;

    @Column(name = "duracion")
    private LocalTime duracion;

    @Column(name = "sala")
    private String sala;

    @Column(name = "aforo_maximo")
    private Integer aforoMaximo;

    @Column(name = "dia")
    private LocalDateTime dia;

    @ManyToOne
    @JoinColumn(name = "id_monitor")
    private Monitor monitor;

    @OneToMany(mappedBy = "actividad")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reserva> reservas;
}