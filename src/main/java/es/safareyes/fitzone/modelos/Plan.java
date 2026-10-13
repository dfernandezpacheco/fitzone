package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(schema = "fitzone", name = "plan")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "precio_mesual")
    private Double precioMesual;

    @Column(name = "tipo")
    @Enumerated(EnumType.ORDINAL)
    private TipoPlan tipo;
}
