package mn.edu.internship.hotel.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class SceneNavigator {
    private static final String STYLESHEET = "/css/app.css";

    private SceneNavigator() {
    }

    public static void show(Stage stage, String fxmlPath, String title) {
        try {
            URL resource = SceneNavigator.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalStateException("FXML resource олдсонгүй: " + fxmlPath);
            }

            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            URL stylesheet = SceneNavigator.class.getResource(STYLESHEET);
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }

            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Дэлгэц ачаалж чадсангүй: " + fxmlPath, exception);
        }
    }
}

