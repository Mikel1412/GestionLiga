package es.esports.modelo;

import es.esports.excepciones.reglaNegocioException.EquipoYaInscritoException;
import es.esports.excepciones.reglaNegocioException.JuegoIncompatibleException;
import es.esports.excepciones.reglaNegocioException.JugadoresInsuficientesException;
import es.esports.excepciones.reglaNegocioException.PlantillaLlenaException;

import java.util.ArrayList;

public class Torneo {

    private String NombreTorneo;
    private EstadoTorneo estado ;
    private ArrayList<Equipo> ListaEquipos;
    private Juego juego;
    private Formato formato;
    private String FechaI;
    private String FechaF;
    private int MaxEquipos;


    public Torneo(String nombreTorneo, EstadoTorneo estado, Juego juego, Formato formato, String fechaI, String fechaF, int maxEquipos) {
        NombreTorneo = nombreTorneo;
        this.estado = estado;
        this.juego = juego;
        this.formato = formato;
        FechaI = fechaI;
        FechaF = fechaF;
        MaxEquipos = maxEquipos;
        ListaEquipos = new ArrayList<Equipo>();
    }

    public String getNombreTorneo() {
        return NombreTorneo;
    }

    public EstadoTorneo getEstado() {
        return estado;
    }

    public ArrayList<Equipo> getListaEquipos() {
        return ListaEquipos;
    }

    public Juego getJuego() {
        return juego;
    }

    public Formato getFormato() {
        return formato;
    }

    public String getFechaI() {
        return FechaI;
    }

    public String getFechaF() {
        return FechaF;
    }

    public int getMaxEquipos() {
        return MaxEquipos;
    }

    public void añadirEquipo(Equipo e ) throws JuegoIncompatibleException, EquipoYaInscritoException, JugadoresInsuficientesException, PlantillaLlenaException {

        // 1. Comprobar que el juego del equipo coincide con el del torneo
        if (e.getJuego() != this.juego) {
            throw new JuegoIncompatibleException("El juego del equipo (" + e.getJuego()
                    + ") no coincide con el del torneo (" + this.juego + ").");
        }

        // 2. Comprobar que la lista de jugadores titulares sea exactamente 5 (o menor si no cumple el mínimo)
        if (e.getJugadores().size() != 5) {
            throw new JugadoresInsuficientesException(("El equipo " + e.getNombre()
                    + " debe tener exactamente 5 jugadores titulares para inscribirse (actual: " + e.getJugadores().size() + ")."));
        }

        // 3. Comprobar que el equipo no esté ya añadido previamente
        if (this.ListaEquipos.contains(e)) {
            throw new EquipoYaInscritoException("El equipo " + e.getNombre() + " ya está inscrito en este torneo.");
        }

        // 4. Comprobamos que si el torneo esta lleno, no añadimos mas equipos
        if (ListaEquipos.size() == MaxEquipos) {
            throw new PlantillaLlenaException("El torneo esta lleno");
        }

        // Si supera todas las comprobaciones, se añade
        this.ListaEquipos.add(e);

    }
}
