package es.esports.dao.json;

import es.esports.dao.JugadorDAO;
import es.esports.excepciones.DAOException;
import es.esports.modelo.Jugador;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonValue;

import java.nio.file.Path;

public class JugadorDAOJson implements JugadorDAO {
    private final Path RUTA_JUGADOR_JSON = Path.of("datos/jugador.json");

    @Override
    public void crearJugador(Jugador jugador) throws DAOException {
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

    }

    @Override
    public void leerJugador(Jugador jugador) {

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
