package es.esports.excepciones.reglaNegocioException;


public class EquiposInsuficientesException extends Exception{
    public EquiposInsuficientesException (String mensaje){
        super(mensaje);
    }

    public EquiposInsuficientesException (String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
