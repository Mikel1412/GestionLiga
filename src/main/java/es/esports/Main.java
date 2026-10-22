package es.esports;

import es.esports.dao.JugadorDAO;
import es.esports.dao.json.JugadorDAOJson;
import es.esports.excepciones.DAOException;
import es.esports.excepciones.reglaNegocioException.RolInvalidoException;
import es.esports.modelo.Juego;
import es.esports.modelo.Jugador;
import es.esports.modelo.Rol;

import java.io.IOException;

public class Main {
    static void main() throws RolInvalidoException {
        Jugador jugador1 = new Jugador
                ("Daniel", "Denyshchuk", "Rusu", "Zomboo", 18, Juego.Valorant, Rol.DUELISTA);
        JugadorDAO jugadorDAO = new JugadorDAOJson();

        try{
            jugadorDAO.crearJugador(jugador1);
            System.out.println("Jugador Creado");
        } catch (DAOException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
