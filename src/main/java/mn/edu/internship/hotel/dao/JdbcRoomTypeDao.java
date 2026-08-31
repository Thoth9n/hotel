package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.config.ConnectionFactory;
import mn.edu.internship.hotel.model.RoomType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class JdbcRoomTypeDao implements RoomTypeDao {
    private static final String FIND_ALL = """
            SELECT id, name, capacity, base_price, description
            FROM room_types
            ORDER BY name
            """;

    private final ConnectionFactory connectionFactory;

    public JdbcRoomTypeDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<RoomType> findAll() throws SQLException {
        List<RoomType> roomTypes = new ArrayList<>();
        try (var connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                roomTypes.add(new RoomType(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("capacity"),
                        resultSet.getBigDecimal("base_price"),
                        resultSet.getString("description")
                ));
            }
        }
        return List.copyOf(roomTypes);
    }
}
