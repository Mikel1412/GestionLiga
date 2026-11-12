
# Gestion de Liga
## Cosas a corregir
-Crear los enumerados que se usan en mas de una clase de forma externa en un fichero:<br>
-Corregir las runtimeException como illegalArgumentException en model/torneo.java:<br>
-Añadir constantes(limite de jugadores), podriamos crear un fichero de config:<br>

## Cómo usar la aplicación

GestionLiga es una aplicación de consola para gestionar una liga de esports (**Valorant** y **League of Legends**): jugadores, equipos, torneos y partidas. Todo se guarda en ficheros JSON, así que los datos se conservan al cerrar el programa.

### Requisitos

- Java 25
- Maven

### Cómo ejecutarla

1. Clona el repositorio y ábrelo en tu IDE (IntelliJ, NetBeans, Eclipse...) como proyecto Maven.
2. Deja que Maven descargue las dependencias.
3. Ejecuta la clase `es.esports.Main`.

> **Importante:** el programa debe ejecutarse con la **raíz del proyecto** como directorio de trabajo, porque guarda los datos en la carpeta relativa `datos/`. La carpeta y los ficheros se crean solos la primera vez.

### Menú principal

Al arrancar aparece el menú. Se elige una opción escribiendo su número y pulsando Enter; `0` sale o vuelve al menú anterior.

```
=== GESTIÓN DE LIGA ===
1. Jugadores
2. Equipos
3. Torneos
4. Partidas
5. Ficheros
0. Salir
```

| Opción | Qué permite hacer |
|--------|-------------------|
| **1. Jugadores** | Crear, ver, listar y borrar jugadores; cambiar edad y roles; marcar titular o suplente; fichar, liberar y traspasar; ver agentes libres, jugadores por juego y la plantilla de un equipo; contar jugadores. |
| **2. Equipos** | Crear, ver, listar y borrar equipos; cambiar tag y organización; buscar por juego, por nombre o por organización. |
| **3. Torneos** | Crear, ver, listar y borrar torneos; inscribir equipos; iniciar y finalizar; filtrar por juego o por estado. |
| **4. Partidas** | Listar y ver partidas; filtrar por torneo, equipo o estado; ver la próxima partida de un equipo; añadir jugadores a una partida; registrar resultados; consultar partidas ganadas y jugadas. |
| **5. Ficheros** | Ver el contenido de una carpeta, mover un fichero y renombrar una carpeta. |

### Flujo recomendado (de cero a un torneo terminado)

1. **Crear los equipos** (`2 → 1`): nombre, tag (2 a 5 caracteres), juego y organización.
2. **Crear los jugadores** (`1 → 1`): datos personales, juego y roles. Todo jugador se crea como **agente libre** (sin equipo).
3. **Fichar a los jugadores** (`1 → 8`): se indica el nickname del jugador y el nombre del equipo. Un equipo necesita al menos **5 jugadores** del mismo juego.
4. **Marcar a los titulares** (`1 → 6`): cada equipo necesita **exactamente 5 titulares** para poder inscribirse en un torneo.
5. **Crear el torneo** (`3 → 1`): nombre, juego, formato (BO1, BO3 o BO5), fechas de inicio y fin y máximo de equipos.
6. **Inscribir los equipos** (`3 → 5`): solo mientras el torneo está en preparación.
7. **Iniciar el torneo** (`3 → 6`): se generan automáticamente las partidas, todos contra todos. Hacen falta al menos 2 equipos.
8. **Preparar y jugar las partidas** (`4`): añade jugadores a cada partida (`4 → 7`) y registra el equipo ganador cuando termine (`4 → 8`).
9. **Finalizar el torneo** (`3 → 7`): solo es posible cuando **todas** las partidas tienen resultado.

### Reglas que conviene conocer

- **Roles por juego.** Cada juego tiene sus propios roles:
  - Valorant: Centinela, Iniciador, Duelista y Controlador. El segundo rol es **opcional**.
  - League of Legends: Top, Jungla, Mid, ADC y Support. El segundo rol es **obligatorio**.
  - Los dos roles de un jugador no pueden ser el mismo.
- **Identificadores.** Un jugador se identifica por su **nickname**, y un equipo o un torneo por su **nombre**. No se distingue entre mayúsculas y minúsculas.
- **Un jugador solo puede estar en un equipo**, y debe ser del mismo juego que el equipo. Para cambiarlo de equipo, usa *Traspasar*.
- **Un jugador libre no puede ser titular.** Si se libera a un jugador, deja de ser titular.
- **Fechas** en formato `aaaa-mm-dd` (por ejemplo, `2026-12-15`). La fecha final no puede ser anterior a la inicial.
- **Edad** de los jugadores: entre 16 y 60 años.
- **Un torneo activo no se puede borrar**, y las inscripciones se cierran al iniciarlo.
- Cada partida admite como máximo **5 jugadores por equipo**, y estos deben pertenecer a uno de los dos equipos que juegan.

### Dónde se guardan los datos

| Fichero | Contenido |
|---------|-----------|
| `datos/jugador.json` | Jugadores |
| `datos/equipos.json` | Equipos |
| `datos/torneos.json` | Torneos |
| `datos/partidas.json` | Partidas |

Son ficheros de texto en formato JSON. Para **empezar de cero**, cierra la aplicación y borra la carpeta `datos/`.

> **Aviso:** no muevas ni renombres la carpeta `datos/` (opción *5. Ficheros*) mientras la aplicación está abierta: los ficheros dejarían de encontrarse hasta que la devuelvas a su sitio o reinicies el programa.

### Si algo falla

Cuando una operación no es válida (un jugador que no existe, un equipo repetido, un torneo sin equipos suficientes...), la aplicación muestra el motivo en pantalla y vuelve al menú sin cerrarse. Puedes corregir el dato e intentarlo de nuevo.
