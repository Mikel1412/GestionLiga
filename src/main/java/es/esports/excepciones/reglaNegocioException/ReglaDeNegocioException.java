package es.esports.excepciones.reglaNegocioException;

import es.esports.excepciones.DAOException;

public class ReglaDeNegocioException extends DAOException {
    public ReglaDeNegocioException(String message) {
        super(message);
    }

    public ReglaDeNegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}
