package es.esports.dao;

import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.excepciones.reglaNegocioException.*;
import es.esports.modelo.EstadoTorneo;
import es.esports.modelo.Juego;
import es.esports.modelo.Torneo;

import java.util.List;

/**
 * Contrato para gestionar torneos. Solo dice QUÉ se puede hacer;
 * CÓMO se guardan lo decide cada implementación.
 * <p>
 * La clave de un torneo es su nombre, que se compara sin distinguir mayúsculas
 * ni espacios al principio o al final. Un torneo pasa por tres estados:
 * preparación (admite inscripciones), activo (con sus partidas generadas) y finalizado.
 */
public interface TorneoDAO {

        // ---------- CRUD básico ----------

        /**
         * Registra un torneo nuevo (queda en preparación).
         *
         * @param torneo el torneo a guardar; no puede ser {@code null}
         * @throws DuplicadoException       si ya existe un torneo con ese nombre
         * @throws DAOException             si falla la lectura o escritura del fichero
         * @throws IllegalArgumentException si {@code torneo} es {@code null}
         */
        void crear(Torneo torneo) throws DuplicadoException, DAOException;

        /**
         * Busca un torneo por su nombre.
         *
         * @param nombre nombre del torneo; no puede ser nulo ni vacío
         * @return el torneo
         * @throws NoEncontradoException    si no existe ningún torneo con ese nombre
         * @throws DAOException             si falla la lectura del fichero
         * @throws IllegalArgumentException si el nombre es nulo o vacío
         */
        Torneo leer(String nombre) throws NoEncontradoException, DAOException;

        /**
         * Devuelve todos los torneos registrados.
         *
         * @return la lista de torneos (vacía si no hay ninguno)
         * @throws DAOException si falla la lectura del fichero
         */
        List<Torneo> leerTodos() throws DAOException;

        /**
         * Sustituye los datos de un torneo ya existente. El torneo se identifica por su
         * nombre, que no se puede cambiar.
         *
         * @param torneo el torneo con los datos nuevos; no puede ser {@code null}
         * @throws NoEncontradoException    si no existe ningún torneo con ese nombre
         * @throws DAOException             si falla la lectura o escritura del fichero
         * @throws IllegalArgumentException si {@code torneo} es {@code null}
         */
        void actualizar(Torneo torneo) throws NoEncontradoException, DAOException;

        /**
         * Elimina un torneo y todas sus partidas. No se puede borrar un torneo activo.
         *
         * @param nombre nombre del torneo; no puede ser nulo ni vacío
         * @throws NoEncontradoException    si no existe ningún torneo con ese nombre
         * @throws EstadoTorneoException    si el torneo está activo
         * @throws DAOException             si falla la lectura o escritura de algún fichero
         * @throws IllegalArgumentException si el nombre es nulo o vacío
         */
        void borrar(String nombre) throws NoEncontradoException, EstadoTorneoException, DAOException;

        // ---------- Operaciones propias de la liga (no CRUD) ----------

        /**
         * Inscribe un equipo en un torneo. El equipo debe existir, jugar al mismo juego
         * que el torneo y tener 5 jugadores titulares.
         *
         * @param nombreTorneo nombre del torneo; no puede ser nulo ni vacío
         * @param nombreEquipo nombre del equipo; no puede ser nulo ni vacío
         * @throws NoEncontradoException         si no existe el torneo o el equipo
         * @throws JuegoIncompatibleException    si el equipo y el torneo son de juegos distintos
         * @throws JugadoresInsuficientesException si el equipo no tiene exactamente 5 titulares
         * @throws EquipoYaInscritoException     si el equipo ya está inscrito en el torneo
         * @throws PlantillaLlenaException       si el torneo ya tiene el máximo de equipos
         * @throws EstadoTorneoException         si el torneo no está en preparación
         * @throws DAOException                  si falla la lectura o escritura del fichero
         * @throws IllegalArgumentException      si algún nombre es nulo o vacío
         */
        void inscribirEquipo(String nombreTorneo, String nombreEquipo)
                throws NoEncontradoException, JuegoIncompatibleException, JugadoresInsuficientesException,
                EquipoYaInscritoException, PlantillaLlenaException, EstadoTorneoException, DAOException;

        /**
         * Inicia un torneo: pasa a activo y genera las partidas de todos contra todos
         * (cada pareja de equipos juega una vez).
         *
         * @param nombreTorneo nombre del torneo; no puede ser nulo ni vacío
         * @throws NoEncontradoException         si no existe el torneo
         * @throws EstadoTorneoException         si el torneo no está en preparación
         * @throws EquiposInsuficientesException si hay menos de 2 equipos inscritos
         * @throws DAOException                  si falla la lectura o escritura de algún fichero
         * @throws IllegalArgumentException      si el nombre es nulo o vacío
         */
        void iniciarTorneo(String nombreTorneo)
                throws NoEncontradoException, EstadoTorneoException, EquiposInsuficientesException, DAOException;

        /**
         * Finaliza un torneo activo, siempre que todas sus partidas tengan resultado.
         *
         * @param nombreTorneo nombre del torneo; no puede ser nulo ni vacío
         * @throws NoEncontradoException    si no existe el torneo
         * @throws EstadoTorneoException    si el torneo no está activo o aún tiene partidas pendientes
         * @throws DAOException             si falla la lectura o escritura de algún fichero
         * @throws IllegalArgumentException si el nombre es nulo o vacío
         */
        void finalizarTorneo(String nombreTorneo)
                throws NoEncontradoException, EstadoTorneoException, DAOException;

        /**
         * Devuelve los torneos de un juego.
         *
         * @param juego el juego; no puede ser {@code null}
         * @return la lista de torneos de ese juego (vacía si no hay ninguno)
         * @throws DAOException             si falla la lectura del fichero
         * @throws IllegalArgumentException si {@code juego} es {@code null}
         */
        List<Torneo> torneosPorJuego(Juego juego) throws DAOException;

        /**
         * Devuelve los torneos que están en un estado concreto
         * (por ejemplo, los que aún aceptan inscripciones).
         *
         * @param estado el estado; no puede ser {@code null}
         * @return la lista de torneos en ese estado (vacía si no hay ninguno)
         * @throws DAOException             si falla la lectura del fichero
         * @throws IllegalArgumentException si {@code estado} es {@code null}
         */
        List<Torneo> torneosPorEstado(EstadoTorneo estado) throws DAOException;
}
