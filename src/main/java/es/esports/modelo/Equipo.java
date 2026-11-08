package es.esports.modelo;

/**
 * Un equipo de la liga, identificado por su nombre. Juega a un único juego y pertenece
 * a una organización (una organización puede tener un equipo por juego).
 * <p>
 * No guarda a sus jugadores: cada {@code Jugador} apunta a su equipo, y la plantilla
 * se obtiene consultando al {@code JugadorDAO}.
 */
public class Equipo {

    private String nombre;
    private String tag;
    private Juego juego;
    private String organizacion;

    /**
     * Crea un equipo.
     *
     * @param nombre       nombre (su clave); no puede estar vacío
     * @param tag          abreviatura de 2 a 5 caracteres
     * @param juego        juego del equipo; no puede ser {@code null}
     * @param organizacion organización a la que pertenece; no puede estar vacía
     * @throws IllegalArgumentException si algún argumento no es válido
     */
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

    /**
     * Establece el tag del equipo.
     *
     * @param tag abreviatura de 2 a 5 caracteres
     * @throws IllegalArgumentException si está vacío o su longitud no es de 2 a 5 caracteres
     */
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
