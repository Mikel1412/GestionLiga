package es.esports.excepciones.reglaNegocioException;

//Lanzaremos esta excepcion cuando intentemos incribir a un equipo cuyo
// juego no corresponde con el juego del torneo

public class JuegoIncompatibleException extends Exception{
    public JuegoIncompatibleException(String mensaje){
        super(mensaje);
    }

    public JuegoIncompatibleException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
