package es.esports.dao;

import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.modelo.EstadoPartida;
import es.esports.modelo.Partida;

import java.util.List;

public interface PartidaDAO {

    void crear(Partida p) throws DuplicadoException, DAOException;

    void leer(int id) throws NoEncontradoException, DAOException;

    List<Partida> leerTodos() throws DAOException;

    void actualizar(Partida p) throws  NoEncontradoException, DAOException;

    void borrar(int id ) throws NoEncontradoException, DAOException;

    List<Partida> patidasPorTorneo (String torneo) throws DAOException;

    List<Partida> partidasPorEquipo(String equipo) throws DAOException;

    List<Partida> partidasPorEstado(EstadoPartida estado) throws DAOException;
}
