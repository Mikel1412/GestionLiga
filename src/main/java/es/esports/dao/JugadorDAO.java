package es.esports.dao;

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

import java.util.List;

/**
 * Contrato para gestionar jugadores. Solo dice QUÉ se puede hacer;
 * CÓMO se guardan (JSON, base de datos...) lo decide cada implementación.
 * <p>
 * La clave de un jugador es su nickname, que se compara sin distinguir mayúsculas
 * ni espacios al principio o al final. Un jugador sin equipo es un agente libre.
 * Cada equipo admite como máximo 5 titulares y 2 sustitutos.
 */
public interface JugadorDAO {

    // ---------- CRUD básico ----------

    /**
     * Registra un jugador nuevo. Debe llegar sin equipo (como agente libre):
     * para asignarle uno se usa {@link #ficharPorEquipo}.
     *
     * @param jugador el jugador a guardar; no puede ser {@code null} ni tener equipo
     * @throws DuplicadoException       si ya existe un jugador con ese nickname
     * @throws DAOException             si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException si el jugador es {@code null} o ya tiene equipo
     */
    void crearJugador(Jugador jugador) throws DuplicadoException, DAOException;

    /**
     * Busca un jugador por su nickname.
     *
     * @param nickname nickname del jugador; no puede ser nulo ni vacío
     * @return el jugador
     * @throws NoEncontradoException    si no existe ningún jugador con ese nickname
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si el nickname es nulo o vacío
     */
    Jugador leerJugador(String nickname) throws NoEncontradoException, DAOException;

    /**
     * Devuelve todos los jugadores registrados.
     *
     * @return la lista de jugadores (vacía si no hay ninguno)
     * @throws DAOException si falla la lectura del fichero
     */
    List<Jugador> leerTodosJugadores() throws DAOException;

    /**
     * Sustituye los datos personales de un jugador ya existente (nombre, edad, roles...).
     * <p>
     * El jugador se identifica por su nickname, que no se puede cambiar. Tampoco cambia
     * su equipo ni su titularidad: se mantienen los guardados. Para eso están
     * {@link #ficharPorEquipo}, {@link #liberarJugador}, {@link #traspasarJugador}
     * y {@link #cambiarTitularidad}.
     *
     * @param jugador el jugador con los datos nuevos; no puede ser {@code null}
     * @throws NoEncontradoException    si no existe ningún jugador con ese nickname
     * @throws DAOException             si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException si el jugador es {@code null}
     */
    void actualizarJugador(Jugador jugador) throws NoEncontradoException, DAOException;

    /**
     * Elimina un jugador por su nickname.
     *
     * @param nickname nickname del jugador; no puede ser nulo ni vacío
     * @throws NoEncontradoException    si no existe ningún jugador con ese nickname
     * @throws DAOException             si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException si el nickname es nulo o vacío
     */
    void borrarJugador(String nickname) throws NoEncontradoException, DAOException;

    // ---------- Operaciones propias de la liga (no CRUD) ----------

    /**
     * Cuenta los jugadores registrados.
     *
     * @return el número de jugadores (0 si no hay ninguno)
     * @throws DAOException si falla la lectura del fichero
     */
    int contarJugadores() throws DAOException;

    /**
     * Devuelve los agentes libres, es decir, los jugadores sin equipo.
     *
     * @return la lista de agentes libres (vacía si no hay ninguno)
     * @throws DAOException si falla la lectura del fichero
     */
    List<Jugador> jugadoresLibres() throws DAOException;

    /**
     * Devuelve los jugadores que juegan a un juego.
     *
     * @param juego el juego; no puede ser {@code null}
     * @return la lista de jugadores de ese juego (vacía si no hay ninguno)
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si {@code juego} es {@code null}
     */
    List<Jugador> buscarJugadorPorJuego(Juego juego) throws DAOException;

