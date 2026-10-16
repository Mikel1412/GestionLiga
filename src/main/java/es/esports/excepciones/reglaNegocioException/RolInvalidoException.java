package es.esports.excepciones.reglaNegocioException;

//Esta excepcion se lanzará cuando el rol del jugador no es el del equipo

public class RolInvalidoException extends Exception{
    public RolInvalidoException(String mensaje){
        super(mensaje);
    }

    public RolInvalidoException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
