package es.esports.dao;

import es.esports.excepciones.DAOException;
import es.esports.modelo.Jugador;

import java.io.IOException;

public interface JugadorDAO {
    public void crearJugador(Jugador jugador) throws DAOException, IOException;

    public void leerJugador(Jugador jugador) throws DAOException, IOException;

    public void actualizarJugador(Jugador jugador);
    public void borrarJugador(Jugador jugador);

    //Operaciones no crud
    public void contarJugadores();

    public void JugadoresLibres();
}

