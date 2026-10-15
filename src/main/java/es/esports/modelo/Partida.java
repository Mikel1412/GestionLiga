package es.esports.modelo;

import es.esports.excepciones.NoEncontradoException;
import es.esports.excepciones.reglaNegocioException.JuegoIncompatibleException;

public class Partida {
    private Equipo equipo1;
    private Equipo equipo2;
    private Equipo Ganador;
    private EstadoPartida Estado;
    private String Fecha;
    private Juego juego;


    public Partida(Equipo equipo1, Equipo equipo2, String fecha, Juego juego1) {
        // Comprobar que ambos equipos pertenezcan al mismo juego que la partida
        if (equipo1.getJuego() != juego1 || equipo2.getJuego() != juego1) {
            throw new JuegoIncompatibleException("El juego de uno o ambos equipos no coincide con el juego de la partida (" + juego1 + ").");
        }

        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
        Fecha = fecha;
        Estado = EstadoPartida.Activa;
        juego = juego1;
    }

    public void setGanador(Equipo ganador){
        if (ganador != null && (ganador.equals(this.equipo1) || ganador.equals(this.equipo2))) {
            this.Ganador = ganador;
            this.Estado = EstadoPartida.Finalizada;
        } else {
            throw new NoEncontradoException("El equipo ganador debe ser el equipo 1 o el equipo 2 de la partida.");
        }


    }

    public Equipo getEquipo2() {
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
    }
}
