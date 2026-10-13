package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(schema = "fitzone", name = "monitor")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "telefono")
    private Integer telefono;

    @OneToMany(mappedBy = "monitor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Actividad> actividades;
}