package aplicacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Clase principal que inicializa la aplicación JavaFX.
 * Carga la vista FXML, enlaza la hoja de estilos CSS con colores vivos
 * y configura la ventana principal (Stage).
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carga de la vista FXML con soporte para múltiples rutas de recursos
            URL fxmlLocation = getClass().getResource("/vista/MainView.fxml");
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("vista/MainView.fxml");
            }
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("/MainView.fxml");
            }

            if (fxmlLocation == null) {
                throw new IllegalStateException("No se pudo localizar el archivo MainView.fxml en el classpath.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // Configuración de la escena principal
            Scene scene = new Scene(root, 1140, 740);

            // Cargar estilos CSS con paleta moderna y viva
            URL cssLocation = getClass().getResource("/estilos.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }

            // Propiedades de la ventana
            primaryStage.setTitle("👥 Gestor de Usuarios - CRUD en Memoria (JavaFX)");
            primaryStage.setMinWidth(960);
            primaryStage.setMinHeight(640);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la aplicación JavaFX:");
            e.printStackTrace();
        }
    }

    /**
     * Método de entrada principal.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
