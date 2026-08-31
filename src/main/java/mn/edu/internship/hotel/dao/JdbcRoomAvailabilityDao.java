package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.config.ConnectionFactory;
import mn.edu.internship.hotel.model.Room;
import mn.edu.internship.hotel.model.RoomStatus;
import mn.edu.internship.hotel.model.RoomType;
import mn.edu.internship.hotel.util.ReservationDateRules;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class JdbcRoomAvailabilityDao implements RoomAvailabilityDao {
    private static final String SQL = """
            SELECT r.id, r.room_number, r.floor, r.status, r.active, r.notes,
                   rt.id AS room_type_id, rt.name AS room_type_name,
                   rt.capacity, rt.base_price, rt.description
            FROM rooms r
            JOIN room_types rt ON rt.id = r.room_type_id
            WHERE r.active = TRUE
              AND r.status = 'AVAILABLE'
              AND rt.capacity >= ?
              AND NOT EXISTS (
                  SELECT 1 FROM reservations existing
                  WHERE existing.room_id = r.id
                    AND existing.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
                    AND existing.check_in_date < ?
                    AND existing.check_out_date > ?
              )
            ORDER BY r.room_number
            """;

    private final ConnectionFactory connectionFactory;

    public JdbcRoomAvailabilityDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Room> findAvailable(LocalDate checkIn, LocalDate checkOut, int guestCount) throws SQLException {
        ReservationDateRules.requireValidStay(checkIn, checkOut);
        ReservationDateRules.requireValidGuestCount(guestCount);
        List<Room> rooms = new ArrayList<>();
        try (var connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setInt(1, guestCount);
            statement.setObject(2, checkOut);
            statement.setObject(3, checkIn);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rooms.add(map(resultSet));
                }
            }
        }
        return List.copyOf(rooms);
    }

    private static Room map(ResultSet resultSet) throws SQLException {
        RoomType type = new RoomType(resultSet.getLong("room_type_id"),
                resultSet.getString("room_type_name"), resultSet.getInt("capacity"),
                resultSet.getBigDecimal("base_price"), resultSet.getString("description"));
        return new Room(resultSet.getLong("id"), resultSet.getString("room_number"),
                (Integer) resultSet.getObject("floor"), type,
                RoomStatus.valueOf(resultSet.getString("status")),
                resultSet.getBoolean("active"), resultSet.getString("notes"));
    }
}
