package es.esports.modelo;

/**
 * Plantilla competitiva de una organización en un juego concreto.
 * Su clave es el nombre. Los jugadores no se guardan aquí: cada Jugador
 * referencia a su equipo por nombre (ver JugadorDAO).
 */
public class Equipo {

    private String nombre;
    private String tag;
    private Juego juego;
    private String organizacion;

    public Equipo(String nombre, String tag, Juego juego, String organizacion) {
        setNombre(nombre);
        setTag(tag);
        setJuego(juego);
        setOrganizacion(organizacion);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del equipo es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("El tag es obligatorio");
        }
        String limpio = tag.trim().toUpperCase();
        if (limpio.length() < 2 || limpio.length() > 5) {
            throw new IllegalArgumentException("El tag debe tener entre 2 y 5 caracteres: " + tag);
        }
        this.tag = limpio;
    }

    public Juego getJuego() {
        return juego;
    }

    public void setJuego(Juego juego) {
        if (juego == null) {
            throw new IllegalArgumentException("El juego es obligatorio");
        }
        this.juego = juego;
    }

    public String getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(String organizacion) {
        if (organizacion == null || organizacion.isBlank()) {
            throw new IllegalArgumentException("La organización es obligatoria");
        }
        this.organizacion = organizacion.trim();
    }

    @Override
    public String toString() {
        return "Equipo{" + nombre + " [" + tag + "], " + juego + ", org='" + organizacion + "'}";
    }
}
