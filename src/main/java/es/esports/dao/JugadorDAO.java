package es.esports.dao;

import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.modelo.Juego;
import es.esports.modelo.Jugador;

import java.util.List;

public interface JugadorDAO {

    /*
    Operaciones CRUD Básicas
     */
    public void crearJugador(Jugador jugador) throws DuplicadoException, DAOException;

    public Jugador leerJugador(String nombre) throws NoEncontradoException, DAOException;

    List<Jugador> leerTodosJugadores() throws NoEncontradoException, DAOException;

    public void actualizarJugador(Jugador jugador) throws NoEncontradoException, DAOException;

    public void borrarJugador(String nomrbe) throws DAOException, NoEncontradoException;

    //Operaciones no crud

    public void contarJugadores() throws DAOException;

    public List<Jugador> jugadoresLibres() throws DAOException;

    public List<Jugador> buscarJugadorPorJuego(Juego juego) throws DAOException;
}

