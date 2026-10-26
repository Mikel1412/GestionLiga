package es.esports.excepciones.reglaNegocioException;

public class JugadorIncompatibleException extends RuntimeException {
    public JugadorIncompatibleException(String message) {
        super(message);
    }
}
