package es.esports.excepciones;

//Equipo o Jugador duplicado

public class DuplicadoException extends Exception{
    public DuplicadoException (String mensaje){
        super(mensaje);
    }

    public DuplicadoException (String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
