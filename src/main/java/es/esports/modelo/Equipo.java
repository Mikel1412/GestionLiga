package es.esports.modelo;

import es.esports.excepciones.reglaNegocioException.JuegoIncompatibleException;

import java.util.ArrayList;

public class Equipo {

    private String Nombre;
    private ArrayList<Jugador> jugadores;
    private Juego juego;
    private ArrayList<Jugador> Sustitutos;

    public Equipo(String nombre, Juego juego) {
        Nombre = nombre;
        this.juego = juego;
        jugadores = new ArrayList<Jugador>();
        Sustitutos = new ArrayList<Jugador>();
    }

    public String getNombre() {
        return Nombre;
    }

    public Juego getJuego() {
        return juego;
    }

    public void añadirJugador(Jugador j ){

        if (j.getJuego() != this.juego) {
            throw new JuegoIncompatibleException("El juego del jugador (" + j.getJuego()
                    + ") no coincide con el del equipo (" + this.juego + ").");
        }

        if (this.jugadores.size() >= 5 && this.Sustitutos.size() >= 2) {
            throw new PlantillaLlenaException("No se pueden añadir más jugadores: el equipo ya cuenta con 5 titulares y 2 sustitutos.");
        }

        if (this.jugadores.size() < 5) {
            this.jugadores.add(j);
        } else {
            this.Sustitutos.add(j);
        }

    }

}
