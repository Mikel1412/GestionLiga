package es.esports.dao.json;

import es.esports.dao.EquipoDAO;
import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.modelo.Equipo;
import es.esports.modelo.Juego;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EquipoDAOJson implements EquipoDAO {

    //HEMOS MODULARIZADO. Para no repetir bvarias veces lo de cargar y guardar de un json.

    private final File fichero;   // por ejemplo new File("datos/equipos.json")

    public EquipoDAOJson(File fichero) throws DAOException {
        if (fichero == null) {
            throw new IllegalArgumentException("La ruta del fichero no puede ser null");
        }
        this.fichero = fichero;

        // Primera vez: el fichero no existe, lo creamos con una lista vacía
        if (!fichero.exists()) {
            guardar(new ArrayList<>());
        }
    }

    // ---------- LEER: fichero -> lista de Equipo ----------
    // UTILIZAMOS ESTE METODO PARA PODER CARGAR LOS DATOS DE EQUIPO, PORQUE A LA HORA DE CERRAR EL PROGRAMA NO HAY PERSISTENCIA DE NINGUN TIPO DE DATOS
    // POR LO QUE CARGAREMOS LOS DATOS DESDE UN FICHERO JSON PARA MANTENER EL PROGRAMA AL TANTO.

    private List<Equipo> cargar() throws DAOException {
        // Lo que se abre dentro del paréntesis del try se cierra solo al terminar. (nuestro Try-with-resources).
        try (FileReader lectorFichero = new FileReader(fichero, StandardCharsets.UTF_8);
             JsonReader lectorJson = Json.createReader(lectorFichero)) {

            JsonArray array = lectorJson.readArray();
            List<Equipo> equipos = new ArrayList<>();

            // Cada elemento del array es un { ... } del JSON
            for (JsonObject o : array.getValuesAs(JsonObject.class)) {
                Equipo equipo = new Equipo(
                        o.getString("nombre"),
                        o.getString("tag"),
                        Juego.valueOf(o.getString("juego")),   // el texto "VALORANT" -> Juego.VALORANT
                        o.getString("organizacion"));
                equipos.add(equipo);
            }
            return equipos;

        } catch (IOException | RuntimeException e) {
            // Fichero no legible, JSON mal escrito, campo que falta o dato que no pasa la validación
            throw new DAOException("No se pudo leer el fichero " + fichero, e);
        }
    }


    // ---------- ESCRIBIR: lista de Equipo -> fichero ----------


    private void guardar(List<Equipo> equipos) throws DAOException {

        // 1. Construir el array JSON, equipo por equipo
        JsonArrayBuilder array = Json.createArrayBuilder();
        for (Equipo e : equipos) {
            array.add(Json.createObjectBuilder()
                    .add("nombre", e.getNombre())
                    .add("tag", e.getTag())
                    .add("juego", e.getJuego().name())   // el enum se guarda como texto
                    .add("organizacion", e.getOrganizacion()));
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

    // ESTE METODO LO UTILIZAMOS PARA EVITAR DUPLICADOS EN CREAR
    // Los equipos están en una lista en memoria, y cada uno ocupa un lugar numerado desde 0.
    // Para modificar o borrar uno concreto, hay que saber en qué lugar está.

    private int buscarPosicion(List<Equipo> equipos, String nombre) {
        for (int i = 0; i < equipos.size(); i++) {
            if (equipos.get(i).getNombre().equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        return -1;
    }

    // ESTE METODO ES PARA LA PARTE DE COMPROBACIÓN DE ARGUMENTOS (Para no pasar argumentos erroneos).
    // ESTE METODO VALIDA LOS ARGUMENTOS DE TEXTO QUE LLEGAN DE FUERA (como leer, borrar, buscarPorNombre... etc)

    private String limpiarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }
        return texto.trim();
    }

    @Override
    public void crear(Equipo equipo) throws DuplicadoException, DAOException {

        //El dato a introducir no puede ser null, osea que lanzariamos una excepcion en caso de que si fuera null.
        if (equipo==null) {
            throw new IllegalArgumentException("No puede ser NULO el equipo");
        }

        //Primero tenemos que obtener la lista de los equipos que ya haya guardados
        List<Equipo> equipos = cargar();

        //Ahora buscaremos si existe un equipo ya con este nombre
        int posicion = buscarPosicion(equipos,equipo.getNombre());

        //Ahora, si la posicion no es -1, ya existe.
        if (posicion != -1) {
            throw new DuplicadoException("equipo " +equipo.getNombre());
        }

        //Añadimos el equipo a la lista, y para que no quede solo en la memoria,
        //llamamos al metodo guardar para que guarde nuevamente la lista equipos en un fichero JSON.
        equipos.add(equipo);
        guardar(equipos);
        System.out.println("Equipo creado correctamente");
    }

    @Override
    public Equipo leer(String nombre) throws NoEncontradoException, DAOException {

        //Para leer los detalles del equipo, tenemos que identificarlo por su nombre, pero el nombre podria tener espacios sobrantes por los lados, para eso utilizamos limpiarTexto
        //No hace falta el if (nombre == null) porque ya lo comprobamos todoo esto en limpiar texto.
        String buscado = limpiarTexto(nombre);

        //Ahora crearemos una Lista en este metodo y le pasaremos los equipos con el metodo cargar.
        List<Equipo> equipos = cargar();

        //Ahora buscaremos la posicion del equipo para ver si comprobar si este esta en la lista, si no esta lanzaremos un error.
        int posicion = buscarPosicion(equipos, nombre);
        if (posicion == -1) {
            throw new NoEncontradoException("El equipo no existe en la lista");
        }

        //Si todoo ha salido bien, para devolver el equipo, utilizaremos la posicion donde esta este equipo en la lista, y le hacemos return.
        return equipos.get(posicion);
    }

    @Override
    public List<Equipo> leerTodos() throws DAOException {
        //Como simplemente tenemos que leer todos los datos de los equipos, pues simplemente devolvemos el metodo cargar.
        return cargar();
    }

    @Override
    public void actualizar(Equipo equipo) throws NoEncontradoException, DAOException {

        //El dato a introducir no puede ser null, osea que lanzariamos una excepcion en caso de que si fuera null.
        if (equipo==null) {
            throw new IllegalArgumentException("No puede ser NULO el equipo");
        } else {
            //Primero cargamos los equipos
            List<Equipo> equipos = cargar();

            int posicion = buscarPosicion(equipos, equipo.getNombre());

            //Comprobamos que posicion no sea -1.
            if (posicion == -1) {
                throw new NoEncontradoException("No se ha encontrado el equipo en la lista");
            }

            //En caso de existir la posicion de donde esta el equipo, sustituimos el equipo viejo por el nuevo.
            equipos.set(posicion,equipo);

            //Y volvemos a guardar la lista de equipos para la persistencia.
            guardar(equipos);
        }
    }

    @Override
    public void borrar(String nombre) throws NoEncontradoException, DAOException {

        //Limpiamos el texto introducido en el metodo.
        //No hace falta el if (nombre == null) porque ya lo comprobamos todoo esto en limpiar texto.
        String textoLimpio = limpiarTexto(nombre);

        //Crearemos un booleano para ver si se ha encontrado lo que queriamos borrar o no.
        boolean encontrado = false;

        //Primero cargamos los equipos
        List<Equipo> equipos = cargar();

        //Hacemos un iterador de la lista equipos para poder borrar de la manera correcta el objeto.
        Iterator<Equipo> it = equipos.iterator();

        while (it.hasNext()) {
            Equipo e = it.next();
            if (e.getNombre().equalsIgnoreCase(textoLimpio)) {
                it.remove();
                encontrado = true;
            }
        }

        //Si no se ha encontrado ningun objeto con el nombre del equipo introducido, es que no lo hemos encontrado (no ha pasado por el if del iterator).
        if (!encontrado) {
            throw new NoEncontradoException("No se ha encontrado el equipo para borrar");
        }

        //Utilizamos el metodo guardar equipos para guardar los cambios del equipo borrado en la persistencia.
        guardar(equipos);

    }

    @Override
    public List<Equipo> equiposPorJuego(Juego juego) throws DAOException {

        //El dato a introducir no puede ser null, osea que lanzariamos una excepcion en caso de que si fuera null.
        if (juego == null) {
            throw new IllegalArgumentException("El juego no puede ser null");
        }

        //Cargamos la lista.
        List<Equipo> equipos = cargar();

        //Hacemos una nueva lista para luego devolver los equipos que compiten en esos juegos.
        List<Equipo> equiposJuego = new ArrayList<>();

        //For-each para introducir los datos en equiposJuego
        for (Equipo e : equipos) {
            if (e.getJuego()==juego) {
                equiposJuego.add(e);
            }
        }

        return equiposJuego;
    }

    @Override
    public List<Equipo> buscarPorNombre(String texto) throws DAOException {

        //Para buscar un equipo por nombre, primero tendremos que limpiar lo que podría tener espacios sobrantes por los lados, para eso utilizamos limpiarTexto
        //No hace falta el if (texto == null) porque ya lo comprobamos todoo esto en limpiar texto.
        String limpio = limpiarTexto(texto).toLowerCase();

        //Cargamos los equipos
        List<Equipo> equipos = cargar();

        //Nueva lista para los equipos identificados con algo del texto introducido en su nombre.
        List<Equipo> equiposCoincidencias = new ArrayList<>();

        //Recorremos los equipos con un for-each para buscar por el texto.
        //Pondremos el toLowerCase para que no pase por alto alguna mayuscula y poder comparar de una manera mas facil.
        for (Equipo e : equipos) {
            if (e.getNombre().toLowerCase().contains(limpio)) {
                equiposCoincidencias.add(e);
            }
        }

        return equiposCoincidencias;
    }

    @Override
    public List<Equipo> equiposDeOrganizacion(String organizacion) throws DAOException {

        //Para buscar un equipos por organizacion, primero tendremos que limpiar lo que podría tener espacios sobrantes por los lados, para eso utilizamos limpiarTexto
        //No hace falta el if (organizacion == null) porque ya lo comprobamos todoo esto en limpiar texto.
        String limpio = limpiarTexto(organizacion);

        //Cargamos los equipos
        List<Equipo> equipos = cargar();

        //Nueva lista para los equipos identificados con la misma organizacion.
        List<Equipo> equiposOrganizacion = new ArrayList<>();

        //Recorremos los equipos con un for-each para buscar los equipos por organizacion.
        for (Equipo e : equipos) {
            if (e.getOrganizacion().equalsIgnoreCase(limpio)) {
                equiposOrganizacion.add(e);
            }
        }

        return equiposOrganizacion;
    }
}
