package es.safareyes.fitzone.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail datosNoValidos(MethodArgumentNotValidException exception) {
        String mensaje = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Revisa los datos enviados");
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, mensaje);
        problema.setTitle("Datos no válidos");
        return problema;
    }

    @ExceptionHandler(ResponseStatusException.class)
    ProblemDetail respuestaEstado(ResponseStatusException exception) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(exception.getStatusCode().value()), exception.getReason());
        problema.setTitle("No se pudo completar la operación");
        return problema;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail conflictoDatos() {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Ya existe un dato registrado con esos valores");
        problema.setTitle("Conflicto de datos");
        return problema;
    }
}
