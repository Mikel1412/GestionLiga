package es.esports.excepciones.reglaNegocioException;

//Esta excepcion se lanzara cuando la plantilla de un equipo este llena
// (7 personas)

public class PlantillaLlenaException extends ReglaDeNegocioException{
    public PlantillaLlenaException (String mensaje){
        super(mensaje);
    }

    public PlantillaLlenaException (String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
