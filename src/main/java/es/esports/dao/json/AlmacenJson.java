package es.esports.dao.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.esports.excepciones.DAOException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class AlmacenJson<T> {

    private final Path fichero;          // ruta del fichero, por ejemplo datos/equipos.json
    private final Class<T[]> tipoArray;  // tipo de dato que guarda: Equipo[].class, Jugador[].class...
    private final ObjectMapper mapper = new ObjectMapper(); // convierte objetos <-> JSON

    protected AlmacenJson(Path fichero, Class<T[]> tipoArray) throws DAOException { //Protected para que la propia clase, sus hijos y las clases del mismo paquete puedan utilizar estos métodos
        if (fichero == null) {
            throw new IllegalArgumentException("La ruta del fichero no puede ser null");
        }
        this.fichero = fichero;
        this.tipoArray = tipoArray;

        // Si el fichero no existe todavía, lo creamos con una lista vacía
        if (!Files.exists(fichero)) {
            guardar(new ArrayList<>());
        }
    }

    // LEER: abre el fichero y lo convierte en una lista de objetos
    protected List<T> cargar() throws DAOException {
        try {
            T[] array = mapper.readValue(fichero.toFile(), tipoArray);
            return new ArrayList<>(Arrays.asList(array));
        } catch (IOException e) {
            throw new DAOException("No se pudo leer el fichero " + fichero, e);
        }
    }

    // ESCRIBIR: convierte la lista en texto JSON y lo guarda en el fichero
    protected void guardar(List<T> elementos) throws DAOException {
        try {
            File archivo = fichero.toFile();
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs(); // crea la carpeta si no existe
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, elementos);
        } catch (IOException e) {
            throw new DAOException("No se pudo escribir el fichero " + fichero, e);
        }
    }
}
