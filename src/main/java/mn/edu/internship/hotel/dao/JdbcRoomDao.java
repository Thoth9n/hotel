package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.config.ConnectionFactory;
import mn.edu.internship.hotel.model.Room;
import mn.edu.internship.hotel.model.RoomStatus;
import mn.edu.internship.hotel.model.RoomType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JdbcRoomDao implements RoomDao {
    private static final String SELECT_COLUMNS = """
            SELECT r.id, r.room_number, r.floor, r.status, r.active, r.notes,
                   rt.id AS room_type_id, rt.name AS room_type_name,
                   rt.capacity, rt.base_price, rt.description
            FROM rooms r
            JOIN room_types rt ON rt.id = r.room_type_id
            WHERE r.active = TRUE
            """;

    private final ConnectionFactory connectionFactory;

    public JdbcRoomDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Room> findAll(String roomNumber, RoomStatus status) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_COLUMNS);
        List<Object> parameters = new ArrayList<>();
        if (roomNumber != null && !roomNumber.isBlank()) {
            sql.append(" AND r.room_number LIKE ?");
            parameters.add("%" + roomNumber.trim() + "%");
        }
        if (status != null) {
            sql.append(" AND r.status = ?");
            parameters.add(status.name());
        }
        sql.append(" ORDER BY r.room_number");

        List<Room> rooms = new ArrayList<>();
        try (var connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rooms.add(map(resultSet));
                }
            }
        }
        return List.copyOf(rooms);
    }

    @Override
    public Optional<Room> findById(long id) throws SQLException {
        String sql = SELECT_COLUMNS + " AND r.id = ?";
        try (var connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
            }
        }
    }

    private static Room map(ResultSet resultSet) throws SQLException {
        RoomType type = new RoomType(
                resultSet.getLong("room_type_id"),
                resultSet.getString("room_type_name"),
                resultSet.getInt("capacity"),
                resultSet.getBigDecimal("base_price"),
                resultSet.getString("description")
        );
        return new Room(
                resultSet.getLong("id"),
                resultSet.getString("room_number"),
                (Integer) resultSet.getObject("floor"),
                type,
                RoomStatus.valueOf(resultSet.getString("status")),
                resultSet.getBoolean("active"),
                resultSet.getString("notes")
        );
    }
}
