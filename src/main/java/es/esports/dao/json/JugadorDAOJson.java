package es.esports.dao.json;

import es.esports.dao.JugadorDAO;
import es.esports.excepciones.DAOException;
import es.esports.modelo.Jugador;
import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class JugadorDAOJson implements JugadorDAO {
    private final File RUTA_JUGADOR_JSON = new File("datos/jugador.json");

    @Override
    public void crearJugador(Jugador jugador) throws DAOException, IOException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(Json.createObjectBuilder()
                .add("nombre", jugador.getNombre())
                .add("apellido1", jugador.getApellido1())
                .add("apellido2", jugador.getApellido2())
                .add("nickname", jugador.getNickname())
                .add("edad", jugador.getEdad())
                .add("juego", jugador.getJuego())
                .add("Rol1", jugador.getRol1())
                .add("Rol2", jugador.getRol2())
                .add("Equipo", jugador.getEquipo()));

        Map<String, Object> config = Map.of(JsonGenerator.PRETTY_PRINTING,
                true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(config);
        try (FileWriter fileWriter = new FileWriter(RUTA_JUGADOR_JSON);
             JsonWriter jsonWriter =
                     writerFactory.createWriter(fileWriter)) {
            jsonWriter.writeArray(arrayBuilder.build());
        }
    }

    @Override
    public void leerJugador(Jugador jugador) throws DAOException, IOException{

    }

    @Override
    public void actualizarJugador(Jugador jugador) {

    }

    @Override
    public void borrarJugador(Jugador jugador) {

    }

    @Override
    public void contarJugadores() {

    }

    @Override
    public void JugadoresLibres() {

    }
}
