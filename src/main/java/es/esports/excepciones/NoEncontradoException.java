package es.esports.excepciones;

//Equipo o jugador no encontrados

public class NoEncontradoException extends Exception{
    public NoEncontradoException (String mensaje){
        super(mensaje);
    }

    public NoEncontradoException (String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
