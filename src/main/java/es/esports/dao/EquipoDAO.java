package es.esports.dao;

import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.modelo.Equipo;
import es.esports.modelo.Juego;

import java.util.List;
import java.util.Optional;

/**
 * Contrato para gestionar equipos. Solo dice QUÉ se puede hacer;
 * CÓMO se guardan (JSON, base de datos...) lo decide cada implementación.
 * <p>
 * La clave de un equipo es su nombre, y se compara sin distinguir mayúsculas
 * ni espacios al principio o al final.
 */
public interface EquipoDAO {

    // ---------- CRUD básico ----------

    /**
     * Registra un equipo nuevo.
     *
     * @param equipo el equipo a guardar; no puede ser {@code null}
     * @throws DuplicadoException   si ya existe un equipo con ese nombre
     * @throws DAOException         si falla la lectura o escritura del fichero
     * @throws NullPointerException si {@code equipo} es {@code null}
     */
    void crear(Equipo equipo) throws DuplicadoException, DAOException;

    /**
     * Busca un equipo por su nombre.
     *
     * @param nombre nombre del equipo; no puede ser nulo ni vacío
     * @return el equipo, o un {@code Optional} vacío si no existe
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    Optional<Equipo> leer (String nombre) throws DAOException;

    /**
     * Devuelve todos los equipos registrados.
     *
     * @return la lista de equipos (vacía si no hay ninguno)
     * @throws DAOException si falla la lectura del fichero
     */
    List<Equipo> leerTodos() throws DAOException;

    /**
     * Sustituye los datos de un equipo ya existente.
     * <p>
     * El equipo se identifica por su nombre, que es su clave, así que este metodo
     * no permite cambiarlo: sirve para modificar el tag, el juego o la organización.
     *
     * @param equipo el equipo con los datos nuevos; no puede ser {@code null}
     * @throws NoEncontradoException si no existe ningún equipo con ese nombre
     * @throws DAOException          si falla la lectura o escritura del fichero
     * @throws NullPointerException  si {@code equipo} es {@code null}
     */
    void actualizar(Equipo equipo) throws NoEncontradoException, DAOException;

    /**
     * Elimina un equipo por su nombre.
     *
     * @param nombre nombre del equipo; no puede ser nulo ni vacío
     * @throws NoEncontradoException    si no existe ningún equipo con ese nombre
     * @throws DAOException             si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    void borrar(String nombre) throws NoEncontradoException, DAOException;

    // ---------- Operaciones propias de la liga (no CRUD) ----------

    /**
     * Devuelve los equipos que compiten en un juego.
     *
     * @param juego el juego; no puede ser {@code null}
     * @return la lista de equipos de ese juego (vacía si no hay ninguno)
     * @throws DAOException         si falla la lectura del fichero
     * @throws NullPointerException si {@code juego} es {@code null}
     */
    List<Equipo> equiposPorJuego(Juego juego) throws DAOException;

    /**
     * Busca equipos cuyo nombre contenga el texto, sin distinguir mayúsculas.
     *
     * @param texto texto a buscar; no puede ser nulo ni vacío
     * @return la lista de coincidencias (vacía si no hay ninguna)
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si el texto es nulo o vacío
     */
    List<Equipo> buscarPorNombre(String texto) throws DAOException;

    /**
     * Devuelve todas las plantillas de una organización, de cualquier juego.
     *
     * @param organizacion nombre de la organización; no puede ser nulo ni vacío
     * @return la lista de equipos de esa organización (vacía si no hay ninguno)
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si la organización es nula o vacía
     */
    List<Equipo> equiposDeOrganizacion(String organizacion) throws DAOException;
}
