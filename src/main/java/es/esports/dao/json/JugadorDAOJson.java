package es.esports.dao.json;

import es.esports.dao.JugadorDAO;
import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.modelo.Juego;
import es.esports.modelo.Jugador;
import es.esports.modelo.Rol;
import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JugadorDAOJson implements JugadorDAO {
    private final File RUTA_JUGADOR_JSON = new File("datos/jugador.json");

    private List<Jugador> cargar() throws DAOException {
        // Lo que se abre dentro del paréntesis del try se cierra solo al terminar. (nuestro Try-with-resources).
        try (FileReader lectorFichero = new FileReader(RUTA_JUGADOR_JSON, StandardCharsets.UTF_8);
             JsonReader lectorJson = Json.createReader(lectorFichero)) {

            JsonArray array = lectorJson.readArray();
            List<Jugador> jugadores = new ArrayList<>();

            // Cada elemento del array es un { ... } del JSON
            for (JsonObject o : array.getValuesAs(JsonObject.class)) {
                Jugador jugador = new Jugador(
                        o.getString("nombre"),
                        o.getString("apellido1"),
                        o.getString("apellido2"),
                        o.getString("nickname"),
                        o.getInt("edad"),
                        Juego.valueOf(o.getString("juego")),   // el texto "VALORANT" -> Juego.VALORANT
                        Rol.valueOf(o.getString("rol1")),
                        Rol.valueOf(o.getString("rol2"))
                );
                jugadores.add(jugador);
            }
            return jugadores;

        } catch (IOException | RuntimeException e) {
            // Fichero no legible, JSON mal escrito, campo que falta o dato que no pasa la validación
            throw new DAOException("No se pudo leer el fichero " + RUTA_JUGADOR_JSON, e);
        }
    }


    // ---------- ESCRIBIR: lista de Equipo -> fichero ----------


    private void guardar(List<Jugador> jugadores) throws DAOException {

        // 1. Construir el array JSON, equipo por equipo
        JsonArrayBuilder array = Json.createArrayBuilder();
        for (Jugador j : jugadores) {
            array.add(Json.createObjectBuilder()
                    .add("nombre", j.getNombre())
                    .add("apellido1", j.getApellido1())
                    .add("apellido2", j.getApellido2())
                    .add("apellido1", j.getNickname())
                    .add("edad", j.getEdad())
                    .add("juego", j.getJuego().name())   // el enum se guarda como texto
                    .add("rol1", j.getRol1().name())
                    .add("rol2", j.getRol2().name()));
        }

        // 2. Escribirlo en el fichero
        try {
            if (RUTA_JUGADOR_JSON.getParentFile() != null) {
                RUTA_JUGADOR_JSON.getParentFile().mkdirs();   // crea la carpeta datos/ si no existe
            }
            Map<String, Object> config = Map.of(JsonGenerator.PRETTY_PRINTING, true);
            try (FileWriter escritorFichero = new FileWriter(RUTA_JUGADOR_JSON, StandardCharsets.UTF_8);
                 JsonWriter escritorJson = Json.createWriterFactory(config).createWriter(escritorFichero)) {
                escritorJson.writeArray(array.build());
            }
        } catch (IOException e) {
            throw new DAOException("No se pudo escribir el fichero " + RUTA_JUGADOR_JSON, e);
        }
    }

    //================Metodos Auxiliares=============

    private int buscarPosicion(List<Jugador> jugadores, String nombre) {
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).getNombre().equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        return -1;
    }

    private String limpiarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }
        return texto.trim();
    }

    @Override
    public void crearJugador(Jugador jugador) throws DuplicadoException, DAOException {
        if(jugador == null){
            throw new IllegalArgumentException("El jugador no puede ser NULO");
        }

        List<Jugador> jugadores = cargar();

        int posicion = buscarPosicion(jugadores, jugador.getNombre());

        if (posicion != -1){
            throw new DuplicadoException("jugador " + jugador.getNombre());
        }

        jugadores.add(jugador);
        guardar(jugadores);
        System.out.println("Jugador creado y guardado");
    }

    @Override
    public Jugador leerJugador(String nombre) throws NoEncontradoException, DAOException {
        String buscado = limpiarTexto(nombre);

        List<Jugador> jugadores = cargar();

        int posicion = buscarPosicion(jugadores, nombre);
        if (posicion == -1) {
            throw new NoEncontradoException("El jugador no existe en la lista");
        }

        return jugadores.get(posicion);
    }

    @Override
    public List<Jugador> leerTodosJugadores() throws NoEncontradoException, DAOException {
        return cargar();
    }

    @Override
    public void actualizarJugador(Jugador jugador) throws NoEncontradoException, DAOException {
        if (jugador == null){
            throw new IllegalArgumentException("El jugador no puede ser NULO");
        } else {
            List<Jugador> jugadores = cargar();

            int posicion = buscarPosicion(jugadores, jugador.getNombre());

            if (posicion == -1){
                throw new NoEncontradoException("No se ha encontrado el jugador en la lista");
            }

            jugadores.set(posicion, jugador);

            guardar(jugadores);
        }
    }

    @Override
    public void borrarJugador(String nombre) throws DAOException, NoEncontradoException {
        String textoLimpio = limpiarTexto(nombre);

        boolean encontrado = false;

        List<Jugador> jugadores = cargar();

        Iterator<Jugador> it = jugadores.iterator();

        while (it.hasNext()){
            Jugador j = it.next();
            if (j.getNombre().equalsIgnoreCase(textoLimpio)){
                it.remove();
                encontrado = true;
            }
        }

        if (!encontrado) {
            throw new NoEncontradoException("No se ha encontrado el jugador para borrar");
        }

        guardar(jugadores);
    }

    @Override
    public void contarJugadores() throws DAOException {
        List<Jugador> jugadores = cargar();

        int numJugadores = jugadores.size();

        System.out.println("Actualmente hay " + numJugadores + " jugadores");
    }

    @Override
    public List<Jugador> jugadoresLibres() throws DAOException {
        List<Jugador> jugadores = cargar();

        List<Jugador> jugadoresLibres = List.of();

        Iterator<Jugador> it = jugadores.iterator();

        while (it.hasNext()){
            Jugador j = it.next();
            if (j.getEquipo() == null){
                jugadoresLibres.add(j);
            }
        }

        return jugadoresLibres;
    }

    @Override
    public List<Jugador> buscarJugadorPorJuego(Juego juego) throws DAOException {
        List<Jugador> jugadores = cargar();

        List<Jugador> jugadoresJuego = List.of();

        Iterator<Jugador> it = jugadores.iterator();

        while (it.hasNext()){
            Jugador j = it.next();
            if (j.getJuego().equals(juego)){
                jugadoresJuego.add(j);
            }
        }

        return jugadoresJuego;
    }
}
