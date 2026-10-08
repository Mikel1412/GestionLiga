package es.esports.modelo;

import es.esports.excepciones.reglaNegocioException.RolInvalidoException;

public class Jugador {
    private String Nombre;
    private String Apellido1;
    private String Apellido2;
    private String Nickname;
    private int edad;
    private Juego juego;
    private Rol rol1;
    private Rol rol2;
    private Equipo equipo = null;

    public Jugador(String apellido1, String nombre, String apellido2, String nickname, int edad, Juego juego, Rol rol1, Rol rol2) {
        Apellido1 = apellido1;
        Nombre = nombre;
        Apellido2 = apellido2;
        Nickname = nickname;
        this.edad = edad;
        this.juego = juego;

        if(juego == Juego.League_of_Legends){
            switch (rol1){
                case TOP,JGL,MID,ADC,SUPP -> this.rol1 = rol1;
                case CONTROLADOR,CENTINELA,DUELISTA,INICIADOR -> throw RolInvalidoException("Ese rol no es de League of Legends");
            }
        }else{
            switch (rol1){
                case TOP,JGL,MID,ADC,SUPP -> throw RolInvalidoException("Ese rol no es de Valorant");;
                case CONTROLADOR,CENTINELA,DUELISTA,INICIADOR -> this.rol1 = rol1;
            }

        }

        if(juego == Juego.League_of_Legends){
            switch (rol2){
                case TOP,JGL,MID,ADC,SUPP -> this.rol2 = rol2;
                case CONTROLADOR,CENTINELA,DUELISTA,INICIADOR -> throw RolInvalidoException("Ese rol no es de League of Legends");
            }
        }else{
            switch (rol2){
                case TOP,JGL,MID,ADC,SUPP -> throw RolInvalidoException("Ese rol no es de Valorant");;
                case CONTROLADOR,CENTINELA,DUELISTA,INICIADOR -> this.rol2 = rol2;
            }

        }
    }

    public Jugador(String nombre, String apellido1, String apellido2, String nickname, int edad, Juego juego, Rol rol1) {
        Nombre = nombre;
        Apellido1 = apellido1;
        Apellido2 = apellido2;
        Nickname = nickname;
        this.edad = edad;
        this.juego = juego;

        if(juego == Juego.League_of_Legends){
            switch (rol1){
                case TOP,JGL,MID,ADC,SUPP -> this.rol1 = rol1;
                case CONTROLADOR,CENTINELA,DUELISTA,INICIADOR -> throw RolInvalidoException("Ese rol no es de League of Legends");
            }
        }else{
            switch (rol1){
                case TOP,JGL,MID,ADC,SUPP -> throw RolInvalidoException("Ese rol no es de Valorant");;
                case CONTROLADOR,CENTINELA,DUELISTA,INICIADOR -> this.rol1 = rol1;
            }

        }

        this.rol2 = null;
    }

    public String getApellido1() {
        return Apellido1;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getApellido2() {
        return Apellido2;
    }

    public String getNickname() {
        return Nickname;
    }

    public int getEdad() {
        return edad;
    }

    public Juego getJuego() {
        return juego;
    }

    public Rol getRol1() {
        return rol1;
    }

    public Rol getRol2() {
        return rol2;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
}
