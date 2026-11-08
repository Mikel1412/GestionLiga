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

public interface JugadorDAO {

    /* Operaciones CRUD Básicas */
    void crearJugador(Jugador jugador) throws DuplicadoException, DAOException;

    Jugador leerJugador(String nickname) throws NoEncontradoException, DAOException;

    List<Jugador> leerTodosJugadores() throws DAOException;

    void actualizarJugador(Jugador jugador) throws NoEncontradoException, DAOException;

    void borrarJugador(String nickname) throws NoEncontradoException, DAOException;

    // Operaciones no CRUD
    int contarJugadores() throws DAOException;
    // ahora DEVUELVE el número
    List<Jugador> jugadoresLibres() throws DAOException;

    List<Jugador> buscarJugadorPorJuego(Juego juego) throws DAOException;

    void ficharPorEquipo(String nickname, Equipo equipo) throws NoEncontradoException, JugadorYaFichadoException, JuegoIncompatibleException, PlantillaLlenaException, DAOException;

    void liberarJugador(String nickname) throws NoEncontradoException, JugadorSinEquipoException, DAOException;

    void traspasarJugador(String nickname, Equipo nuevoEquipo) throws NoEncontradoException, JugadorSinEquipoException, JugadorYaFichadoException, JuegoIncompatibleException, PlantillaLlenaException, DAOException;

    List<Jugador> jugadoresDeEquipo(String equipo) throws DAOException;

    //Este lo necesita el menú al borrar un equipo (los jugadores quedan libres)
    void liberarJugadoresDeEquipo(String equipo) throws DAOException;

    void cambiarTitularidad(String nickname, boolean titular) throws NoEncontradoException, JugadorSinEquipoException, PlantillaLlenaException, DAOException;
}

