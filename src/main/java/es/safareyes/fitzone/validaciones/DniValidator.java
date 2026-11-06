package es.safareyes.fitzone.validaciones;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DniValidator implements ConstraintValidator<ValidDni, String> {

    private static final String LETRAS_DNI = "TRWAGMYFPDXBNJZSQVHLCKE";

    @Override
    public boolean isValid(String dni, ConstraintValidatorContext context) {
        if (dni == null || !dni.matches("^[0-9]{8}[A-Z]$")) {
            return false;
        }

        int numero = Integer.parseInt(dni.substring(0, 8));
        char letraCalculada = LETRAS_DNI.charAt(numero % 23);
        char letraIngresada = dni.charAt(8);

        return letraCalculada == letraIngresada;
    }
}