package es.esports.dao;

import es.esports.excepciones.DAOException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.modelo.EstadoPartida;
import es.esports.modelo.Partida;

import java.util.ArrayList;
import java.util.List;

public interface PartidaDAO {

    void crear(Partida p) throws  DAOException;

    Partida leer(int id) throws NoEncontradoException, DAOException;

    List<Partida> leerTodos() throws DAOException;

    void actualizar(Partida p) throws  NoEncontradoException, DAOException;

    void borrar(int id ) throws NoEncontradoException, DAOException;

    List<Partida> partidasPorTorneo (String torneo) throws DAOException;

    List<Partida> partidasPorEquipo(String equipo) throws DAOException;

    List<Partida> partidasPorEstado(EstadoPartida estado) throws DAOException;

    boolean existePartida(int id) throws DAOException;

    Partida proximaPartida(String equipo) throws NoEncontradoException, DAOException;

    ArrayList<Partida> partidasGanadasPorEquipo(String equipo) throws DAOException;

    int contarPartidasJugadas(String equipo) throws DAOException;
}

