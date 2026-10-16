package es.esports.excepciones.reglaNegocioException;

//Lanzaremos esta excepcion cuando intentemos inscribir a un equipo a
//un torneo y este ya haya empezado

public class EstadoTorneoException extends Exception{
    public EstadoTorneoException(String mensaje){
        super(mensaje);
    }

    public EstadoTorneoException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
