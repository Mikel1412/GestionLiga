package es.esports.modelo;

public class Equipo {

    //FALTAN TODAS LAS COMPROBACIONES A LOS DATOS (Como lo que hay en el setID)
    //Y DECIDIR SI PONER ID EN EL EQUIPO O NO, O PONERLO EN SU DAO, PORQUE YA VIMOS QUE CON EL STATIC NO SE PODIA.

    //ATRIBUTOS EQUIPO

    // int ID;
    String nombre;
    String tag;
    Juego juego;
    String organizacion;

    //CONSTRUCTORES EQUIPO

    public Equipo() {

    }

    public Equipo(String nombre, String tag, Juego juego, String organizacion) {
        setNombre(nombre);
        setTag(tag);
        setJuego(juego);
        setOrganizacion(organizacion);
    }

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
}
