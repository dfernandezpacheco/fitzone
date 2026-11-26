package es.safareyes.fitzone.controladores;

import es.safareyes.fitzone.servicios.RecepcionTurnosService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recepcion/reservas")
@RequiredArgsConstructor
public class RecepcionReservasController {

    private final RecepcionTurnosService recepcionTurnosService;

}