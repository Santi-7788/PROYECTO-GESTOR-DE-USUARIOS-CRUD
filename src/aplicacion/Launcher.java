package aplicacion;

/**
 * Clase lanzadora independiente (Launcher).
 * Esta clase no hereda directamente de Application, lo que permite
 * ejecutar la aplicación JavaFX en JDK moderno sin conflictos del módulo del sistema.
 */
public class Launcher {

    public static void main(String[] args) {
        Main.main(args);
    }
}
