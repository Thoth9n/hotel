package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.config.ConnectionFactory;
import mn.edu.internship.hotel.model.ReservationStatus;
import mn.edu.internship.hotel.model.Customer;
import mn.edu.internship.hotel.model.Room;
import mn.edu.internship.hotel.model.RoomStatus;
import mn.edu.internship.hotel.model.RoomType;
import mn.edu.internship.hotel.util.ReservationDateRules;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public final class JdbcReservationDao implements ReservationDao {
    private final ConnectionFactory connectionFactory;

    public JdbcReservationDao(ConnectionFactory connectionFactory) { this.connectionFactory = connectionFactory; }

    @Override
    public long create(long customerId, long roomId, LocalDate checkIn, LocalDate checkOut,
                       int guestCount, String specialRequest, long createdBy) throws SQLException {
        ReservationDateRules.requireValidStay(checkIn, checkOut);
        ReservationDateRules.requireValidGuestCount(guestCount);
        try (var connection = connectionFactory.openConnection()) {
            connection.setAutoCommit(false);
            try {
                String available = """
                        SELECT rm.id FROM rooms rm JOIN room_types rt ON rt.id = rm.room_type_id
                        WHERE rm.id = ? AND rm.active = TRUE AND rm.status = 'AVAILABLE' AND rt.capacity >= ?
                          AND NOT EXISTS (SELECT 1 FROM reservations r WHERE r.room_id = rm.id
                            AND r.status IN ('PENDING','CONFIRMED','CHECKED_IN')
                            AND r.check_in_date < ? AND r.check_out_date > ?) FOR UPDATE
                        """;
                try (PreparedStatement check = connection.prepareStatement(available)) {
                    check.setLong(1, roomId); check.setInt(2, guestCount);
                    check.setObject(3, checkOut); check.setObject(4, checkIn);
                    try (ResultSet result = check.executeQuery()) {
                        if (!result.next()) throw new SQLException("Энэ хугацаанд сонгосон өрөө боломжгүй байна.");
                    }
                }
                String insert = """
                        INSERT INTO reservations (reservation_code, customer_id, room_id, check_in_date,
                            check_out_date, guest_count, nightly_rate, status, special_request, created_by)
                        SELECT ?, ?, ?, ?, ?, ?, rt.base_price, 'PENDING', ?, ?
                        FROM rooms rm JOIN room_types rt ON rt.id = rm.room_type_id WHERE rm.id = ?
                        """;
                try (PreparedStatement statement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, "RES-" + System.currentTimeMillis()); statement.setLong(2, customerId);
                    statement.setLong(3, roomId); statement.setObject(4, checkIn); statement.setObject(5, checkOut);
                    statement.setInt(6, guestCount); statement.setString(7, blankToNull(specialRequest));
                    statement.setLong(8, createdBy); statement.setLong(9, roomId); statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Захиалгын ID үүссэнгүй.");
                        connection.commit(); return keys.getLong(1);
                    }
                }
            } catch (SQLException exception) { connection.rollback(); throw exception; }
            finally { connection.setAutoCommit(true); }
        }
    }

    @Override
    public java.util.List<mn.edu.internship.hotel.model.Reservation> findAll(ReservationStatus status) throws SQLException {
        String sql = """
                SELECT r.id, r.reservation_code, r.check_in_date, r.check_out_date, r.guest_count,
                       r.nightly_rate, r.status, r.special_request,
                       c.id customer_id, c.first_name, c.last_name, c.phone, c.email,
                       c.document_type, c.document_number, c.address,
                       rm.id room_id, rm.room_number, rm.floor, rm.status room_status, rm.active, rm.notes,
                       rt.id room_type_id, rt.name room_type_name, rt.capacity, rt.base_price, rt.description
                FROM reservations r JOIN customers c ON c.id = r.customer_id
                JOIN rooms rm ON rm.id = r.room_id JOIN room_types rt ON rt.id = rm.room_type_id
                """ + (status == null ? "" : " WHERE r.status = ?")
                + " ORDER BY r.check_in_date DESC, r.id DESC";
        java.util.List<mn.edu.internship.hotel.model.Reservation> result = new java.util.ArrayList<>();
        try (var connection = connectionFactory.openConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (status != null) statement.setString(1, status.name());
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Customer customer = new Customer(rows.getLong("customer_id"), rows.getString("first_name"), rows.getString("last_name"), rows.getString("phone"), rows.getString("email"), rows.getString("document_type"), rows.getString("document_number"), rows.getString("address"));
                    RoomType type = new RoomType(rows.getLong("room_type_id"), rows.getString("room_type_name"), rows.getInt("capacity"), rows.getBigDecimal("base_price"), rows.getString("description"));
                    Room room = new Room(rows.getLong("room_id"), rows.getString("room_number"), (Integer) rows.getObject("floor"), type, RoomStatus.valueOf(rows.getString("room_status")), rows.getBoolean("active"), rows.getString("notes"));
                    result.add(new mn.edu.internship.hotel.model.Reservation(rows.getLong("id"), rows.getString("reservation_code"), customer, room, rows.getObject("check_in_date", LocalDate.class), rows.getObject("check_out_date", LocalDate.class), rows.getInt("guest_count"), rows.getBigDecimal("nightly_rate"), ReservationStatus.valueOf(rows.getString("status")), rows.getString("special_request")));
                }
            }
        }
        return java.util.List.copyOf(result);
    }

    @Override
    public void changeStatus(long reservationId, ReservationStatus status) throws SQLException {
        try (var connection = connectionFactory.openConnection()) {
            connection.setAutoCommit(false);
            try {
                long roomId;
                try (PreparedStatement find = connection.prepareStatement("SELECT room_id FROM reservations WHERE id = ? FOR UPDATE")) {
                    find.setLong(1, reservationId);
                    try (ResultSet result = find.executeQuery()) {
                        if (!result.next()) throw new SQLException("Захиалга олдсонгүй.");
                        roomId = result.getLong(1);
                    }
                }
                String update = "UPDATE reservations SET status = ?, actual_check_in_at = "
                        + (status == ReservationStatus.CHECKED_IN ? "CURRENT_TIMESTAMP" : "actual_check_in_at")
                        + ", actual_check_out_at = "
                        + (status == ReservationStatus.CHECKED_OUT ? "CURRENT_TIMESTAMP" : "actual_check_out_at")
                        + " WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(update)) {
                    statement.setString(1, status.name()); statement.setLong(2, reservationId); statement.executeUpdate();
                }
                String roomStatus = switch (status) {
                    case CHECKED_IN -> "OCCUPIED";
                    case CHECKED_OUT -> "AVAILABLE";
                    case PENDING, CONFIRMED -> "RESERVED";
                    default -> null;
                };
                if (roomStatus != null) try (PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET status = ? WHERE id = ?")) {
                    statement.setString(1, roomStatus); statement.setLong(2, roomId); statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException exception) { connection.rollback(); throw exception; }
            finally { connection.setAutoCommit(true); }
        }
    }

    private static String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
