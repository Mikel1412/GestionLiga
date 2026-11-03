package es.esports.modelo;

import es.esports.excepciones.reglaNegocioException.JugadorIncompatibleException;
import es.esports.excepciones.reglaNegocioException.JuegoIncompatibleException;
import es.esports.excepciones.reglaNegocioException.PlantillaLlenaException;
import es.esports.excepciones.reglaNegocioException.ResultadoInvalidoException;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class Partida {
    private static final int MAX_JUGADORES = 5;

    private int id;                                       // 0 hasta que el DAO le asigna uno
    private String torneo;
    private String equipo1;
    private String equipo2;
    private ArrayList<String> jugadores1 = new ArrayList<>();   // nicknames
    private ArrayList<String> jugadores2 = new ArrayList<>();
    private String ganador;                               // null si no hay resultado
    private EstadoPartida estado;
    private String fecha;
    private Juego juego;


    //Nuestro metodo de hacerlo sería: creamos la partida y aqui metemos el nombre del torneo en el que reside la partida, los dos equipos, la fecha y el juego.
    //Los dos equipos son obligatorios y no puede jugar un mismo equipo contra otro
    //El juego tambien es obligatorio, y la partida empieza con ganador en null y estado de juego pendiente, porque aun no se sabe como acaba.

    public Partida(String torneo, String equipo1, String equipo2, String fecha, Juego juego) {
        setTorneo(torneo);
        if (equipo1 == null || equipo1.isBlank() || equipo2 == null || equipo2.isBlank()) {
            throw new IllegalArgumentException("Los dos equipos son obligatorios");
        }
        if (equipo1.trim().equalsIgnoreCase(equipo2.trim())) {
            throw new IllegalArgumentException("Un equipo no puede jugar contra si mismo");
        }
        this.equipo1 = equipo1.trim();
        this.equipo2 = equipo2.trim();
        setFecha(fecha);
        if (juego == null) {
            throw new IllegalArgumentException("El juego es obligatorio");
        }
        this.juego = juego;
        this.estado = EstadoPartida.PENDIENTE;
        this.ganador = null;
    }

    //AñadirJugador solo compara los dos textos: si jugador.getEquipo() coincide con equipo1, va a la lista 1.
    //Si coincide con equipo2, a la lista 2. Si no coincide con ninguno, o es null, salta JugadorIncompatibleException.

    public void anadirJugador(Jugador jugador)
            throws JugadorIncompatibleException, PlantillaLlenaException, JuegoIncompatibleException {
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser null");
        }
        if (jugador.getJuego() != juego) {
            throw new JuegoIncompatibleException("El jugador " + jugador.getNickname() + " no juega a " + juego);
        }
        String equipoJugador = jugador.getEquipo(); //Metemos el equipo del jugador que estamos añadiendo en otra variable para luego comprobar si es null o no.
        if (equipoJugador == null) {
            throw new JugadorIncompatibleException("El jugador " + jugador.getNickname() + " es agente libre");
        }

        //Si el jugador que estamos añadiendo ya está en la lista de jugadores 1 o en la lista de jugadores 2, no se añade.
        if (estaEn(jugadores1, jugador.getNickname()) || estaEn(jugadores2, jugador.getNickname())) {
            throw new IllegalArgumentException("El jugador ya está en la partida: " + jugador.getNickname());
        }

        //Ahora si el jugador ha pasado todos las comprobaciones hasta ahora, comprobamos si el jugador pertenece al equipo de alguno de los que se va a enfrentar en la partida.
        if (equipoJugador.equalsIgnoreCase(equipo1)) {
            meter(jugadores1, jugador.getNickname(), equipo1);
        } else if (equipoJugador.equalsIgnoreCase(equipo2)) {
            meter(jugadores2, jugador.getNickname(), equipo2);
        } else {
            throw new JugadorIncompatibleException("El jugador " + jugador.getNickname()
                    + " (" + equipoJugador + ") no es de ninguno de los dos equipos de la partida"); //Si no simplemente dice que el jugador de por ejemplo "Titan" no pertenece al equipo de "G2" o "T1"
        }
    }

    //Este metodo nos sirve solamente para saber si en la lista de jugadores1 o la lista de jugadores2, existe el jugador que queremos añadir en la partida ya.

    private boolean estaEn(ArrayList<String> lista, String nickname) {
        for (String n : lista) {
            if (n.equalsIgnoreCase(nickname)) {
                return true;
            }
        }
        return false;
    }

    //Este metodo lo que hace es meter al jugador dentro de la lista si el la lista de jugadores de ese equipo no ha pasado el max de 5 jugadores.
    //Si lo ha pasado, simplemente pasa una excepcion

    private void meter(ArrayList<String> lista, String nickname, String nombreEquipo) throws PlantillaLlenaException {
        if (lista.size() >= MAX_JUGADORES) {
            throw new PlantillaLlenaException("El equipo " + nombreEquipo + " ya tiene " + MAX_JUGADORES + " jugadores en la partida");
        }
        lista.add(nickname);
    }

    //Un set para luego establecer al ganador de esta partida si el estado de la partida no es finalizado aun y cumple con que no sea null
    //Comprueba que el equipo que introducimos por parametro es alguno de estos dos y lo establece como ganador de la partida, si introducimos otro equipo que no es alguno de los dos que jugaron, nos lanzará una excepción
    public void setGanador(String ganador) throws ResultadoInvalidoException {
        if (estado == EstadoPartida.FINALIZADA) {
            throw new ResultadoInvalidoException("La partida ya está finalizada");
        }
        if (ganador == null || ganador.isBlank()) {
            throw new ResultadoInvalidoException("El ganador es obligatorio");
        }
        if (ganador.trim().equalsIgnoreCase(equipo1)) {
            this.ganador = equipo1;
        } else if (ganador.trim().equalsIgnoreCase(equipo2)) {
            this.ganador = equipo2;
        } else {
            throw new ResultadoInvalidoException("El ganador debe ser " + equipo1 + " o " + equipo2);
        }
        this.estado = EstadoPartida.FINALIZADA;
    }

    //Hacemos un get y un set del id para luego asignar los ID en el DAO.
    //Simplemente comprobamos en el setID si es un numero positivo.

    public int getId() { return id; }
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la partida debe ser positivo");
        }
        this.id = id;
    }

    public String getTorneo() { return torneo; }
    public void setTorneo(String torneo) {
        if (torneo == null || torneo.isBlank()) {
            throw new IllegalArgumentException("El torneo es obligatorio");
        }
        this.torneo = torneo.trim();
    }

    public String getEquipo1() { return equipo1; }
    public String getEquipo2() { return equipo2; }

    public List<String> getJugadores1() { return List.copyOf(jugadores1); }
    public List<String> getJugadores2() { return List.copyOf(jugadores2); }


    //Para que el DAO pueda reconstruir una partida al leer el fichero.
    //necesita estos dos de abajo

    public void setJugadores1(List<String> nicknames) {
        if (nicknames == null) throw new IllegalArgumentException("La alineación no puede ser null");
        jugadores1 = new ArrayList<>(nicknames);
    }

    public void setJugadores2(List<String> nicknames) {
        if (nicknames == null) throw new IllegalArgumentException("La alineación no puede ser null");
        jugadores2 = new ArrayList<>(nicknames);
    }

    public String getGanador() { return ganador; }
    public EstadoPartida getEstado() { return estado; }
    public Juego getJuego() { return juego; }
    public String getFecha() { return fecha; }

    //Metodo para comprobar que la fecha metida por String es una fecha valida y ya
    public void setFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        try {
            LocalDate.parse(fecha.trim());   // solo comprueba que sea una fecha real (aaaa-mm-dd)
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha no válida (usa aaaa-mm-dd): " + fecha);
        }
        this.fecha = fecha.trim();
    }

    @Override
    public String toString() {
        return "Partida{id=" + id + ", " + torneo + ", " + equipo1 + " vs " + equipo2 + ", " + juego
                + ", " + fecha + ", " + estado + (ganador != null ? ", ganador=" + ganador : "") + "}";
    }
}
