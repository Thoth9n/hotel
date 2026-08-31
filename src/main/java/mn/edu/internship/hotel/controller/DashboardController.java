package mn.edu.internship.hotel.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.stage.Stage;
import mn.edu.internship.hotel.config.DatabaseConfig;
import mn.edu.internship.hotel.config.MySqlConnectionFactory;
import mn.edu.internship.hotel.dao.JdbcRoomDao;
import mn.edu.internship.hotel.dao.JdbcRoomAdminDao;
import mn.edu.internship.hotel.dao.JdbcRoomTypeDao;
import mn.edu.internship.hotel.dao.JdbcCustomerDao;
import mn.edu.internship.hotel.dao.JdbcReservationDao;
import mn.edu.internship.hotel.dao.JdbcRoomAvailabilityDao;
import mn.edu.internship.hotel.dao.JdbcPaymentDao;
import mn.edu.internship.hotel.model.Customer;
import mn.edu.internship.hotel.model.Reservation;
import mn.edu.internship.hotel.model.ReservationStatus;
import mn.edu.internship.hotel.model.PaymentMethod;
import mn.edu.internship.hotel.model.RoomType;
import mn.edu.internship.hotel.model.Room;
import mn.edu.internship.hotel.model.RoomStatus;
import mn.edu.internship.hotel.session.UserSession;
import mn.edu.internship.hotel.util.SceneNavigator;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

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
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> reservationCodeColumn;
    @FXML private TableColumn<Reservation, String> reservationCustomerColumn;
    @FXML private TableColumn<Reservation, String> reservationRoomColumn;
    @FXML private TableColumn<Reservation, String> reservationDatesColumn;
    @FXML private TableColumn<Reservation, String> reservationStatusColumn;

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
        reservationCodeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().reservationCode()));
        reservationCustomerColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().customer().fullName()));
        reservationRoomColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().room().roomNumber()));
        reservationDatesColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().checkInDate() + " - " + data.getValue().checkOutDate()));
        reservationStatusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().status().name()));
        loadReservations();
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

    @FXML private void handleRefreshReservations() { loadReservations(); }

    @FXML private void handleAddCustomer() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Үйлчлүүлэгч бүртгэх");
        ButtonType save = new ButtonType("Хадгалах", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        TextField first = new TextField(), last = new TextField(), phone = new TextField(), email = new TextField();
        GridPane form = new GridPane(); form.setHgap(12); form.setVgap(12);
        form.addRow(0, new Label("Нэр *"), first); form.addRow(1, new Label("Овог *"), last);
        form.addRow(2, new Label("Утас *"), phone); form.addRow(3, new Label("Имэйл"), email);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> button == save ? button : null);
        dialog.showAndWait().ifPresent(button -> {
            try {
                new JdbcCustomerDao(new MySqlConnectionFactory(DatabaseConfig.fromEnvironment()))
                        .save(new Customer(0, first.getText(), last.getText(), phone.getText(), email.getText(), null, null, null));
                showInfo("Амжилттай", "Үйлчлүүлэгч бүртгэгдлээ.");
            } catch (Exception exception) { showError("Үйлчлүүлэгч хадгалахад алдаа гарлаа", exception); }
        });
    }

    @FXML private void handleAddReservation() {
        try {
            var factory = new MySqlConnectionFactory(DatabaseConfig.fromEnvironment());
            List<Customer> customers = new JdbcCustomerDao(factory).search("");
            if (customers.isEmpty()) { showError("Захиалга үүсгэх боломжгүй", new IllegalArgumentException("Эхлээд үйлчлүүлэгч бүртгэнэ үү.")); return; }
            Dialog<ButtonType> dialog = new Dialog<>(); dialog.setTitle("Захиалга үүсгэх");
            ButtonType save = new ButtonType("Захиалах", ButtonBar.ButtonData.OK_DONE); dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
            ComboBox<Customer> customer = new ComboBox<>(); customer.getItems().setAll(customers); customer.setPromptText("Үйлчлүүлэгч");
            customer.setCellFactory(list -> customerCell()); customer.setButtonCell(customerCell());
            DatePicker in = new DatePicker(LocalDate.now()), out = new DatePicker(LocalDate.now().plusDays(1));
            TextField guests = new TextField("1"), request = new TextField(); ComboBox<Room> room = new ComboBox<>(); room.setPromptText("Огноо сонгосны дараа өрөө сонгоно");
            room.setCellFactory(list -> roomCell()); room.setButtonCell(roomCell());
            Button find = new Button("Боломжит өрөө хайх"); find.setOnAction(event -> {
                try { room.getItems().setAll(new JdbcRoomAvailabilityDao(factory).findAvailable(in.getValue(), out.getValue(), Integer.parseInt(guests.getText()))); }
                catch (Exception e) { showError("Өрөө хайхад алдаа гарлаа", e); }
            });
            GridPane form = new GridPane(); form.setHgap(12); form.setVgap(12);
            form.addRow(0, new Label("Үйлчлүүлэгч"), customer); form.addRow(1, new Label("Check-in"), in); form.addRow(2, new Label("Check-out"), out);
            form.addRow(3, new Label("Зочдын тоо"), guests); form.addRow(4, new Label("Өрөө"), room); form.addRow(5, new Label("Хүсэлт"), request); form.add(find, 1, 6);
            dialog.getDialogPane().setContent(form); dialog.setResultConverter(button -> button == save ? button : null);
            dialog.showAndWait().ifPresent(button -> { try {
                if (room.getValue() == null || customer.getValue() == null) throw new IllegalArgumentException("Үйлчлүүлэгч болон өрөө сонгоно уу.");
                long userId = UserSession.currentUser().orElseThrow().id();
                new JdbcReservationDao(factory).create(customer.getValue().id(), room.getValue().id(), in.getValue(), out.getValue(), Integer.parseInt(guests.getText()), request.getText(), userId);
                loadRooms(); loadReservations(); showInfo("Амжилттай", "Захиалга үүсгэлээ.");
            } catch (Exception e) { showError("Захиалга үүсгэхэд алдаа гарлаа", e); } });
        } catch (Exception exception) { showError("Захиалга эхлүүлэхэд алдаа гарлаа", exception); }
    }

    @FXML
    private void handleAddReservationMulti() {
        try {
            var factory = new MySqlConnectionFactory(DatabaseConfig.fromEnvironment());
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Олон өрөөний захиалга");
            ButtonType save = new ButtonType("Захиалах", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

            TextField first = new TextField(), last = new TextField(), phone = new TextField(), email = new TextField();
            DatePicker in = new DatePicker(LocalDate.now()), out = new DatePicker(LocalDate.now().plusDays(1));
            TextField guests = new TextField("1"), request = new TextField();
            ListView<Room> rooms = new ListView<>();
            rooms.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            rooms.setPrefHeight(150);
            rooms.setCellFactory(list -> roomCell());
            Button find = new Button("Боломжит өрөөнүүд хайх");
            find.setOnAction(event -> {
                try { rooms.getItems().setAll(new JdbcRoomAvailabilityDao(factory).findAvailable(in.getValue(), out.getValue(), 1)); }
                catch (Exception exception) { showError("Өрөө хайхад алдаа гарлаа", exception); }
            });
            GridPane form = new GridPane(); form.setHgap(12); form.setVgap(10);
            form.addRow(0, new Label("Нэр *"), first); form.addRow(1, new Label("Овог *"), last);
            form.addRow(2, new Label("Утас *"), phone); form.addRow(3, new Label("Имэйл"), email);
            form.addRow(4, new Label("Check-in"), in); form.addRow(5, new Label("Check-out"), out);
            form.addRow(6, new Label("Зочдын тоо"), guests); form.addRow(7, new Label("Өрөөнүүд"), rooms);
            form.addRow(8, new Label("Хүсэлт"), request); form.add(find, 1, 9);
            dialog.getDialogPane().setContent(form); dialog.setResultConverter(button -> button == save ? button : null);
            dialog.showAndWait().ifPresent(button -> {
                try {
                    List<Room> selectedRooms = List.copyOf(rooms.getSelectionModel().getSelectedItems());
                    if (selectedRooms.isEmpty()) throw new IllegalArgumentException("Нэг буюу хэд хэдэн өрөө сонгоно уу.");
                    Customer customer = new Customer(0, first.getText(), last.getText(), phone.getText(), email.getText(), null, null, null);
                    long customerId = new JdbcCustomerDao(factory).save(customer);
                    long userId = UserSession.currentUser().orElseThrow().id();
                    int count = Integer.parseInt(guests.getText());
                    int totalCapacity = selectedRooms.stream().mapToInt(room -> room.roomType().capacity()).sum();
                    if (totalCapacity < count) throw new IllegalArgumentException("Сонгосон өрөөнүүдийн нийт багтаамж хүрэлцэхгүй байна.");
                    JdbcReservationDao reservations = new JdbcReservationDao(factory);
                    int remainingGuests = count;
                    for (Room room : selectedRooms) {
                        int assignedGuests = Math.min(room.roomType().capacity(), remainingGuests);
                        reservations.create(customerId, room.id(), in.getValue(), out.getValue(), assignedGuests, request.getText(), userId);
                        remainingGuests -= assignedGuests;
                    }
                    loadRooms(); loadReservations(); showInfo("Амжилттай", selectedRooms.size() + " өрөөний захиалга үүсгэлээ.");
                } catch (Exception exception) { showError("Захиалга үүсгэхэд алдаа гарлаа", exception); }
            });
        } catch (Exception exception) { showError("Захиалга эхлүүлэхэд алдаа гарлаа", exception); }
    }

    @FXML private void handleCheckIn() { changeSelectedReservationStatus(ReservationStatus.CHECKED_IN); }
    @FXML private void handleCheckOut() { changeSelectedReservationStatus(ReservationStatus.CHECKED_OUT); }
    @FXML private void handlePayment() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Төлбөр", new IllegalArgumentException("Эхлээд захиалга сонгоно уу.")); return; }
        TextInputDialog input = new TextInputDialog(); input.setTitle("Төлбөр бүртгэх"); input.setHeaderText("Төлөх дүнг оруулна уу");
        input.showAndWait().ifPresent(value -> { try {
            new JdbcPaymentDao(new MySqlConnectionFactory(DatabaseConfig.fromEnvironment())).save(selected.id(), new java.math.BigDecimal(value), PaymentMethod.CASH.name(), UserSession.currentUser().orElseThrow().id(), null);
            showInfo("Амжилттай", "Төлбөр бүртгэгдлээ.");
        } catch (Exception e) { showError("Төлбөр бүртгэхэд алдаа гарлаа", e); } });
    }

    @FXML
    private void handleAddRoom() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Шинэ өрөө нэмэх");
        dialog.setHeaderText("Өрөөний мэдээллийг оруулна уу");
        ButtonType save = new ButtonType("Хадгалах", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        TextField number = new TextField();
        number.setPromptText("Жишээ: 301");
        TextField floor = new TextField();
        floor.setPromptText("Жишээ: 3");
        ComboBox<RoomType> type = new ComboBox<>();
        TextField notes = new TextField();
        notes.setPromptText("Нэмэлт тэмдэглэл");
        try {
            type.getItems().setAll(new JdbcRoomTypeDao(new MySqlConnectionFactory(DatabaseConfig.fromEnvironment())).findAll());
        } catch (Exception exception) {
            showError("Өрөөний төрөл ачааллахад алдаа гарлаа", exception);
        }
        type.setPromptText("Өрөөний төрөл");
        type.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(RoomType item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.name()); }
        });
        type.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(RoomType item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.name()); }
        });
        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(12); form.setAlignment(Pos.CENTER_LEFT);
        form.addRow(0, new Label("Дугаар"), number);
        form.addRow(1, new Label("Давхар"), floor);
        form.addRow(2, new Label("Төрөл"), type);
        form.addRow(3, new Label("Тэмдэглэл"), notes);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> button == save ? button : null);
        dialog.showAndWait().ifPresent(button -> {
            try {
                Integer floorValue = floor.getText().isBlank() ? null : Integer.valueOf(floor.getText().trim());
                if (floorValue != null && (floorValue < 0 || floorValue > 100)) throw new IllegalArgumentException("Давхар 0-100 хооронд байна.");
                if (type.getValue() == null) throw new IllegalArgumentException("Өрөөний төрөл сонгоно уу.");
                new JdbcRoomAdminDao(new MySqlConnectionFactory(DatabaseConfig.fromEnvironment()))
                        .create(number.getText(), floorValue, type.getValue().id(), notes.getText());
                loadRooms();
            } catch (Exception exception) {
                showError("Өрөө хадгалахад алдаа гарлаа", exception);
            }
        });
    }

    private void showError(String header, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Алдаа");
        alert.setHeaderText(header);
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    private void showInfo(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hotel"); alert.setHeaderText(header); alert.setContentText(message); alert.showAndWait();
    }

    private void loadReservations() {
        try {
            var factory = new MySqlConnectionFactory(DatabaseConfig.fromEnvironment());
            reservationsTable.getItems().setAll(new JdbcReservationDao(factory).findAll(null));
        } catch (Exception exception) { roomMessageLabel.setText("Захиалга ачаалахад алдаа: " + exception.getMessage()); }
    }

    private void changeSelectedReservationStatus(ReservationStatus status) {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Захиалга", new IllegalArgumentException("Эхлээд захиалга сонгоно уу.")); return; }
        try {
            new JdbcReservationDao(new MySqlConnectionFactory(DatabaseConfig.fromEnvironment())).changeStatus(selected.id(), status);
            loadReservations(); loadRooms(); showInfo("Амжилттай", status == ReservationStatus.CHECKED_IN ? "Check-in хийлээ." : "Check-out хийлээ.");
        } catch (Exception exception) { showError("Төлөв шинэчлэхэд алдаа гарлаа", exception); }
    }

    private javafx.scene.control.ListCell<Customer> customerCell() {
        return new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Customer item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.fullName() + " - " + item.phone()); }
        };
    }

    private javafx.scene.control.ListCell<Room> roomCell() {
        return new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Room item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.roomNumber() + " / " + item.roomType().name()); }
        };
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
