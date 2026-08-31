package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.config.ConnectionFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class JdbcRoomAdminDao implements RoomAdminDao {
    private final ConnectionFactory connectionFactory;
    public JdbcRoomAdminDao(ConnectionFactory connectionFactory) { this.connectionFactory = connectionFactory; }

    @Override
    public long create(String roomNumber, Integer floor, long roomTypeId, String notes) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, floor, room_type_id, status, active, notes) VALUES (?, ?, ?, 'AVAILABLE', TRUE, ?)";
        try (var connection = connectionFactory.openConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, roomNumber, floor, roomTypeId, notes);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Өрөөний ID үүссэнгүй.");
                return keys.getLong(1);
            }
        }
    }

    @Override
    public void update(long id, String roomNumber, Integer floor, long roomTypeId, String notes) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, floor = ?, room_type_id = ?, notes = ? WHERE id = ? AND active = TRUE";
        try (var connection = connectionFactory.openConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, roomNumber, floor, roomTypeId, notes);
            statement.setLong(5, id);
            statement.executeUpdate();
        }
    }

    @Override
    public void deactivate(long id) throws SQLException {
        String sql = "UPDATE rooms SET active = FALSE WHERE id = ? AND status NOT IN ('OCCUPIED', 'RESERVED')";
        try (var connection = connectionFactory.openConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            if (statement.executeUpdate() == 0) throw new SQLException("Ашиглагдаж буй өрөөг идэвхгүй болгож болохгүй.");
        }
    }

    private static void bind(PreparedStatement statement, String roomNumber, Integer floor, long roomTypeId, String notes) throws SQLException {
        if (roomNumber == null || roomNumber.isBlank()) throw new IllegalArgumentException("Өрөөний дугаар заавал байна.");
        statement.setString(1, roomNumber.trim());
        if (floor == null) statement.setNull(2, java.sql.Types.INTEGER); else statement.setInt(2, floor);
        statement.setLong(3, roomTypeId);
        statement.setString(4, notes == null || notes.isBlank() ? null : notes.trim());
    }
}
