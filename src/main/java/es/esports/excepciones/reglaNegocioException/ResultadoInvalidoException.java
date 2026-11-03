package es.esports.excepciones.reglaNegocioException;

public class ResultadoInvalidoException extends RuntimeException {
    public ResultadoInvalidoException(String message) {
        super(message);
    }
}
