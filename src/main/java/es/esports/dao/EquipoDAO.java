package es.esports.dao;

public interface EquipoDAO {

    //Operaciones CRUD.

    /**
     * Registra un equipo nuevo en el almacén de datos.
     * <p>
     * El equipo nace sin jugadores: la plantilla se forma después fichando jugadores
     * mediante {@link JugadorDAO}.
     *
     * @param equipo el equipo a guardar; no puede ser {@code null}
     * @throws NullPointerException si {@code equipo} es {@code null}
     */
    private void crearEquipo(Equipo equipo) {

    }

    private void leerEquipo() {

    }

    private void leerTodosEquipos() {

    }

    private void actualizarEquipo() {

    }

    private void borrarEquipo() {

    }

    //Operaciones NO CRUD.



}
