package es.esports.ui;

import es.esports.dao.EquipoDAO;
import es.esports.dao.JugadorDAO;
import es.esports.dao.PartidaDAO;
import es.esports.dao.TorneoDAO;

import java.util.Scanner;

public class Menu {
    private final Scanner sc = new Scanner(System.in);

    private final JugadorDAO jugadorDAO;
    private final EquipoDAO equipoDAO;
    private final TorneoDAO torneoDAO;
    private final PartidaDAO partidaDAO;

    public Menu(JugadorDAO jugadorDAO, EquipoDAO equipoDAO, TorneoDAO torneoDAO, PartidaDAO partidaDAO){
        this.jugadorDAO = jugadorDAO;
        this.equipoDAO = equipoDAO;
        this.torneoDAO = torneoDAO;
        this.partidaDAO = partidaDAO;
    }


    //---------------Menu Principal---------------

    private void iniciar() {
        int opcion;
        do{
            System.out.println("=== GESTIÓN DE LIGA ===");
            System.out.println("1. Jugadores");
            System.out.println("2. Equipos");
            System.out.println("3. Torneos");
            System.out.println("4. Partidas");
            System.out.println("0. Salir");
            opcion = leerEntero("Opción");

            switch (opcion) {
                case 1 -> menuJugadores();
                case 2 -> menuEquipos();
                case 3 -> menuTorneos();
                case 4 -> menuPartidas();
                case 0 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    // =====================================================================
    //  JUGADORES
    // =====================================================================

    private void menuJugadores() {
        int opcion;
        do {
            System.out.println();
            System.out.println("--- JUGADORES ---");
            System.out.println(" 1. Crear jugador");
            System.out.println(" 2. Ver un jugador");
            System.out.println(" 3. Listar todos");
            System.out.println(" 4. Cambiar edad");
            System.out.println(" 5. Cambiar roles");
            System.out.println(" 6. Marcar titular / suplente");
            System.out.println(" 7. Borrar jugador");
            System.out.println(" 8. Fichar por un equipo");
            System.out.println(" 9. Liberar (dejar libre)");
            System.out.println("10. Traspasar a otro equipo");
            System.out.println("11. Agentes libres");
            System.out.println("12. Jugadores de un juego");
            System.out.println("13. Plantilla de un equipo");
            System.out.println("14. Contar jugadores");
            System.out.println(" 0. Volver");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> ejecutar(this::crearJugador);
                case 2 -> ejecutar(this::verJugador);
                case 3 -> ejecutar(this::listarJugadores);
                case 4 -> ejecutar(this::cambiarEdadJugador);
                case 5 -> ejecutar(this::cambiarRolesJugador);
                case 6 -> ejecutar(this::marcarTitular);
                case 7 -> ejecutar(this::borrarJugador);
                case 8 -> ejecutar(this::ficharJugador);
                case 9 -> ejecutar(this::liberarJugador);
                case 10 -> ejecutar(this::traspasarJugador);
                case 11 -> ejecutar(this::jugadoresLibres);
                case 12 -> ejecutar(this::jugadoresPorJuego);
                case 13 -> ejecutar(this::plantillaDeEquipo);
                case 14 -> ejecutar(jugadorDAO::contarJugadores);
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    // =============  LECTURA POR CONSOLA ====================

    /** Pide un entero y repite hasta que se escribe uno válido. */
    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número entero.");
            }
        }
    }
}