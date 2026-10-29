package es.esports.modelo;

public enum Rol {

    //Hice un constructor en Rol para asignarle a cada rol el juego de donde proviene.
    //Para no tener que ir haciendo Switches en las clases.

    TOP(Juego.League_of_Legends),
    JGL(Juego.League_of_Legends),
    MID(Juego.League_of_Legends),
    ADC(Juego.League_of_Legends),
    SUPP(Juego.League_of_Legends),
    CENTINELA(Juego.Valorant),
    INICIADOR(Juego.Valorant),
    DUELISTA(Juego.Valorant),
    CONTROLADOR(Juego.Valorant);

    private final Juego juego;

    Rol(Juego juego) {
        this.juego = juego;
    }

    public Juego getJuego() {
        return juego;
    }
}
