package mn.edu.internship.hotel.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mn.edu.internship.hotel.config.DatabaseConfig;
import mn.edu.internship.hotel.config.MySqlConnectionFactory;
import mn.edu.internship.hotel.dao.JdbcRoomDao;
import mn.edu.internship.hotel.model.Room;
import mn.edu.internship.hotel.model.RoomStatus;
import mn.edu.internship.hotel.session.UserSession;
import mn.edu.internship.hotel.util.SceneNavigator;

import java.math.BigDecimal;
import java.util.List;

public final class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Button logoutButton;
    @FXML private TextField roomNumberFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label roomMessageLabel;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomNumberColumn;
    @FXML private TableColumn<Room, Integer> floorColumn;
    @FXML private TableColumn<Room, String> typeColumn;
    @FXML private TableColumn<Room, Integer> capacityColumn;
    @FXML private TableColumn<Room, BigDecimal> priceColumn;
    @FXML private TableColumn<Room, String> statusColumn;

    @FXML
    private void initialize() {
        UserSession.currentUser().ifPresentOrElse(user -> {
            welcomeLabel.setText("Сайн байна уу, " + user.fullName());
            roleLabel.setText("Эрх: " + user.role());
        }, () -> {
            welcomeLabel.setText("Session дууссан байна.");
            roleLabel.setText("");
        });

        statusFilter.getItems().add("Бүгд");
        for (RoomStatus status : RoomStatus.values()) {
            statusFilter.getItems().add(status.name());
        }
        statusFilter.getSelectionModel().selectFirst();
        roomNumberColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().roomNumber()));
        floorColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().floor()));
        typeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().roomType().name()));
        capacityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().roomType().capacity()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().roomType().basePrice()));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().status().name()));
        loadRooms();
    }

    @FXML
    private void handleSearch() {
        loadRooms();
    }

    @FXML
    private void handleRefresh() {
        roomNumberFilter.clear();
        statusFilter.getSelectionModel().selectFirst();
        loadRooms();
    }

    @FXML
    private void handleLogout() {
        UserSession.clear();
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        SceneNavigator.show(stage, "/fxml/login.fxml", "Зочид буудлын систем");
    }

    private void loadRooms() {
        roomMessageLabel.setText("Өрөөнүүдийг ачаалж байна...");
        Task<List<Room>> task = new Task<>() {
            @Override
            protected List<Room> call() throws Exception {
                String selectedStatus = statusFilter.getSelectionModel().getSelectedItem();
                RoomStatus status = selectedStatus == null || selectedStatus.equals("Бүгд")
                        ? null : RoomStatus.valueOf(selectedStatus);
                return new JdbcRoomDao(new MySqlConnectionFactory(DatabaseConfig.fromEnvironment()))
                        .findAll(roomNumberFilter.getText(), status);
            }
        };
        task.setOnSucceeded(event -> {
            roomsTable.getItems().setAll(task.getValue());
            roomMessageLabel.setText(task.getValue().isEmpty()
                    ? "Тохирох өрөө олдсонгүй."
                    : task.getValue().size() + " өрөө харагдаж байна.");
        });
        task.setOnFailed(event -> roomMessageLabel.setText(
                "Өгөгдлийн сангийн алдаа: " + task.getException().getMessage()));
        Thread thread = new Thread(task, "room-loader");
        thread.setDaemon(true);
        thread.start();
    }
}
