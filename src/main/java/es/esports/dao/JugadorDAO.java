package es.esports.dao;

import es.esports.excepciones.DAOException;
import es.esports.modelo.Jugador;

public interface JugadorDAO {
    public void crearJugador(Jugador jugador) throws DAOException;

    public void leerJugador(Jugador jugador);

    public void actualizarJugador(Jugador jugador);
    public void borrarJugador(Jugador jugador);

    //Operaciones no crud
    public void contarJugadores();

    public void JugadoresLibres();
}

