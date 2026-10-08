package es.esports.excepciones.reglaNegocioException;

//Esta excepcion se lanzara cuando la plantilla de un equipo este llena
// (7 personas)

public class PlantillaLlenaException extends Exception{
    private PlantillaLlenaException (String mensaje){
        super(mensaje);
    }

    private PlantillaLlenaException (String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
