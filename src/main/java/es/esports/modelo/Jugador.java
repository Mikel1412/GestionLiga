package es.esports.modelo;

import es.esports.excepciones.reglaNegocioException.RolInvalidoException;

public class Jugador {

    private String nombre;
    private String apellido1;
    private String apellido2;      // Este apellido podria llegar a ser opcional (un "" si queremos)
    private String nickname;       // Seria con lo que se identificaria al jugador
    private int edad;
    private Juego juego;
    private Rol rol1;
    private Rol rol2;              // Solamente estaria permitido el null en el valorant, porque no te tienes que asignar un segundo rol. (este se elige a la hora de entrar a la partida por los personajes)
    private String equipo;         // Nombre del equipo de donde proviene este jugador (si no tiene equipo (equipo == null) es libre)
    private boolean titular;

    //IMPORTANTE: Cada jugador se crea como libre (que no tiene equipo al principio cuando este es creado)
    //Lo hago porque equipo tiene reglas como que el equipo exista, que este equipo sea el de este juego, que no tenga este ya equipo, y que obviamente la plantilla del equipo no este llena.
    //DONDE SE ASIGNARIAN ENTONCES: estos se asignarian luego en el metodo CARGAR() que hariamos en jugadorDAOJson.
    //(y bueno por consecuencia tambien se asiganria titular en el DAOJson porque no puedes ser titular de un equipo que no existe.

    // CONSTRUCTOR LOL
    // Este seria un constructor para los jugadores de LoL, porque si o si se piden dos roles.
    public Jugador(String nombre, String apellido1, String apellido2, String nickname,
                   int edad, Juego juego, Rol rol1, Rol rol2) throws RolInvalidoException {
        setNombre(nombre);          //Todos estos sets son para las comprobaciones de datos erroneos como, nulls, en blanco, y luego si hay espacios sobrantes por los lados, hacer un trim() para limpiarlo.
        setApellido1(apellido1);
        setApellido2(apellido2);
        setNickname(nickname);
        setEdad(edad);
        setJuego(juego);       // el juego va ANTES que los roles, porque los roles se validan contra él
        setRoles(rol1, rol2);
    }

    // CONSTRUCTOR VALORANT
    // Este seria un constructor para jugadores de Valorant, que tienen solo un rol en el momento.
    public Jugador(String nombre, String apellido1, String apellido2, String nickname,
                   int edad, Juego juego, Rol rol1) throws RolInvalidoException {
        this(nombre, apellido1, apellido2, nickname, edad, juego, rol1, null);
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del jugador es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    public String getApellido1() { return apellido1; }
    public void setApellido1(String apellido1) {
        if (apellido1 == null || apellido1.isBlank()) {
            throw new IllegalArgumentException("El primer apellido es obligatorio");
        }
        this.apellido1 = apellido1.trim();
    }

    public String getApellido2() { return apellido2; }
    public void setApellido2(String apellido2) {
        if (apellido2 == null) {
            this.apellido2 = "";
        } else {
            this.apellido2 = apellido2.trim();
        }
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("El nickname es obligatorio");
        }
        this.nickname = nickname.trim();
    }

    public int getEdad() { return edad; }
    public void setEdad(int edad) {
        if (edad < 16 || edad > 60) {
            throw new IllegalArgumentException("La edad debe estar entre 16 y 60: " + edad);
        }
        this.edad = edad;
    }

    public Juego getJuego() { return juego; }
    public void setJuego(Juego juego) {
        if (juego == null) {
            throw new IllegalArgumentException("El juego es obligatorio");
        }
        this.juego = juego;
    }

    public Rol getRol1() { return rol1; }
    public Rol getRol2() { return rol2; }

    // Los dos roles se validan JUNTOS porque las reglas al final dependen de ambos
    public void setRoles(Rol rol1, Rol rol2) throws RolInvalidoException {
        if (rol1 == null) {
            throw new RolInvalidoException("El rol principal es obligatorio");
        }
        if (rol1.getJuego() != juego) {
            throw new RolInvalidoException("El rol " + rol1 + " no es de " + juego);
        }

        if (rol2 == null) {
            if (juego == Juego.League_of_Legends) {
                throw new RolInvalidoException("En League of Legends el segundo rol es obligatorio");
            }
        } else {
            if (rol2.getJuego() != juego) {
                throw new RolInvalidoException("El rol " + rol2 + " no es de " + juego);
            }
            if (rol2 == rol1) {
                throw new RolInvalidoException("Los dos roles no pueden ser el mismo");
            }
        }

        this.rol1 = rol1;
        this.rol2 = rol2;
    }

    public String getEquipo() { return equipo; }
    public void setEquipo(String equipo) {
        if (equipo == null) {
            this.equipo = null;
            this.titular = false;      // un jugador libre no es titular de nadie porque no tiene equipo.
        } else if (equipo.isBlank()) {
            throw new IllegalArgumentException("El nombre del equipo no puede estar vacío");
        } else {
            this.equipo = equipo.trim();
        }
    }

    public boolean isTitular() { return titular; }
    public void setTitular(boolean titular) { this.titular = titular; }

    public boolean isAgenteLibre() { return equipo == null; }

    @Override
    public String toString() {
        return "Jugador{" + nickname + " (" + nombre + " " + apellido1 + "), " + juego
                + ", roles=" + rol1 + (rol2 != null ? "/" + rol2 : "")
                + ", equipo=" + (equipo == null ? "agente libre" : equipo) + "}";
    }
}
