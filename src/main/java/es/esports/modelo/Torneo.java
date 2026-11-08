package es.esports.modelo;

import es.esports.excepciones.reglaNegocioException.EquipoYaInscritoException;
import es.esports.excepciones.reglaNegocioException.EquiposInsuficientesException;
import es.esports.excepciones.reglaNegocioException.EstadoTorneoException;
import es.esports.excepciones.reglaNegocioException.PlantillaLlenaException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Un torneo de un juego, con formato, fechas y un máximo de equipos.
 * <p>
 * Guarda los equipos inscritos por su nombre (no los objetos {@code Equipo}).
 * Nace en estado {@link EstadoTorneo#PREPARACION}, pasa a {@code ACTIVO} al iniciarse
 * y a {@code FINALIZADO} al terminar. Las fechas son textos con formato {@code aaaa-mm-dd}.
 */
public class Torneo {

    private String nombre;
    private EstadoTorneo estado; //Puede este estar en preparacion, activo o finalizado
    private ArrayList<String> equipos = new ArrayList<>();   // nombres de los equipos inscritos
    private Juego juego;
    private Formato formato; //A mejor de uno, de tres o de cinco
    private String fechaI;
    private String fechaF;
    private int maxEquipos;

    /**
     * Crea un torneo en preparación y sin equipos.
     *
     * @param nombre     nombre del torneo (su clave); no puede estar vacío
     * @param juego      juego del torneo; no puede ser {@code null}
     * @param formato    formato de cada enfrentamiento (BO1, BO3 o BO5); no puede ser {@code null}
     * @param fechaI     fecha de inicio ({@code aaaa-mm-dd})
     * @param fechaF     fecha de fin ({@code aaaa-mm-dd}); no puede ser anterior a la de inicio
     * @param maxEquipos máximo de equipos inscritos; mínimo 2
     * @throws IllegalArgumentException si algún argumento no es válido
     */
    public Torneo(String nombre, Juego juego, Formato formato, String fechaI, String fechaF, int maxEquipos) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del torneo es obligatorio");
        }
        if (juego == null) {
            throw new IllegalArgumentException("El juego es obligatorio");
        }
        if (formato == null) {
            throw new IllegalArgumentException("El formato es obligatorio");
        }
        //Un torneo minimo tiene que tener 2 equipos para poder prepararse.
        if (maxEquipos < 2) {
            throw new IllegalArgumentException("Un torneo necesita al menos 2 equipos como máximo");
        }
        //Comprobación de fechas de ver si estan bien
        LocalDate inicio = parsearFecha(fechaI);
        LocalDate fin = parsearFecha(fechaF);
        if (fin.isBefore(inicio)) { //Basicamente si la fecha final esta antes de la inicial, simplemente lanzamos una excepcion.
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial");
        }

        this.nombre = nombre.trim();
        this.juego = juego;
        this.formato = formato;
        this.fechaI = fechaI.trim();
        this.fechaF = fechaF.trim();
        this.maxEquipos = maxEquipos;
        this.estado = EstadoTorneo.PREPARACION;
    }

    private LocalDate parsearFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        try {
            return LocalDate.parse(fecha.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha no válida (usa aaaa-mm-dd): " + fecha);
        }
    }

    // ===Reglas del torneo===

    /**
     * Inscribe un equipo (por su nombre) en el torneo.
     *
     * @param nombreEquipo nombre del equipo; no puede estar vacío
     * @throws EstadoTorneoException       si el torneo no está en preparación
     * @throws EquipoYaInscritoException   si el equipo ya está inscrito
     * @throws PlantillaLlenaException     si el torneo ya tiene el máximo de equipos
     * @throws IllegalArgumentException    si el nombre está vacío
     */
    public void anadirEquipo(String nombreEquipo)
            throws EstadoTorneoException, EquipoYaInscritoException, PlantillaLlenaException {
        if (nombreEquipo == null || nombreEquipo.isBlank()) {
            throw new IllegalArgumentException("El nombre del equipo es obligatorio");
        }
        if (estado != EstadoTorneo.PREPARACION) {
            throw new EstadoTorneoException("Solo se pueden inscribir equipos mientras el torneo está en preparación");
        }
        String nombreLimpio = nombreEquipo.trim();

        //Comprueba si el equipo que queremos añadir esta en el torneo o no.
        for (String e : equipos) {
            if (e.equalsIgnoreCase(nombreLimpio)) {
                throw new EquipoYaInscritoException("El equipo " + nombreLimpio + " ya está inscrito en este torneo");
            }
        }
        //Si los equipos son iguales o mayores a MaxEquipos, una excepcion, y si cumple todoo lo de arriba + esto, simplemente añade el equipo a la lista
        if (equipos.size() >= maxEquipos) {
            throw new PlantillaLlenaException("El torneo " + nombre + " está lleno (" + maxEquipos + " equipos)");
        }
        equipos.add(nombreLimpio);
    }

    /**
     * Pasa el torneo de preparación a activo.
     *
     * @throws EstadoTorneoException         si el torneo no está en preparación
     * @throws EquiposInsuficientesException si hay menos de 2 equipos inscritos
     */
    public void iniciar() throws EstadoTorneoException, EquiposInsuficientesException {
        if (estado != EstadoTorneo.PREPARACION) {
            throw new EstadoTorneoException("Solo se puede iniciar un torneo que está en preparación");
        }
        if (equipos.size() < 2) {
            throw new EquiposInsuficientesException("Hacen falta al menos 2 equipos para iniciar el torneo");
        }
        estado = EstadoTorneo.ACTIVO;
    }

    /**
     * Pasa el torneo de activo a finalizado.
     *
     * @throws EstadoTorneoException si el torneo no está activo
     */
    public void finalizar() throws EstadoTorneoException {
        if (estado != EstadoTorneo.ACTIVO) {
            throw new EstadoTorneoException("Solo se puede finalizar un torneo que está activo");
        }
        estado = EstadoTorneo.FINALIZADO;
    }

    // ---------- Getters y setters para el DAO ----------

    public String getNombre() { return nombre; }
    public EstadoTorneo getEstado() { return estado; }
    /**
     * Devuelve una copia de la lista de equipos inscritos (modificarla no cambia el torneo).
     */
    public List<String> getEquipos() { return List.copyOf(equipos); }
    public Juego getJuego() { return juego; }
    public Formato getFormato() { return formato; }
    public String getFechaI() { return fechaI; }
    public String getFechaF() { return fechaF; }
    public int getMaxEquipos() { return maxEquipos; }

    /** Restaura el estado al leer el torneo del fichero. Lo usa el DAO. */
    // Los usa el DAO al leer el JSON para restaurar el estado y los inscritos
    public void setEstado(EstadoTorneo estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        this.estado = estado;
    }

    /** Restaura los equipos inscritos al leer el torneo del fichero. Lo usa el DAO. */
    public void setEquipos(List<String> nombres) {
        this.equipos = new ArrayList<>(nombres);
    }

    @Override
    public String toString() {
        return "Torneo{" + nombre + ", " + juego + ", " + formato + ", " + fechaI + " a " + fechaF
                + ", " + estado + ", equipos=" + equipos.size() + "/" + maxEquipos + "}";
    }
}
