package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(schema = "fitzone", name = "acceso")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "fecha_hora_entrada")
    private LocalDateTime fechaHoraEntrada;

    @Column(name = "hora_salida")
    private LocalDateTime HoraSalida;

    @Column(name = "tipo_resultado")
    private String tipoResultado;

    @Column(name = "motivo_denegacion")
    private Integer motivoDenegacion;

    @Column(name = "ticket parking")
    private Boolean ticketParking;

    @ManyToOne
    @JoinColumn(name = "id_socio")
    private Socio socio;
}
