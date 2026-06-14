package mn.edu.internship.hotel;

import javafx.application.Application;
import javafx.stage.Stage;
import mn.edu.internship.hotel.util.SceneNavigator;

public class HotelApplication extends Application {
    @Override
    public void start(Stage stage) {
        SceneNavigator.show(stage, "/fxml/login.fxml", "Зочид буудлын систем");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

