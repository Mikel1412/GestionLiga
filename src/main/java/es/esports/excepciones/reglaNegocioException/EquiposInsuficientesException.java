package es.esports.excepciones.reglaNegocioException;

//Esta excepcion se lanzara en los casos que se inicie el torneo con menos
// de dos o con la plantilla incompleta

public class EquiposInsuficientesException extends Exception{
    public EquiposInsuficientesException (String mensaje){
        super(mensaje);
    }

    public EquiposInsuficientesException (String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
