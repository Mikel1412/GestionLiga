package es.esports.dao;

import es.esports.excepciones.DAOException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.modelo.EstadoPartida;
import es.esports.modelo.Partida;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrato para gestionar partidas. Solo dice QUÉ se puede hacer;
 * CÓMO se guardan lo decide cada implementación.
 * <p>
 * A diferencia de equipos, jugadores y torneos, las partidas se identifican por un
 * id numérico que asigna el propio DAO al crearlas.
 */
public interface PartidaDAO {

    // ---------- CRUD básico ----------

    /**
     * Registra una partida nueva y le asigna su id (el mayor existente más uno).
     *
     * @param partida la partida a guardar; no puede ser {@code null}
     * @throws DAOException             si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException si la partida es {@code null}
     */
    void crear(Partida partida) throws DAOException;

    /**
     * Busca una partida por su id.
     *
     * @param id identificador de la partida
     * @return la partida
     * @throws NoEncontradoException si no existe ninguna partida con ese id
     * @throws DAOException          si falla la lectura del fichero
     */
    Partida leer(int id) throws NoEncontradoException, DAOException;

    /**
     * Devuelve todas las partidas registradas.
     *
     * @return la lista de partidas (vacía si no hay ninguna)
     * @throws DAOException si falla la lectura del fichero
     */
    List<Partida> leerTodos() throws DAOException;

    /**
     * Sustituye los datos de una partida ya existente (por ejemplo, al añadir
     * jugadores o registrar el resultado). Se identifica por su id.
     *
     * @param partida la partida con los datos nuevos; no puede ser {@code null}
     * @throws NoEncontradoException    si no existe ninguna partida con ese id
     * @throws DAOException             si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException si la partida es {@code null}
     */
    void actualizar(Partida partida) throws NoEncontradoException, DAOException;

    /**
     * Elimina una partida por su id.
     *
     * @param id identificador de la partida
     * @throws NoEncontradoException si no existe ninguna partida con ese id
     * @throws DAOException          si falla la lectura o escritura del fichero
     */
    void borrar(int id) throws NoEncontradoException, DAOException;

    // ---------- Operaciones propias de la liga (no CRUD) ----------

    /**
     * Devuelve las partidas de un torneo.
     *
     * @param torneo nombre del torneo; no puede ser nulo ni vacío
     * @return la lista de partidas del torneo (vacía si no tiene ninguna)
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    List<Partida> partidasDeTorneo(String torneo) throws DAOException;

    /**
     * Devuelve las partidas en las que juega un equipo, ya sea como equipo 1 o como equipo 2.
     *
     * @param equipo nombre del equipo; no puede ser nulo ni vacío
     * @return la lista de partidas del equipo (vacía si no tiene ninguna)
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    List<Partida> partidasPorEquipo(String equipo) throws DAOException;

    /**
     * Devuelve las partidas que están en un estado (pendientes o finalizadas).
     *
     * @param estado el estado; no puede ser {@code null}
     * @return la lista de partidas en ese estado (vacía si no hay ninguna)
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si {@code estado} es {@code null}
     */
    List<Partida> partidasPorEstado(EstadoPartida estado) throws DAOException;

    boolean existePartida(int id) throws DAOException;

    /**
     * Devuelve la próxima partida pendiente de un equipo.
     *
     * @param equipo nombre del equipo; no puede ser nulo ni vacío
     * @return la próxima partida pendiente del equipo
     * @throws NoEncontradoException    si el equipo no tiene ninguna partida pendiente
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    Partida proximaPartida(String equipo) throws NoEncontradoException, DAOException;

    /**
     * Devuelve las partidas que ha ganado un equipo.
     *
     * @param equipo nombre del equipo; no puede ser nulo ni vacío
     * @return la lista de partidas ganadas (vacía si no ha ganado ninguna)
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    List<Partida> partidasGanadasPorEquipo(String equipo) throws DAOException;

    /**
     * Cuenta las partidas ya finalizadas en las que ha participado un equipo.
     *
     * @param equipo nombre del equipo; no puede ser nulo ni vacío
     * @return el número de partidas jugadas (0 si no ha jugado ninguna)
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    int contarPartidasJugadas(String equipo) throws DAOException;
}

