package es.esports.modelo;

import es.esports.excepciones.reglaNegocioException.JuegoIncompatibleException;
import es.esports.excepciones.reglaNegocioException.PlantillaLlenaException;

import java.util.ArrayList;
import java.util.List;

public class Equipo {

    //Atributos

    private ArrayList<Jugador> jugadores;
    private Juego juego;
    private ArrayList<Jugador> Sustitutos;
    // int ID;
    private String nombre;
    private String tag;
    private String organizacion;

    //CONSTRUCTORES EQUIPO

    public Equipo(String nombre, Juego juego) {
        nombre = nombre;
        this.juego = juego;
        jugadores = new ArrayList<Jugador>();
        Sustitutos = new ArrayList<Jugador>();
    }

    public Equipo() {

    }

    public Equipo(String nombre, String tag, Juego juego, String organizacion) {
        setNombre(nombre);
        setTag(tag);
        setJuego(juego);
        setOrganizacion(organizacion);
    }

    //Métodos

    public void añadirJugador(Jugador j ) throws JuegoIncompatibleException, PlantillaLlenaException {

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
    //FALTAN TODAS LAS COMPROBACIONES A LOS DATOS (Como lo que hay en el setID)
    //Y DECIDIR SI PONER ID EN EL EQUIPO O NO, O PONERLO EN SU DAO, PORQUE YA VIMOS QUE CON EL STATIC NO SE PODIA.



    /* public Equipo(int ID, String nombre, String tag, Juego juego, String organizacion) {

    } */

    //SETTERS (Con sus comprobaciones)

    /* public void setID(int ID) {
        this.ID = ID;
        if (ID <= 0) {
            throw new IllegalArgumentException("El id del equipo debe ser positivo");
        }
        this.id = id;
    } */

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    public void setOrganizacion(String organizacion) {
        this.organizacion = organizacion;
    }

    //GETTERS

    /* public int getID() {
        return ID;
    } */

    public String getNombre() {
        return nombre;
    }

    public String getTag() {
        return tag;
    }

    public Juego getJuego() {
        return juego;
    }

    public String getOrganizacion() {
        return organizacion;
    }

    public List getJugadores(){return jugadores;}
}
