package es.esports.excepciones.reglaNegocioException;

public class JugadoresInsuficientesException extends Exception{
    public JugadoresInsuficientesException(String mensaje) {
        super(mensaje);
    }

    public JugadoresInsuficientesException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
