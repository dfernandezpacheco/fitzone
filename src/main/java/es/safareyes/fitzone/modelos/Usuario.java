package es.safareyes.fitzone.modelos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "fitzone", name = "usuario")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "password")
    private String password;

    @Column(name = "rol")
    private Integer rol;
}
