package mn.edu.internship.hotel.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mn.edu.internship.hotel.session.UserSession;
import mn.edu.internship.hotel.util.SceneNavigator;

public final class DashboardController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        UserSession.currentUser().ifPresentOrElse(user -> {
            welcomeLabel.setText("Сайн байна уу, " + user.fullName());
            roleLabel.setText("Эрх: " + user.role());
        }, () -> {
            welcomeLabel.setText("Session дууссан байна.");
            roleLabel.setText("");
        });
    }

    @FXML
    private void handleLogout() {
        UserSession.clear();
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        SceneNavigator.show(stage, "/fxml/login.fxml", "Зочид буудлын систем");
    }
}

