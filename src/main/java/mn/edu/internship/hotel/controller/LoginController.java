package mn.edu.internship.hotel.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mn.edu.internship.hotel.config.DatabaseConfig;
import mn.edu.internship.hotel.config.MySqlConnectionFactory;
import mn.edu.internship.hotel.dao.JdbcUserDao;
import mn.edu.internship.hotel.service.AuthService;
import mn.edu.internship.hotel.service.AuthenticationException;
import mn.edu.internship.hotel.session.UserSession;
import mn.edu.internship.hotel.util.BCryptPasswordHasher;
import mn.edu.internship.hotel.util.SceneNavigator;

public final class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final AuthService authService = createAuthService();

    @FXML
    private void handleLogin() {
        errorLabel.setText("");
        loginButton.setDisable(true);

        try {
            var user = authService.authenticate(usernameField.getText(), passwordField.getText());
            UserSession.start(user);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            SceneNavigator.show(stage, "/fxml/dashboard.fxml", "Зочид буудлын систем");
        } catch (AuthenticationException exception) {
            errorLabel.setText(exception.getMessage());
        } finally {
            loginButton.setDisable(false);
        }
    }

    private static AuthService createAuthService() {
        var connectionFactory = new MySqlConnectionFactory(DatabaseConfig.fromEnvironment());
        return new AuthService(new JdbcUserDao(connectionFactory), new BCryptPasswordHasher());
    }
}

