package mn.edu.internship.hotel.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mn.edu.internship.hotel.config.DatabaseConfig;
import mn.edu.internship.hotel.config.MySqlConnectionFactory;
import mn.edu.internship.hotel.dao.JdbcUserDao;
import mn.edu.internship.hotel.model.User;
import mn.edu.internship.hotel.service.AuthService;
import mn.edu.internship.hotel.service.AuthenticationException;
import mn.edu.internship.hotel.session.UserSession;
import mn.edu.internship.hotel.util.BCryptPasswordHasher;
import mn.edu.internship.hotel.util.SceneNavigator;

public final class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private final AuthService authService = createAuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            errorLabel.setText("Нэвтрэх нэр болон нууц үгээ оруулна уу.");
            return;
        }

        errorLabel.setText("Нэвтэрч байна...");
        loginButton.setDisable(true);
        Task<User> task = new Task<>() {
            @Override
            protected User call() {
                return authService.authenticate(username.trim(), password);
            }
        };
        task.setOnSucceeded(event -> {
            UserSession.start(task.getValue());
            Stage stage = (Stage) loginButton.getScene().getWindow();
            SceneNavigator.show(stage, "/fxml/dashboard.fxml", "Зочид буудлын систем");
        });
        task.setOnFailed(event -> {
            Throwable failure = task.getException();
            if (failure instanceof AuthenticationException) {
                errorLabel.setText(failure.getMessage());
            } else {
                errorLabel.setText("Нэвтрэх үед техникийн алдаа гарлаа: " + failure.getMessage());
            }
            loginButton.setDisable(false);
        });

        Thread thread = new Thread(task, "login-task");
        thread.setDaemon(true);
        thread.start();
    }

    private static AuthService createAuthService() {
        var connectionFactory = new MySqlConnectionFactory(DatabaseConfig.fromEnvironment());
        return new AuthService(new JdbcUserDao(connectionFactory), new BCryptPasswordHasher());
    }
}
