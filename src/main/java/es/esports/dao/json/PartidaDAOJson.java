package es.esports.dao.json;

import es.esports.dao.PartidaDAO;
import es.esports.excepciones.DAOException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.modelo.EstadoPartida;
import es.esports.modelo.Juego;
import es.esports.modelo.Partida;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class PartidaDAOJson implements PartidaDAO{


    private final File fichero;

    public PartidaDAOJson(File fichero) throws DAOException {
        if (fichero == null) {
            throw new IllegalArgumentException("La ruta del fichero no puede ser null");
        }
        this.fichero = fichero;


        if (!fichero.exists()) {
            guardar(new ArrayList<>());
        }
    }



    private List<Partida> cargar() throws DAOException {
        try (FileReader lectorFichero = new FileReader(fichero, StandardCharsets.UTF_8);
             JsonReader lectorJson = Json.createReader(lectorFichero)) {

            JsonArray array = lectorJson.readArray();
            List<Partida> partidas = new ArrayList<>();

            for (JsonObject o : array.getValuesAs(JsonObject.class)) {

                Partida partida = new Partida(
                        o.getString("torneo"),
                        o.getString("equipo1"),
                        o.getString("equipo2"),
                        o.getString("fecha"),
                        Juego.valueOf(o.getString("juego")));


                partida.setId(o.getInt("id"));
                partida.setJugadores1(o.getJsonArray("jugadores1").getValuesAs(JsonString::getString));
                partida.setJugadores2(o.getJsonArray("jugadores2").getValuesAs(JsonString::getString));


                String ganador = o.getString("ganador", null);
                if (ganador != null) {
                    partida.setGanador(ganador);
                }


                EstadoPartida estadoFichero = EstadoPartida.valueOf(o.getString("estado"));
                if (estadoFichero != partida.getEstado()) {
                    throw new DAOException("Partida " + partida.getId() + ": el estado " + estadoFichero
                            + " no coincide con su ganador");
                }
                partidas.add(partida);
            }
            return partidas;


        } catch (IOException | RuntimeException e) {

            throw new DAOException("No se pudo leer el fichero " + fichero, e);
        }
    }


    private void guardar(List<Partida> partidas) throws DAOException {


        JsonArrayBuilder array = Json.createArrayBuilder();
        for (Partida p : partidas) {
            JsonArrayBuilder jugadores1 = Json.createArrayBuilder();
            for (String nick : p.getJugadores1()) {
                jugadores1.add(nick);
            }
            JsonArrayBuilder jugadores2 = Json.createArrayBuilder();
            for (String nick : p.getJugadores2()) {
                jugadores2.add(nick);
            }

            JsonObjectBuilder objeto = Json.createObjectBuilder()
                    .add("id", p.getId())
                    .add("torneo", p.getTorneo())
                    .add("equipo1", p.getEquipo1())
                    .add("equipo2", p.getEquipo2())
                    .add("jugadores1", jugadores1)
                    .add("jugadores2", jugadores2)
                    .add("estado", p.getEstado().name())
                    .add("fecha", p.getFecha())
                    .add("juego", p.getJuego().name());

            if (p.getGanador() != null) {
                objeto.add("ganador", p.getGanador());
            }
            array.add(objeto);
        }

        try {
            if (fichero.getParentFile() != null) {
                fichero.getParentFile().mkdirs();
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



    private int buscarPosicion(List<Partida> partidas, int id) {
        for (int i = 0; i < partidas.size(); i++) {
            if (partidas.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }


    private int siguienteId(List<Partida> partidas) {
        int max = 0;
        for (Partida p : partidas) {
            max = Math.max(max, p.getId());
        }
        return max + 1;
    }



    private String limpiarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }
        return texto.trim();
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la partida debe ser positivo");
        }
    }



    @Override
    public void crear(Partida partida) throws DAOException {
        if (partida == null) {
            throw new IllegalArgumentException("No puede ser NULA la partida");
        }

        List<Partida> partidas = cargar();

        partida.setId(siguienteId(partidas));
        partidas.add(partida);
        guardar(partidas);
    }

    @Override
    public Partida leer(int id) throws NoEncontradoException, DAOException {
        validarId(id);

        List<Partida> partidas = cargar();

        int posicion = buscarPosicion(partidas, id);
        if (posicion == -1) {
            throw new NoEncontradoException("No existe la partida con id " + id);
        }
        return partidas.get(posicion);
    }

    @Override
    public List<Partida> leerTodos() throws DAOException {
        return cargar();
    }

    @Override
    public void actualizar(Partida partida) throws NoEncontradoException, DAOException {
        if (partida == null) {
            throw new IllegalArgumentException("No puede ser NULA la partida");
        }
        validarId(partida.getId());

        List<Partida> partidas = cargar();

        int posicion = buscarPosicion(partidas, partida.getId());
        if (posicion == -1) {
            throw new NoEncontradoException("No existe la partida con id " + partida.getId());
        }

        partidas.set(posicion, partida);
        guardar(partidas);
    }

    @Override
    public void borrar(int id) throws NoEncontradoException, DAOException {
        validarId(id);

        List<Partida> partidas = cargar();

        int posicion = buscarPosicion(partidas, id);
        if (posicion == -1) {
            throw new NoEncontradoException("No existe la partida con id " + id);
        }

        partidas.remove(posicion);
        guardar(partidas);
    }



    @Override
    public List<Partida> partidasPorTorneo(String torneo) throws DAOException {
        String limpio = limpiarTexto(torneo);

        List<Partida> resultado = new ArrayList<>();
        for (Partida p : cargar()) {
            if (p.getTorneo().equalsIgnoreCase(limpio)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public List<Partida> partidasPorEquipo(String equipo) throws DAOException {
        String limpio = limpiarTexto(equipo);

        List<Partida> resultado = new ArrayList<>();
        for (Partida p : cargar()) {
            if (p.getEquipo1().equalsIgnoreCase(limpio) || p.getEquipo2().equalsIgnoreCase(limpio)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public List<Partida> partidasPorEstado(EstadoPartida estado) throws DAOException {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser null");
        }

        List<Partida> resultado = new ArrayList<>();
        for (Partida p : cargar()) {
            if (p.getEstado() == estado) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public boolean existePartida(int id) throws DAOException {
        validarId(id);

        return buscarPosicion(cargar(), id) != -1;
    }

    @Override
    public Partida proximaPartida(String equipo) throws NoEncontradoException, DAOException {
        String limpio = limpiarTexto(equipo);
        LocalDate hoy = LocalDate.now();

        Partida proxima = null;
        LocalDate fechaProxima = null;

        for (Partida p : cargar()) {
            boolean juegaElEquipo = p.getEquipo1().equalsIgnoreCase(limpio) || p.getEquipo2().equalsIgnoreCase(limpio);
            if (!juegaElEquipo || p.getEstado() != EstadoPartida.PENDIENTE) {
                continue;
            }
            LocalDate fecha = LocalDate.parse(p.getFecha());
            if (fecha.isBefore(hoy)) {
                continue;
            }

            if (proxima == null || fecha.isBefore(fechaProxima)) {
                proxima = p;
                fechaProxima = fecha;
            }
        }
        if (proxima == null) {
            throw new NoEncontradoException("El equipo " + limpio + " no tiene partidas pendientes");
        }
        return proxima;
    }

    @Override
    public ArrayList<Partida> partidasGanadasPorEquipo(String equipo) throws DAOException {
        String limpio = limpiarTexto(equipo);

        ArrayList<Partida> resultado = new ArrayList<>();
        for (Partida p : cargar()) {

            if (p.getEstado() == EstadoPartida.FINALIZADA && p.getGanador().equalsIgnoreCase(limpio)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public int contarPartidasJugadas(String equipo) throws DAOException {
        String limpio = limpiarTexto(equipo);

        int jugadas = 0;
        for (Partida p : cargar()) {
            if (p.getEstado() == EstadoPartida.FINALIZADA
                    && (p.getEquipo1().equalsIgnoreCase(limpio) || p.getEquipo2().equalsIgnoreCase(limpio))) {
                jugadas++;
            }
        }
        return jugadas;
    }


}
