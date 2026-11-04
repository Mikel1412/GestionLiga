package es.esports.dao.json;

import es.esports.dao.EquipoDAO;
import es.esports.dao.JugadorDAO;
import es.esports.dao.PartidaDAO;
import es.esports.dao.TorneoDAO;
import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.excepciones.reglaNegocioException.*;
import es.esports.modelo.*;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TorneoDAOJson implements TorneoDAO {

    private final File fichero;
    private final EquipoDAO equipoDAO;
    private final JugadorDAO jugadorDAO;
    private final PartidaDAO partidaDAO;

    // Recibe el fichero y los otros DAO que necesita para sus operaciones propias
    public TorneoDAOJson(File fichero, EquipoDAO equipoDAO, JugadorDAO jugadorDAO, PartidaDAO partidaDAO)
            throws DAOException {
        if (fichero == null) {
            throw new IllegalArgumentException("El fichero no puede ser null");
        }
        this.fichero = fichero;
        this.equipoDAO = equipoDAO;
        this.jugadorDAO = jugadorDAO;
        this.partidaDAO = partidaDAO;

        // Primera ejecución: si no existe el fichero, se crea con una lista vacía "[]"
        if (!fichero.exists()) {
            guardar(new ArrayList<>());
        }
    }

    // ---------- Leer y escribir el fichero ----------

    private List<Torneo> cargar() throws DAOException {
        // Lo que se abre dentro del paréntesis del try se cierra solo al terminar. (nuestro Try-with-resources).
        try (FileReader lectorFichero = new FileReader(fichero, StandardCharsets.UTF_8);
             JsonReader lectorJson = Json.createReader(lectorFichero)) {

            JsonArray array = lectorJson.readArray();
            List<Torneo> torneos = new ArrayList<>();

            // Cada elemento del array es un { ... } del JSON
            for (JsonObject o : array.getValuesAs(JsonObject.class)) {
                Torneo t = new Torneo(
                        o.getString("nombre"),
                        Juego.valueOf(o.getString("juego")),
                        Formato.valueOf(o.getString("formato")),
                        o.getString("fechaI"),
                        o.getString("fechaF"),
                        o.getInt("maxEquipos"));

                ArrayList<String> equipos = new ArrayList<>();
                for (JsonString s : o.getJsonArray("equipos").getValuesAs(JsonString.class)) {
                    equipos.add(s.getString());
                }
                t.setEquipos(equipos);
                t.setEstado(EstadoTorneo.valueOf(o.getString("estado")));
                torneos.add(t);
            }
            return torneos;

        } catch (IOException | RuntimeException e) {
            // Fichero no legible, JSON mal escrito, campo que falta o dato que no pasa la validación
            throw new DAOException("No se pudo leer el fichero " + fichero, e);
        }
    }

    private void guardar(List<Torneo> torneos) throws DAOException {

        // 1. Construir el array JSON, torneo por torneo.
        JsonArrayBuilder array = Json.createArrayBuilder();
        for (Torneo t : torneos) {

            // Array con los nombres de los equipos de ESTE torneo
            JsonArrayBuilder equiposJson = Json.createArrayBuilder();
            for (String e : t.getEquipos()) {
                equiposJson.add(e);
            }

            // El objeto del torneo, con el array de equipos como un campo más
            array.add(Json.createObjectBuilder()
                    .add("nombre", t.getNombre())
                    .add("juego", t.getJuego().name())
                    .add("formato", t.getFormato().name())
                    .add("fechaI", t.getFechaI())
                    .add("fechaF", t.getFechaF())
                    .add("maxEquipos", t.getMaxEquipos())
                    .add("estado", t.getEstado().name())
                    .add("equipos", equiposJson));
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

    // ---------- Métodos auxiliares ----------

    // Devuelve la posición del torneo con ese nombre en la lista, o -1 si no está.
    // Se usa para evitar duplicados en crear y para localizar el torneo en
    // leer, actualizar, borrar y el resto de operaciones.
    private int buscarPosicion(List<Torneo> torneos, String nombre) {
        for (int i = 0; i < torneos.size(); i++) {
            if (torneos.get(i).getNombre().equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        return -1;
    }

    // Valida los argumentos de texto que llegan de fuera (leer, borrar, inscribirEquipo...)
    // y los devuelve sin espacios a los lados.
    private String limpiarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }
        return texto.trim();
    }

    @Override
    public void crear(Torneo torneo) throws DuplicadoException, DAOException {

        //El dato a introducir no puede ser null, osea que lanzariamos una excepcion en caso de que si fuera null.
        if (torneo == null) {
            throw new IllegalArgumentException("El torneo no puede ser null");
        }

        //cargamos la lista de torneos
        List<Torneo> torneos = cargar();

        // La clave es el nombre: si ya hay uno con ese nombre (sin distinguir mayúsculas), error
        if (buscarPosicion(torneos, torneo.getNombre()) != -1) {
            throw new DuplicadoException("torneo " +torneo.getNombre());
        }

        torneos.add(torneo);
        guardar(torneos);

    }

    @Override
    public Torneo leer(String nombre) throws NoEncontradoException, DAOException {
        String limpio = limpiarTexto(nombre); //Limpiamos el String introducido en el parametro para ver si no es null y hacerle un trim().
        List<Torneo> torneos = cargar(); //Cargamos la lista de torneos

        int posicion = buscarPosicion(torneos,limpio); //Buscamos la posicion de donde esta el objeto torneo que tiene el nombre que le hemos pasado por parametro
        if (posicion == -1) {
            throw new NoEncontradoException("torneo" +limpio); //Si no existe nos lanza una excepción
        }
        return torneos.get(posicion);
    }

    @Override
    public List<Torneo> leerTodos() throws DAOException {
        return cargar(); //Simplemente hacemos un return del metodo cargar para todas las listas
    }

    @Override
    public void actualizar(Torneo torneo) throws NoEncontradoException, DAOException {
        if (torneo==null) {
            throw new IllegalArgumentException("El torneo no puede ser null");
        }

        List<Torneo> torneos = cargar();
        int posicion = buscarPosicion(torneos,torneo.getNombre());

        if (posicion == -1) {
            throw new NoEncontradoException("No se ha encontrado el torneo que quieres actualizar");
        }

        torneos.set(posicion,torneo);
        guardar(torneos);
    }

    @Override
    public void borrar(String nombre) throws NoEncontradoException, EstadoTorneoException, DAOException {
        String limpio = limpiarTexto(nombre);

        List<Torneo> torneos = cargar();
        int posicion = buscarPosicion(torneos, limpio);
        if (posicion == -1) {
            throw new NoEncontradoException("torneo "+limpio);
        }

        Torneo torneo = torneos.get(posicion);
        if (torneo.getEstado() == EstadoTorneo.ACTIVO) {
            throw new EstadoTorneoException("No se puede borrar un torneo activo: " + torneo.getNombre());
        }

        // Borramos también sus partidas para que no queden huérfanas
        for (Partida p : partidaDAO.partidasDeTorneo(torneo.getNombre())) { //PREGUNTAR LUEGO POR ESTA OPCION DE BORRAR. (Por que si no luego las clases quedan volando por ahi)
            partidaDAO.borrar(p.getId());
        }

        torneos.remove(posicion);
        guardar(torneos);

    }

    @Override
    public void inscribirEquipo(String nombreTorneo, String nombreEquipo) throws NoEncontradoException, JuegoIncompatibleException, JugadoresInsuficientesException, EquipoYaInscritoException, PlantillaLlenaException, EstadoTorneoException, DAOException {
        String limpioTorneo = limpiarTexto(nombreTorneo);
        String limpioEquipo = limpiarTexto(nombreEquipo);

        //Buscamos el torneo
        List<Torneo> torneos = cargar();
        int posicion = buscarPosicion(torneos, limpioTorneo);
        if (posicion == -1) {
            throw new NoEncontradoException("torneo " +limpioTorneo);
        }
        Torneo torneo = torneos.get(posicion);

        //Buscamos el equipo (si no existe, el EquipoDAO lanza NoEncontradoException)
        Equipo equipo = equipoDAO.leer(limpioEquipo);

        //El equipo debe ser del mismo juego que el torneo
        if (equipo.getJuego() != torneo.getJuego()) {
            throw new JuegoIncompatibleException("El equipo " + equipo.getNombre() + " juega a "
                    + equipo.getJuego() + " y el torneo es de " + torneo.getJuego());
        }

        //Debe tener exactamente 5 titulares
        int titulares = 0;
        for (Jugador j : jugadorDAO.jugadoresDeEquipo(equipo.getNombre())) { //PREGUNTAR LUEGO POR ESTA OPCION DE INSCRIBIR EQUIPO.
            if (j.isTitular()) {
                titulares++;
            }
        }
        if (titulares != 5) {
            throw new JugadoresInsuficientesException("El equipo " + equipo.getNombre()
                    + " necesita 5 titulares para inscribirse (tiene " + titulares + ")");
        }

        //El propio torneo comprueba estado, equipo repetido y torneo lleno
        torneo.anadirEquipo(equipo.getNombre());

        //Guardar (el objeto ya está dentro de la lista, modificado)
        guardar(torneos);

    }

    @Override
    public void iniciarTorneo(String nombreTorneo) throws NoEncontradoException, EstadoTorneoException, EquiposInsuficientesException, DAOException {

        String limpio = limpiarTexto(nombreTorneo);

        List<Torneo> torneos = cargar();
        int posicion = buscarPosicion(torneos,limpio);
        if (posicion == -1) {
            throw new NoEncontradoException("Torneo " +limpio);
        }

        Torneo torneo = torneos.get(posicion);

        //El propio torneo comprueba estado y mínimo de equipos, y pasa a ACTIVO
        torneo.iniciar();

        //Generar las partidas: todos contra todos (cada pareja una sola vez)
        List<String> equipos = torneo.getEquipos();
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                Partida partida = new Partida(torneo.getNombre(), equipos.get(i), equipos.get(j),
                        torneo.getFechaI(), torneo.getJuego());
                partidaDAO.crear(partida);
            }
        }
        guardar(torneos);
    }

    @Override
    public void finalizarTorneo(String nombreTorneo) throws NoEncontradoException, EstadoTorneoException, DAOException {
        String limpio = limpiarTexto(nombreTorneo);

        //Buscar el torneo
        List<Torneo> torneos = cargar();
        int posicion = buscarPosicion(torneos, limpio);
        if (posicion == -1) {
            throw new NoEncontradoException("torneo "+limpio);
        }
        Torneo torneo = torneos.get(posicion);

        //Todas sus partidas deben tener resultado
        for (Partida p : partidaDAO.partidasDeTorneo(torneo.getNombre())) { //CONSULTAR ESTA OPCION TAMBIEN
            if (p.getEstado() == EstadoPartida.PENDIENTE) {
                throw new EstadoTorneoException("No se puede finalizar: aún hay partidas pendientes en " + torneo.getNombre());
            }
        }

        //El propio torneo comprueba que esté ACTIVO y pasa a FINALIZADO
        torneo.finalizar();

        //Guardar
        guardar(torneos);
    }

    @Override
    public List<Torneo> torneosPorJuego(Juego juego) throws DAOException {
        if (juego==null) {
            throw new IllegalArgumentException("El juego no puede ser null");
        }
        List<Torneo> torneos = cargar();
        List<Torneo> nueva = new ArrayList<>();
        for (Torneo t : torneos) {
            if (t.getJuego() == juego) {
                nueva.add(t);
            }
        }
        return nueva;
    }

    @Override
    public List<Torneo> torneosPorEstado(EstadoTorneo estado) throws DAOException {
        if (estado==null) {
            throw new IllegalArgumentException("El estado no puede ser null");
        }
        List<Torneo> torneos = cargar();
        List<Torneo> nueva = new ArrayList<>();
        for (Torneo t : torneos) {
            if (t.getEstado() == estado) {
                nueva.add(t);
            }
        }
        return nueva;
    }
}
