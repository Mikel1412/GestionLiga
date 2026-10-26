package es.esports.modelo;

import es.esports.excepciones.reglaNegocioException.JugadorIncompatibleException;
import es.esports.excepciones.reglaNegocioException.JuegoIncompatibleException;
import es.esports.excepciones.reglaNegocioException.PlantillaLlenaException;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;


public class Partida {
    private static final int MAX_JUGADORES = 5;

    private int id;                          // 0 hasta que el DAO le asigna uno
    private ArrayList<Jugador> equipo1 = new ArrayList<>();
    private ArrayList<Jugador> equipo2 = new ArrayList<>();
    private String ganador;                  // nombre del equipo ganador; null si no hay resultado
    private EstadoPartida estado;
    private String fecha;
    private Juego juego;

    public Partida(String fecha, Juego juego) {
        if (juego == null) {
            throw new IllegalArgumentException("El juego es obligatorio");
        }
        this.juego = juego;
        setFecha(fecha);
        this.estado = EstadoPartida.PENDIENTE;
        this.ganador = null;
    }

    public void anadirJugador(Jugador jugador)
            throws JugadorIncompatibleException, PlantillaLlenaException, JuegoIncompatibleException {
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser null");
        }
        if (jugador.getJuego() != juego) {
            throw new JuegoIncompatibleException("El jugador " + jugador.getNickname() + " no juega a " + juego);
        }
        String equipoJugador = jugador.getEquipo();
        if (equipoJugador == null) {
            throw new JugadorIncompatibleException("El jugador " + jugador.getNickname() + " es agente libre");
        }
        if (estaEn(equipo1, jugador) || estaEn(equipo2, jugador)) {
            throw new IllegalArgumentException("El jugador ya está en la partida: " + jugador.getNickname());
        }
        // Los dos equipos llenos: no cabe nadie más
        if (equipo1.size() >= MAX_JUGADORES && equipo2.size() >= MAX_JUGADORES) {
            throw new PlantillaLlenaException("Los dos equipos de la partida ya tienen " + MAX_JUGADORES + " jugadores");
        }
        // ¿Es del mismo equipo que los que ya hay en el equipo 1?
        if (mismoEquipo(equipo1, equipoJugador)) {
            meter(equipo1, jugador);
            return;
        }
        // ¿Y en el equipo 2?
        if (mismoEquipo(equipo2, equipoJugador)) {
            meter(equipo2, jugador);
            return;
        }
        // No coincide con ninguno: si hay una lista vacía, empieza un equipo nuevo
        if (equipo1.isEmpty()) {
            equipo1.add(jugador);
        } else if (equipo2.isEmpty()) {
            equipo2.add(jugador);
        } else {
            throw new JugadorIncompatibleException("El jugador " + jugador.getNickname()
                    + " (" + equipoJugador + ") no es de ninguno de los dos equipos de la partida");
        }
    }




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


    private boolean mismoEquipo(ArrayList<Jugador> lista, String nombreEquipo) {
        return !lista.isEmpty() && lista.get(0).getEquipo().equalsIgnoreCase(nombreEquipo);
    }

    private boolean estaEn(ArrayList<Jugador> lista, Jugador jugador) {
        for (Jugador j : lista) {
            if (j.getNickname().equalsIgnoreCase(jugador.getNickname())) {
                return true;
            }
        }
        return false;
    }

    private void meter(ArrayList<Jugador> lista, Jugador jugador) throws PlantillaLlenaException {
        if (lista.size() >= MAX_JUGADORES) {
            throw new PlantillaLlenaException("El equipo " + lista.get(0).getEquipo()
                    + " ya tiene " + MAX_JUGADORES + " jugadores en la partida");
        }
        lista.add(jugador);
    }

    public String getNombreEquipo1() {
        if (equipo1.isEmpty()) {
            return null;
        }
        Equipo e = equipo1.get(0).getEquipo();
        return e.getNombre();
    }

    public String getNombreEquipo2() {
        if (equipo2.isEmpty()) {
            return null;
        }
        Equipo e = equipo2.get(0).getEquipo();
        return e.getNombre();
    }

   /* public void setGanador(Equipo ganador) throws NoEncontradoException {
        if (ganador != null && (ganador.equals(this.equipo1) || ganador.equals(this.equipo2))) {
            this.Ganador = ganador;
            this.Estado = EstadoPartida.Finalizada;
        } else {
            throw new NoEncontradoException("El equipo ganador debe ser el equipo 1 o el equipo 2 de la partida.");
        }


    }*/

   /* public Equipo getEquipo2() {
        return equipo2;
    }

    public Equipo getEquipo1() {
        return equipo1;
    }

    public Equipo getGanador() {
        return Ganador;
    }

    public EstadoPartida getEstado() {
        return Estado;
    }

    public String getFecha() {
        return Fecha;
    }

    public Juego getJuego() {
        return juego;
    }*/
}
