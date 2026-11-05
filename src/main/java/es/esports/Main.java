package es.esports;

import es.esports.dao.EquipoDAO;
import es.esports.dao.JugadorDAO;
import es.esports.dao.PartidaDAO;
import es.esports.dao.TorneoDAO;
import es.esports.dao.json.EquipoDAOJson;
import es.esports.dao.json.JugadorDAOJson;
import es.esports.dao.json.PartidaDAOJson;
import es.esports.dao.json.TorneoDAOJson;
import es.esports.excepciones.DAOException;
import es.esports.ui.Menu;

import java.io.File;

public class Main {
    static void main() {
        try {
            EquipoDAO equipoDAO = new EquipoDAOJson(new File("datos/equipos.json"));
            JugadorDAO jugadorDAO = new JugadorDAOJson(new File("datos/jugadores.json"));
            PartidaDAO partidaDAO = new PartidaDAOJson(new File("datos/partidas.json"));
            TorneoDAO torneoDAO = new TorneoDAOJson(new File("datos/torneos.json"), equipoDAO, jugadorDAO, partidaDAO);

            new Menu(jugadorDAO, equipoDAO, torneoDAO, partidaDAO).iniciar();
        } catch (DAOException e) {
            System.out.println("No se pudo iniciar la aplicación:");
        }
    }
}