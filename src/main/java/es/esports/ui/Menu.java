package es.esports.ui;

import es.esports.dao.EquipoDAO;
import es.esports.dao.JugadorDAO;
import es.esports.dao.PartidaDAO;
import es.esports.dao.TorneoDAO;
import es.esports.excepciones.DAOException;
import es.esports.excepciones.DuplicadoException;
import es.esports.excepciones.NoEncontradoException;
import es.esports.excepciones.reglaNegocioException.ReglaDeNegocioException;
import es.esports.modelo.*;
import es.esports.sistema.GestorFicheros;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Menú de consola. Solo pide datos, llama a los DAO y muestra resultados:
 * las reglas de negocio viven en el modelo y en los DAO, no aquí.
 */
public class Menu {

    // Una acción del menú: puede lanzar cualquier excepción, que ejecutar() se encarga de mostrar.
    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws Exception;
    }

    private final Scanner sc = new Scanner(System.in);   // un único Scanner para todo el programa

    private final JugadorDAO jugadorDAO;
    private final EquipoDAO equipoDAO;
    private final TorneoDAO torneoDAO;
    private final PartidaDAO partidaDAO;

    public Menu(JugadorDAO jugadorDAO, EquipoDAO equipoDAO, TorneoDAO torneoDAO, PartidaDAO partidaDAO) {
        this.jugadorDAO = jugadorDAO;
        this.equipoDAO = equipoDAO;
        this.torneoDAO = torneoDAO;
        this.partidaDAO = partidaDAO;
    }

    // =====================================================================
    //  MENÚ PRINCIPAL
    // =====================================================================

    public void iniciar() {
        int opcion;
        do {
            System.out.println();
            System.out.println("=== GESTIÓN DE LIGA ===");
            System.out.println("1. Jugadores");
            System.out.println("2. Equipos");
            System.out.println("3. Torneos");
            System.out.println("4. Partidas");
            System.out.println("5. Ficheros");
            System.out.println("0. Salir");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> menuJugadores();
                case 2 -> menuEquipos();
                case 3 -> menuTorneos();
                case 4 -> menuPartidas();
                case 5 -> menuFicheros();
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

    private void crearJugador() throws Exception {
        String nombre = leerTexto("Nombre: ");
        String apellido1 = leerTexto("Primer apellido: ");
        System.out.print("Segundo apellido (Enter para dejarlo vacío): ");
        String apellido2 = sc.nextLine();
        String nickname = leerTexto("Nickname: ");
        int edad = leerEntero("Edad: ");
        Juego juego = leerEnum("Juego:", Juego.values());

        // Los roles dependen del juego, por eso se pide el juego antes.
        Rol rol1 = leerRol("Rol principal:", juego);
        Rol rol2 = null;
        if (juego == Juego.League_of_Legends) {
            rol2 = leerRol("Segundo rol (obligatorio en League of Legends):", juego);
        } else if (confirmar("¿Quieres indicar un segundo rol?")) {
            rol2 = leerRol("Segundo rol:", juego);
        }

        // Todo jugador nace libre: sin equipo. Después se ficha con la opción 8.
        Jugador jugador = new Jugador(nombre, apellido1, apellido2, nickname, edad, juego, rol1, rol2, null);
        jugadorDAO.crearJugador(jugador);
    }

    private void verJugador() throws Exception {
        System.out.println(jugadorDAO.leerJugador(leerTexto("Nickname: ")));
    }

    private void listarJugadores() throws Exception {
        mostrarLista("Jugadores", jugadorDAO.leerTodosJugadores());
    }

    private void cambiarEdadJugador() throws Exception {
        Jugador jugador = jugadorDAO.leerJugador(leerTexto("Nickname: "));
        jugador.setEdad(leerEntero("Nueva edad (ahora " + jugador.getEdad() + "): "));
        jugadorDAO.actualizarJugador(jugador);
        System.out.println("Edad actualizada.");
    }

    private void cambiarRolesJugador() throws Exception {
        Jugador jugador = jugadorDAO.leerJugador(leerTexto("Nickname: "));
        Juego juego = jugador.getJuego();
        System.out.println("Roles actuales: " + jugador.getRol1()
                + (jugador.getRol2() != null ? " / " + jugador.getRol2() : ""));

        Rol rol1 = leerRol("Nuevo rol principal:", juego);
        Rol rol2 = null;
        if (juego == Juego.League_of_Legends) {
            rol2 = leerRol("Nuevo segundo rol:", juego);
        } else if (confirmar("¿Quieres indicar un segundo rol?")) {
            rol2 = leerRol("Nuevo segundo rol:", juego);
        }

        jugador.setRoles(rol1, rol2);   // valida juego, roles repetidos, etc.
        jugadorDAO.actualizarJugador(jugador);
        System.out.println("Roles actualizados.");
    }

    private void marcarTitular() throws Exception {
        Jugador jugador = jugadorDAO.leerJugador(leerTexto("Nickname: "));
        if (jugador.isAgenteLibre()) {
            System.out.println("Es agente libre: sin equipo no puede ser titular.");
            return;
        }
        boolean titular = confirmar("¿Marcar a " + jugador.getNickname() + " como titular de "
                + jugador.getEquipo() + "? (n = suplente)");
        jugador.setTitular(titular);
        jugadorDAO.actualizarJugador(jugador);
        System.out.println(jugador.getNickname() + (titular ? " es titular." : " es suplente."));
    }

    private void borrarJugador() throws Exception {
        String nickname = leerTexto("Nickname del jugador a borrar: ");
        if (confirmar("¿Seguro que quieres borrar a " + nickname + "?")) {
            jugadorDAO.borrarJugador(nickname);
            System.out.println("Jugador borrado.");
        }
    }

    private void ficharJugador() throws Exception {
        String nickname = leerTexto("Nickname del jugador: ");
        Equipo equipo = equipoDAO.leer(leerTexto("Nombre del equipo: "));
        jugadorDAO.ficharPorEquipo(nickname, equipo);
        System.out.println(nickname + " ficha por " + equipo.getNombre() + ".");
    }

    private void liberarJugador() throws Exception {
        String nickname = leerTexto("Nickname del jugador: ");
        jugadorDAO.liberarJugador(nickname);
        System.out.println(nickname + " es ahora agente libre.");
    }

    private void traspasarJugador() throws Exception {
        String nickname = leerTexto("Nickname del jugador: ");
        Equipo destino = equipoDAO.leer(leerTexto("Nombre del equipo destino: "));
        jugadorDAO.transpasarJugador(nickname, destino);
        System.out.println(nickname + " traspasado a " + destino.getNombre() + ".");
    }

    private void jugadoresLibres() throws Exception {
        mostrarLista("Agentes libres", jugadorDAO.jugadoresLibres());
    }

    private void jugadoresPorJuego() throws Exception {
        Juego juego = leerEnum("Juego:", Juego.values());
        mostrarLista("Jugadores de " + juego, jugadorDAO.buscarJugadorPorJuego(juego));
    }

    private void plantillaDeEquipo() throws Exception {
        String equipo = leerTexto("Nombre del equipo: ");
        mostrarLista("Plantilla de " + equipo, jugadorDAO.jugadoresDeEquipo(equipo));
    }

    // =====================================================================
    //  EQUIPOS
    // =====================================================================

    private void menuEquipos() {
        int opcion;
        do {
            System.out.println();
            System.out.println("--- EQUIPOS ---");
            System.out.println("1. Crear equipo");
            System.out.println("2. Ver un equipo");
            System.out.println("3. Listar todos");
            System.out.println("4. Cambiar tag y organización");
            System.out.println("5. Borrar equipo");
            System.out.println("6. Equipos de un juego");
            System.out.println("7. Buscar por nombre");
            System.out.println("8. Equipos de una organización");
            System.out.println("0. Volver");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> ejecutar(this::crearEquipo);
                case 2 -> ejecutar(this::verEquipo);
                case 3 -> ejecutar(this::listarEquipos);
                case 4 -> ejecutar(this::actualizarEquipo);
                case 5 -> ejecutar(this::borrarEquipo);
                case 6 -> ejecutar(this::equiposPorJuego);
                case 7 -> ejecutar(this::buscarEquipos);
                case 8 -> ejecutar(this::equiposDeOrganizacion);
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void crearEquipo() throws Exception {
        String nombre = leerTexto("Nombre: ");
        String tag = leerTexto("Tag (2 a 5 caracteres): ");
        Juego juego = leerEnum("Juego:", Juego.values());
        String organizacion = leerTexto("Organización: ");
        equipoDAO.crear(new Equipo(nombre, tag, juego, organizacion));
        System.out.println("Equipo creado.");
    }

    private void verEquipo() throws Exception {
        System.out.println(equipoDAO.leer(leerTexto("Nombre del equipo: ")));
    }

    private void listarEquipos() throws Exception {
        mostrarLista("Equipos", equipoDAO.leerTodos());
    }

    private void actualizarEquipo() throws Exception {
        Equipo equipo = equipoDAO.leer(leerTexto("Nombre del equipo: "));
        System.out.println("Actual: " + equipo);
        equipo.setTag(leerTexto("Nuevo tag: "));
        equipo.setOrganizacion(leerTexto("Nueva organización: "));
        equipoDAO.actualizar(equipo);
        System.out.println("Equipo actualizado.");
    }

    private void borrarEquipo() throws Exception {
        String nombre = leerTexto("Nombre del equipo a borrar: ");
        if (confirmar("¿Seguro que quieres borrar " + nombre + "?")) {
            equipoDAO.borrar(nombre);
            System.out.println("Equipo borrado.");
        }
    }

    private void equiposPorJuego() throws Exception {
        Juego juego = leerEnum("Juego:", Juego.values());
        mostrarLista("Equipos de " + juego, equipoDAO.equiposPorJuego(juego));
    }

    private void buscarEquipos() throws Exception {
        String texto = leerTexto("Texto a buscar en el nombre: ");
        mostrarLista("Coincidencias", equipoDAO.buscarPorNombre(texto));
    }

    private void equiposDeOrganizacion() throws Exception {
        String organizacion = leerTexto("Organización: ");
        mostrarLista("Equipos de " + organizacion, equipoDAO.equiposDeOrganizacion(organizacion));
    }

    // =====================================================================
    //  TORNEOS
    // =====================================================================

    private void menuTorneos() {
        int opcion;
        do {
            System.out.println();
            System.out.println("--- TORNEOS ---");
            System.out.println("1. Crear torneo");
            System.out.println("2. Ver un torneo");
            System.out.println("3. Listar todos");
            System.out.println("4. Borrar torneo");
            System.out.println("5. Inscribir un equipo");
            System.out.println("6. Iniciar torneo (genera las partidas)");
            System.out.println("7. Finalizar torneo");
            System.out.println("8. Torneos de un juego");
            System.out.println("9. Torneos por estado");
            System.out.println("0. Volver");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> ejecutar(this::crearTorneo);
                case 2 -> ejecutar(this::verTorneo);
                case 3 -> ejecutar(this::listarTorneos);
                case 4 -> ejecutar(this::borrarTorneo);
                case 5 -> ejecutar(this::inscribirEquipo);
                case 6 -> ejecutar(this::iniciarTorneo);
                case 7 -> ejecutar(this::finalizarTorneo);
                case 8 -> ejecutar(this::torneosPorJuego);
                case 9 -> ejecutar(this::torneosPorEstado);
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void crearTorneo() throws Exception {
        String nombre = leerTexto("Nombre: ");
        Juego juego = leerEnum("Juego:", Juego.values());
        Formato formato = leerEnum("Formato:", Formato.values());
        String fechaInicio = leerTexto("Fecha de inicio (aaaa-mm-dd): ");
        String fechaFin = leerTexto("Fecha de fin (aaaa-mm-dd): ");
        int maxEquipos = leerEntero("Máximo de equipos: ");
        torneoDAO.crear(new Torneo(nombre, juego, formato, fechaInicio, fechaFin, maxEquipos));
        System.out.println("Torneo creado.");
    }

    private void verTorneo() throws Exception {
        Torneo torneo = torneoDAO.leer(leerTexto("Nombre del torneo: "));
        System.out.println(torneo);
        System.out.println("Equipos inscritos: " + (torneo.getEquipos().isEmpty() ? "ninguno" : torneo.getEquipos()));
    }

    private void listarTorneos() throws Exception {
        mostrarLista("Torneos", torneoDAO.leerTodos());
    }

    private void borrarTorneo() throws Exception {
        String nombre = leerTexto("Nombre del torneo a borrar: ");
        if (confirmar("¿Seguro que quieres borrar " + nombre + "?")) {
            torneoDAO.borrar(nombre);
            System.out.println("Torneo borrado.");
        }
    }

    private void inscribirEquipo() throws Exception {
        String torneo = leerTexto("Nombre del torneo: ");
        String equipo = leerTexto("Nombre del equipo: ");
        torneoDAO.inscribirEquipo(torneo, equipo);
        System.out.println(equipo + " inscrito en " + torneo + ".");
    }

    private void iniciarTorneo() throws Exception {
        String torneo = leerTexto("Nombre del torneo: ");
        torneoDAO.iniciarTorneo(torneo);
        System.out.println("Torneo iniciado: partidas generadas.");
    }

    private void finalizarTorneo() throws Exception {
        String torneo = leerTexto("Nombre del torneo: ");
        torneoDAO.finalizarTorneo(torneo);
        System.out.println("Torneo finalizado.");
    }

    private void torneosPorJuego() throws Exception {
        Juego juego = leerEnum("Juego:", Juego.values());
        mostrarLista("Torneos de " + juego, torneoDAO.torneosPorJuego(juego));
    }

    private void torneosPorEstado() throws Exception {
        EstadoTorneo estado = leerEnum("Estado:", EstadoTorneo.values());
        mostrarLista("Torneos en estado " + estado, torneoDAO.torneosPorEstado(estado));
    }

    // =====================================================================
    //  PARTIDAS
    // =====================================================================

    private void menuPartidas() {
        int opcion;
        do {
            System.out.println();
            System.out.println("--- PARTIDAS ---");
            System.out.println(" 1. Listar todas");
            System.out.println(" 2. Ver una partida (por id)");
            System.out.println(" 3. Partidas de un torneo");
            System.out.println(" 4. Partidas de un equipo");
            System.out.println(" 5. Partidas por estado");
            System.out.println(" 6. Próxima partida de un equipo");
            System.out.println(" 7. Añadir jugador a una partida");
            System.out.println(" 8. Registrar resultado");
            System.out.println(" 9. Partidas ganadas por un equipo");
            System.out.println("10. Partidas jugadas por un equipo");
            System.out.println("11. Borrar partida");
            System.out.println(" 0. Volver");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> ejecutar(this::listarPartidas);
                case 2 -> ejecutar(this::verPartida);
                case 3 -> ejecutar(this::partidasDeTorneo);
                case 4 -> ejecutar(this::partidasDeEquipo);
                case 5 -> ejecutar(this::partidasPorEstado);
                case 6 -> ejecutar(this::proximaPartida);
                case 7 -> ejecutar(this::anadirJugadorAPartida);
                case 8 -> ejecutar(this::registrarResultado);
                case 9 -> ejecutar(this::partidasGanadas);
                case 10 -> ejecutar(this::partidasJugadas);
                case 11 -> ejecutar(this::borrarPartida);
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void listarPartidas() throws Exception {
        mostrarLista("Partidas", partidaDAO.leerTodos());
    }

    private void verPartida() throws Exception {
        Partida partida = partidaDAO.leer(leerEntero("Id de la partida: "));
        System.out.println(partida);
        System.out.println("Jugadores de " + partida.getEquipo1() + ": " + partida.getJugadores1());
        System.out.println("Jugadores de " + partida.getEquipo2() + ": " + partida.getJugadores2());
    }

    private void partidasDeTorneo() throws Exception {
        String torneo = leerTexto("Nombre del torneo: ");
        mostrarLista("Partidas de " + torneo, partidaDAO.partidasDeTorneo(torneo));
    }

    private void partidasDeEquipo() throws Exception {
        String equipo = leerTexto("Nombre del equipo: ");
        mostrarLista("Partidas de " + equipo, partidaDAO.partidasPorEquipo(equipo));
    }

    private void partidasPorEstado() throws Exception {
        EstadoPartida estado = leerEnum("Estado:", EstadoPartida.values());
        mostrarLista("Partidas " + estado, partidaDAO.partidasPorEstado(estado));
    }

    private void proximaPartida() throws Exception {
        System.out.println(partidaDAO.proximaPartida(leerTexto("Nombre del equipo: ")));
    }

    private void anadirJugadorAPartida() throws Exception {
        Partida partida = partidaDAO.leer(leerEntero("Id de la partida: "));
        Jugador jugador = jugadorDAO.leerJugador(leerTexto("Nickname del jugador: "));
        partida.anadirJugador(jugador);       // comprueba juego, equipo y plantilla llena
        partidaDAO.actualizar(partida);
        System.out.println(jugador.getNickname() + " añadido a la partida.");
    }

    private void registrarResultado() throws Exception {
        Partida partida = partidaDAO.leer(leerEntero("Id de la partida: "));
        System.out.println(partida);
        String ganador = leerTexto("Equipo ganador (" + partida.getEquipo1() + " / " + partida.getEquipo2() + "): ");
        partida.setGanador(ganador);          // valida que sea uno de los dos y que no esté ya finalizada
        partidaDAO.actualizar(partida);
        System.out.println("Resultado registrado.");
    }

    private void partidasGanadas() throws Exception {
        String equipo = leerTexto("Nombre del equipo: ");
        List<Partida> ganadas = partidaDAO.partidasGanadasPorEquipo(equipo);
        mostrarLista(equipo + " ha ganado " + ganadas.size() + " partidas", ganadas);
    }

    private void partidasJugadas() throws Exception {
        String equipo = leerTexto("Nombre del equipo: ");
        System.out.println(equipo + " ha jugado " + partidaDAO.contarPartidasJugadas(equipo) + " partidas.");
    }

    private void borrarPartida() throws Exception {
        int id = leerEntero("Id de la partida a borrar: ");
        if (confirmar("¿Seguro que quieres borrar la partida " + id + "?")) {
            partidaDAO.borrar(id);
            System.out.println("Partida borrada.");
        }
    }

    // =====================================================================
    //  FICHEROS (usa GestorFicheros)
    // =====================================================================

    private void menuFicheros() {
        int opcion;
        do {
            System.out.println();
            System.out.println("--- FICHEROS ---");
            System.out.println("1. Ver el contenido de una carpeta");
            System.out.println("2. Mover un fichero a otra carpeta");
            System.out.println("3. Renombrar una carpeta");
            System.out.println("0. Volver");
            System.out.println("(Ojo: si mueves o renombras la carpeta 'datos' mientras el programa está abierto,");
            System.out.println(" los DAO no encontrarán sus ficheros hasta que la devuelvas a su sitio.)");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> ejecutar(this::verCarpeta);
                case 2 -> ejecutar(this::moverFichero);
                case 3 -> ejecutar(this::renombrarCarpeta);
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void verCarpeta() {
        System.out.print("Carpeta (Enter para 'datos'): ");
        String ruta = sc.nextLine().trim();
        File carpeta = new File(ruta.isEmpty() ? "datos" : ruta);

        File[] contenido = carpeta.listFiles();
        if (contenido == null) {
            System.out.println("La carpeta no existe o no se puede leer: " + carpeta);
            return;
        }
        System.out.println("--- " + carpeta + " ---");
        if (contenido.length == 0) {
            System.out.println("(vacía)");
        }
        for (File f : contenido) {
            System.out.println((f.isDirectory() ? "[carpeta] " : "[fichero] ") + f.getName());
        }
    }

    private void moverFichero() throws IOException {
        File fichero = new File(leerTexto("Ruta del fichero a mover: "));
        File destino = new File(leerTexto("Carpeta destino (se crea si no existe): "));
        GestorFicheros.moverFichero(fichero, destino);
        System.out.println("Fichero movido a " + destino + ".");
    }

    private void renombrarCarpeta() throws IOException {
        File carpeta = new File(leerTexto("Ruta de la carpeta a renombrar: "));
        String nuevoNombre = leerTexto("Nuevo nombre: ");
        GestorFicheros.renombrarCarpeta(carpeta, nuevoNombre);
        System.out.println("Carpeta renombrada a " + nuevoNombre + ".");
    }

    // =====================================================================
    //  GESTIÓN DE ERRORES (un único sitio para todas las opciones)
    // =====================================================================

    /** Ejecuta una opción del menú y, si falla, muestra el motivo y vuelve al menú sin caerse. */
    private void ejecutar(Accion accion) {
        try {
            accion.ejecutar();
        } catch (NoEncontradoException e) {
            System.out.println("No encontrado: " + e.getMessage());
        } catch (DuplicadoException e) {
            System.out.println("Ya existe: " + e.getMessage());
        } catch (ReglaDeNegocioException e) {
            // Va ANTES que DAOException porque es una subclase suya.
            System.out.println("No permitido: " + e.getMessage());
        } catch (DAOException e) {
            System.out.println("Error de datos: " + e.getMessage()
                    + (e.getCause() != null ? " (" + e.getCause().getMessage() + ")" : ""));
        } catch (IOException e) {
            System.out.println("Error de ficheros: " + e.getMessage());
        } catch (RuntimeException e) {
            // IllegalArgumentException de los setters del modelo, ResultadoInvalidoException, etc.
            System.out.println("Dato no válido: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    // =====================================================================
    //  LECTURA POR CONSOLA
    // =====================================================================

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

    /** Pide un texto y repite si está vacío. */
    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("No puede estar vacío.");
        }
    }

    /** Pregunta sí/no. */
    private boolean confirmar(String pregunta) {
        while (true) {
            System.out.print(pregunta + " (s/n): ");
            String r = sc.nextLine().trim().toLowerCase();
            if (r.equals("s") || r.equals("si") || r.equals("sí")) {
                return true;
            }
            if (r.equals("n") || r.equals("no")) {
                return false;
            }
            System.out.println("Responde s o n.");
        }
    }

    /** Muestra los valores de un enum numerados y devuelve el elegido. */
    private <E extends Enum<E>> E leerEnum(String titulo, E[] valores) {
        System.out.println(titulo);
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + ". " + valores[i]);
        }
        while (true) {
            int n = leerEntero("Elige (1-" + valores.length + "): ");
            if (n >= 1 && n <= valores.length) {
                return valores[n - 1];
            }
            System.out.println("Opción no válida.");
        }
    }

    /** Igual que leerEnum, pero solo con los roles del juego indicado. */
    private Rol leerRol(String titulo, Juego juego) {
        List<Rol> roles = new ArrayList<>();
        for (Rol rol : Rol.values()) {
            if (rol.getJuego() == juego) {
                roles.add(rol);
            }
        }
        return leerEnum(titulo, roles.toArray(new Rol[0]));
    }

    private void mostrarLista(String titulo, List<?> lista) {
        System.out.println("--- " + titulo + " ---");
        if (lista.isEmpty()) {
            System.out.println("(ninguno)");
            return;
        }
        for (Object elemento : lista) {
            System.out.println(elemento);
        }
    }
}