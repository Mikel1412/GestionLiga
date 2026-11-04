package es.esports.dao;

import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.excepciones.reglaNegocioException.*;
import es.esports.modelo.EstadoTorneo;
import es.esports.modelo.Juego;
import es.esports.modelo.Torneo;

import java.util.List;

public interface TorneoDAO {

        // ---------- CRUD ----------

        void crear(Torneo torneo) throws DuplicadoException, DAOException;


        Torneo leer(String nombre) throws NoEncontradoException, DAOException;


        List<Torneo> leerTodos() throws DAOException;


        void actualizar(Torneo torneo) throws NoEncontradoException, DAOException;


        void borrar(String nombre) throws NoEncontradoException, EstadoTorneoException, DAOException;

        // ---------- Operaciones propias (no CRUD) ----------

        /** Inscribe un equipo: debe existir, ser del juego del torneo y tener 5 titulares. */
        void inscribirEquipo(String nombreTorneo, String nombreEquipo)
                throws NoEncontradoException, JuegoIncompatibleException, JugadoresInsuficientesException,
                EquipoYaInscritoException, PlantillaLlenaException, EstadoTorneoException, DAOException;

        /** Inicia el torneo y genera las partidas todos contra todos. */
        void iniciarTorneo(String nombreTorneo)
                throws NoEncontradoException, EstadoTorneoException, EquiposInsuficientesException, DAOException;

        /** Finaliza el torneo; solo si todas sus partidas tienen resultado. */
        void finalizarTorneo(String nombreTorneo)
                throws NoEncontradoException, EstadoTorneoException, DAOException;

        /** Torneos de un juego. */
        List<Torneo> torneosPorJuego(Juego juego) throws DAOException;

        /** Torneos en un estado (por ejemplo, los que aún aceptan inscripciones). */
        List<Torneo> torneosPorEstado(EstadoTorneo estado) throws DAOException;
    }
