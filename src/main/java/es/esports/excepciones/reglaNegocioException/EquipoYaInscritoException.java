package es.esports.excepciones.reglaNegocioException;

//Esta excepcion se lanzara cuando el equipo que estemos intentando
// meter este ya inscrito en el torneo

public class EquipoYaInscritoException extends ReglaDeNegocioException{
    public EquipoYaInscritoException (String mensaje){
        super(mensaje);
    }

    public EquipoYaInscritoException (String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
