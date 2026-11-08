package es.esports.dao.json;

import es.esports.dao.JugadorDAO;
import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.excepciones.reglaNegocioException.JuegoIncompatibleException;
import es.esports.excepciones.reglaNegocioException.JugadorSinEquipoException;
import es.esports.excepciones.reglaNegocioException.JugadorYaFichadoException;
import es.esports.excepciones.reglaNegocioException.PlantillaLlenaException;
import es.esports.modelo.Equipo;
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

    private final File fichero;   // por ejemplo new File("datos/jugador.json")
    private static final int MAX_TITULARES = 5;
    private static final int MAX_PLANTILLA = 7;

    public JugadorDAOJson(File fichero) throws DAOException {
        if (fichero == null) {
            throw new IllegalArgumentException("La ruta del fichero no puede ser null");
        }
        this.fichero = fichero;

        // Primera vez: el fichero no existe, lo creamos con una lista vacía
        if (!fichero.exists()) {
            guardar(new ArrayList<>());
        }
    }

    private List<Jugador> cargar() throws DAOException {
        // Lo que se abre dentro del paréntesis del try se cierra solo al terminar. (nuestro Try-with-resources).
        try (FileReader lectorFichero = new FileReader(fichero, StandardCharsets.UTF_8);
             JsonReader lectorJson = Json.createReader(lectorFichero)) {

            JsonArray array = lectorJson.readArray();
            List<Jugador> jugadores = new ArrayList<>();

            // Cada elemento del array es un { ... } del JSON
            for (JsonObject o : array.getValuesAs(JsonObject.class)) {
                Rol rol2 = null;
                if(o.containsKey("rol2") && !o.isNull("rol2")){
                    rol2 = Rol.valueOf(o.getString("rol2"));
                }

                String equipo = null;
                if(o.containsKey("equipo") && !o.isNull("equipo")){
                    equipo = o.getString("equipo");
                }
                Jugador jugador = new Jugador(
                        o.getString("nombre"),
                        o.getString("apellido1"),
                        o.getString("apellido2"),
                        o.getString("nickname"),
                        o.getInt("edad"),
                        Juego.valueOf(o.getString("juego")),   // el texto "VALORANT" -> Juego.VALORANT
                        Rol.valueOf(o.getString("rol1")),
                        rol2,
                        equipo
                );

                if (jugador.getEquipo() != null && o.containsKey("titular")) {
                    jugador.setTitular(o.getBoolean("titular"));
                }

                jugadores.add(jugador);
            }
            return jugadores;

        } catch (IOException | RuntimeException e) {
            // Fichero no legible, JSON mal escrito, campo que falta o dato que no pasa la validación
            throw new DAOException("No se pudo leer el fichero " + fichero, e);
        }
    }


    // ---------- ESCRIBIR: lista de Jugador -> fichero ----------


    private void guardar(List<Jugador> jugadores) throws DAOException {

        // 1. Construir el array JSON, jugador por jugador
        JsonArrayBuilder array = Json.createArrayBuilder();
        for (Jugador j : jugadores) {
            JsonObjectBuilder o = Json.createObjectBuilder()
                    .add("nombre", j.getNombre())
                    .add("apellido1", j.getApellido1())
                    .add("apellido2", j.getApellido2())
                    .add("nickname", j.getNickname())
                    .add("edad", j.getEdad())
                    .add("juego", j.getJuego().name())
                    .add("rol1", j.getRol1().name())
                    .add("titular", j.isTitular());
            if (j.getRol2() != null) {
                o.add("rol2", j.getRol2().name());
            } else {
                o.addNull("rol2");
            }
            if (j.getEquipo() != null) {
                o.add("equipo", j.getEquipo());
            } else {
                o.addNull("equipo");
            }
            array.add(o);
        }

        // 2. Escribirlo en el fichero
        try {
            if (fichero.getParentFile() != null) {
                fichero.getParentFile().mkdirs();   // crea la carpeta datos/ si no existe
            }
            Map<String, Object> config = Map.of(JsonGenerator.PRETTY_PRINTING, true);
            try (FileWriter escritorFichero = new FileWriter(fichero, StandardCharsets.UTF_8);
                 JsonWriter escritorJson = Json.createWriterFactory(config).createWriter(escritorFichero)) {
                escritorJson.writeArray(array.build());
            }
        } catch (IOException e) {
            throw new DAOException("No se pudo escribir el fichero " + fichero, e);
        }
    }

    //================Metodos Auxiliares=============

    private void asignarEquipo(List<Jugador> jugadores, Jugador jugador, Equipo equipo)
            throws JuegoIncompatibleException, PlantillaLlenaException {
        if (jugador.getJuego() != equipo.getJuego()) {
            throw new JuegoIncompatibleException("El jugador " + jugador.getNickname() + " juega a "
                    + jugador.getJuego() + " y el equipo " + equipo.getNombre() + " es de " + equipo.getJuego());
        }

        int total = 0;
        int titulares = 0;
        for (Jugador j : jugadores) {
            if (j.getEquipo() != null && j.getEquipo().equalsIgnoreCase(equipo.getNombre())) {
                total++;
                if (j.isTitular()) {
                    titulares++;
                }
            }
        }
        if (total >= MAX_PLANTILLA) {
            throw new PlantillaLlenaException("El equipo " + equipo.getNombre()
                    + " ya tiene " + MAX_PLANTILLA + " jugadores");
        }

        jugador.setEquipo(equipo.getNombre());
        jugador.setTitular(titulares < MAX_TITULARES);   // hasta 5 entran de titulares; los 2 siguientes, de sustitutos
    }

    private int buscarPosicion(List<Jugador> jugadores, String nombre) {
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).getNickname().equalsIgnoreCase(nombre)) {
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
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser NULO");
        }
        if (jugador.getEquipo() != null) {
            throw new IllegalArgumentException("Un jugador nuevo se crea sin equipo: usa ficharPorEquipo");
        }

        List<Jugador> jugadores = cargar();
        if (buscarPosicion(jugadores, jugador.getNickname()) != -1) {
            throw new DuplicadoException("jugador " +jugador.getNickname());
        }
        jugadores.add(jugador);
        guardar(jugadores);
    }

    @Override
    public Jugador leerJugador(String nickname) throws NoEncontradoException, DAOException {
        String buscado = limpiarTexto(nickname);

        List<Jugador> jugadores = cargar();

        int posicion = buscarPosicion(jugadores, buscado);
        if (posicion == -1) {
            throw new NoEncontradoException("jugador " +buscado);
        }

        return jugadores.get(posicion);
    }

    @Override
    public List<Jugador> leerTodosJugadores() throws DAOException {
        return cargar();
    }

    @Override
    public void actualizarJugador(Jugador jugador) throws NoEncontradoException, DAOException {
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser NULO");
        }

        List<Jugador> jugadores = cargar();
        int posicion = buscarPosicion(jugadores, jugador.getNickname());
        if (posicion == -1) {
            throw new NoEncontradoException("jugador" +jugador.getNickname());
        }

        // El equipo y la titularidad no se cambian aquí (para eso están fichar, liberar y traspasar)
        Jugador guardado = jugadores.get(posicion);
        jugador.setEquipo(guardado.getEquipo());
        jugador.setTitular(guardado.isTitular());

        jugadores.set(posicion, jugador);
        guardar(jugadores);
    }

    @Override
    public void borrarJugador(String nickname) throws DAOException, NoEncontradoException {
        String textoLimpio = limpiarTexto(nickname);

        boolean encontrado = false;

        List<Jugador> jugadores = cargar();

        Iterator<Jugador> it = jugadores.iterator();

        while (it.hasNext()){
            Jugador j = it.next();
            if (j.getNickname().equalsIgnoreCase(textoLimpio)){
                it.remove();
                encontrado = true;
            }
        }

        if (!encontrado) {
            throw new NoEncontradoException("jugador" +textoLimpio);
        }

        guardar(jugadores);
    }

    //==============Operaciones no CRUD==================

    @Override
    public int contarJugadores() throws DAOException {
        return cargar().size();
    }

    @Override
    public List<Jugador> jugadoresLibres() throws DAOException {
        List<Jugador> jugadores = cargar();

        List<Jugador> jugadoresLibres = new ArrayList<>();

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
        if (juego == null) {
            throw new IllegalArgumentException("El juego no puede ser NULO");
        }
        List<Jugador> jugadores = cargar();

        List<Jugador> jugadoresJuego = new ArrayList<>();

        Iterator<Jugador> it = jugadores.iterator();

        while (it.hasNext()){
            Jugador j = it.next();
            if (j.getJuego().equals(juego)){
                jugadoresJuego.add(j);
            }
        }

        return jugadoresJuego;
    }

    @Override
    public void ficharPorEquipo(String nickname, Equipo equipo)
            throws NoEncontradoException, JugadorYaFichadoException, JuegoIncompatibleException,
            PlantillaLlenaException, DAOException {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no puede ser NULO");
        }
        String nick = limpiarTexto(nickname);

        List<Jugador> jugadores = cargar();
        int posicion = buscarPosicion(jugadores, nick);
        if (posicion == -1) {
            throw new NoEncontradoException("jugador " +nick);
        }

        Jugador jugador = jugadores.get(posicion);
        if (jugador.getEquipo() != null) {
            throw new JugadorYaFichadoException("El jugador " + nick + " ya tiene equipo: " + jugador.getEquipo());
        }

        asignarEquipo(jugadores, jugador, equipo);
        guardar(jugadores);
    }

    @Override
    public void liberarJugador(String nickname)
            throws NoEncontradoException, JugadorSinEquipoException, DAOException {
        String nick = limpiarTexto(nickname);

        List<Jugador> jugadores = cargar();
        int posicion = buscarPosicion(jugadores, nick);
        if (posicion == -1) {
            throw new NoEncontradoException("jugador " +nick);
        }

        Jugador jugador = jugadores.get(posicion);
        if (jugador.getEquipo() == null) {
            throw new JugadorSinEquipoException("El jugador " + nick + " ya es agente libre");
        }

        jugador.setEquipo(null);
        guardar(jugadores);
    }

    @Override
    public void traspasarJugador(String nickname, Equipo nuevoEquipo)
            throws NoEncontradoException, JugadorSinEquipoException, JugadorYaFichadoException,
            JuegoIncompatibleException, PlantillaLlenaException, DAOException {
        if (nuevoEquipo == null) {
            throw new IllegalArgumentException("El equipo no puede ser NULO");
        }
        String nick = limpiarTexto(nickname);

        List<Jugador> jugadores = cargar();
        int posicion = buscarPosicion(jugadores, nick);
        if (posicion == -1) {
            throw new NoEncontradoException("jugador " +nick);
        }

        Jugador jugador = jugadores.get(posicion);
        if (jugador.getEquipo() == null) {
            throw new JugadorSinEquipoException("El jugador " + nick + " es agente libre: usa ficharPorEquipo");
        }
        if (jugador.getEquipo().equalsIgnoreCase(nuevoEquipo.getNombre())) {
            throw new JugadorYaFichadoException("El jugador " + nick + " ya está en " + nuevoEquipo.getNombre());
        }

        asignarEquipo(jugadores, jugador, nuevoEquipo);
        guardar(jugadores);
    }

    @Override
    public List<Jugador> jugadoresDeEquipo(String equipo) throws DAOException {
        String buscado = limpiarTexto(equipo);

        List<Jugador> resultado = new ArrayList<>();
        for (Jugador j : cargar()) {
            if (j.getEquipo() != null && j.getEquipo().equalsIgnoreCase(buscado)) {
                resultado.add(j);
            }
        }
        return resultado;
    }

    @Override
    public void liberarJugadoresDeEquipo(String equipo) throws DAOException {
        String buscado = limpiarTexto(equipo);

        List<Jugador> jugadores = cargar();
        for (Jugador j : jugadores) {
            if (j.getEquipo() != null && j.getEquipo().equalsIgnoreCase(buscado)) {
                j.setEquipo(null);
            }
        }
        guardar(jugadores);
    }

    @Override
    public void cambiarTitularidad(String nickname, boolean titular)
            throws NoEncontradoException, JugadorSinEquipoException, PlantillaLlenaException, DAOException {
        String nick = limpiarTexto(nickname);

        List<Jugador> jugadores = cargar();
        int posicion = buscarPosicion(jugadores, nick);
        if (posicion == -1) {
            throw new NoEncontradoException("jugador "+ nick);
        }

        Jugador jugador = jugadores.get(posicion);
        if (jugador.getEquipo() == null) {
            throw new JugadorSinEquipoException("El jugador " + nick + " es agente libre: sin equipo no puede ser titular");
        }

        // Para hacerlo titular, el equipo no puede tener ya 5 titulares
        if (titular && !jugador.isTitular()) {
            int titulares = 0;
            for (Jugador j : jugadores) {
                if (j.getEquipo() != null && j.getEquipo().equalsIgnoreCase(jugador.getEquipo()) && j.isTitular()) {
                    titulares++;
                }
            }
            if (titulares >= MAX_TITULARES) {
                throw new PlantillaLlenaException("El equipo " + jugador.getEquipo() + " ya tiene " + MAX_TITULARES + " titulares");
            }
        }

        jugador.setTitular(titular);
        guardar(jugadores);
    }

}