    /**
     * Ficha a un agente libre por un equipo. Entra como titular si el equipo tiene
     * menos de 5 titulares y como sustituto en caso contrario.
     *
     * @param nickname nickname del jugador; no puede ser nulo ni vacío
     * @param equipo   el equipo que lo ficha; no puede ser {@code null}
     * @throws NoEncontradoException      si no existe el jugador
     * @throws JugadorYaFichadoException  si el jugador ya tiene equipo
     * @throws JuegoIncompatibleException si el jugador y el equipo son de juegos distintos
     * @throws PlantillaLlenaException    si el equipo ya tiene 7 jugadores
     * @throws DAOException               si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException   si algún argumento es nulo o vacío
     */
    void ficharPorEquipo(String nickname, Equipo equipo)
            throws NoEncontradoException, JugadorYaFichadoException, JuegoIncompatibleException,
            PlantillaLlenaException, DAOException;

    /**
     * Deja libre a un jugador que tiene equipo (pierde también la titularidad).
     *
     * @param nickname nickname del jugador; no puede ser nulo ni vacío
     * @throws NoEncontradoException     si no existe el jugador
     * @throws JugadorSinEquipoException si el jugador ya es agente libre
     * @throws DAOException              si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException  si el nickname es nulo o vacío
     */
    void liberarJugador(String nickname)
            throws NoEncontradoException, JugadorSinEquipoException, DAOException;

    /**
     * Pasa a un jugador de su equipo actual a otro. Si el traspaso no es posible,
     * el jugador conserva su equipo y no se modifica nada.
     *
     * @param nickname    nickname del jugador; no puede ser nulo ni vacío
     * @param nuevoEquipo el equipo de destino; no puede ser {@code null}
     * @throws NoEncontradoException      si no existe el jugador
     * @throws JugadorSinEquipoException  si el jugador es agente libre (debe usarse {@link #ficharPorEquipo})
     * @throws JugadorYaFichadoException  si el jugador ya está en ese equipo
     * @throws JuegoIncompatibleException si el jugador y el equipo destino son de juegos distintos
     * @throws PlantillaLlenaException    si el equipo destino ya tiene 7 jugadores
     * @throws DAOException               si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException   si algún argumento es nulo o vacío
     */
    void traspasarJugador(String nickname, Equipo nuevoEquipo)
            throws NoEncontradoException, JugadorSinEquipoException, JugadorYaFichadoException,
            JuegoIncompatibleException, PlantillaLlenaException, DAOException;

    /**
     * Cambia un jugador de titular a sustituto, o al revés. Un equipo puede tener como máximo 5 titulares.
     *
     * @param nickname nickname del jugador; no puede ser nulo ni vacío
     * @param titular  {@code true} para hacerlo titular, {@code false} para hacerlo sustituto
     * @throws NoEncontradoException     si no existe el jugador
     * @throws JugadorSinEquipoException si el jugador es agente libre
     * @throws PlantillaLlenaException   si se le quiere hacer titular y el equipo ya tiene 5 titulares
     * @throws DAOException              si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException  si el nickname es nulo o vacío
     */
    void cambiarTitularidad(String nickname, boolean titular)
            throws NoEncontradoException, JugadorSinEquipoException, PlantillaLlenaException, DAOException;

    /**
     * Devuelve la plantilla de un equipo: titulares y sustitutos.
     *
     * @param equipo nombre del equipo; no puede ser nulo ni vacío
     * @return la lista de jugadores del equipo (vacía si no tiene ninguno)
     * @throws DAOException             si falla la lectura del fichero
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    List<Jugador> jugadoresDeEquipo(String equipo) throws DAOException;

    /**
     * Deja libres a todos los jugadores de un equipo. Se usa antes de borrar un equipo
     * para que ningún jugador quede apuntando a un equipo que ya no existe.
     *
     * @param equipo nombre del equipo; no puede ser nulo ni vacío
     * @throws DAOException             si falla la lectura o escritura del fichero
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    void liberarJugadoresDeEquipo(String equipo) throws DAOException;
}

