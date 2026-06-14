package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.config.ConnectionFactory;
import mn.edu.internship.hotel.model.Role;
import mn.edu.internship.hotel.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class JdbcUserDao implements UserDao {
    private static final String FIND_ACTIVE_BY_USERNAME = """
            SELECT id, username, password_hash, full_name, role, active
            FROM users
            WHERE username = ? AND active = TRUE
            LIMIT 1
            """;

    private final ConnectionFactory connectionFactory;

    public JdbcUserDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Optional<User> findActiveByUsername(String username) throws SQLException {
        try (var connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ACTIVE_BY_USERNAME)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password_hash"),
                        resultSet.getString("full_name"),
                        Role.valueOf(resultSet.getString("role")),
                        resultSet.getBoolean("active")
                ));
            }
        }
    }
}

