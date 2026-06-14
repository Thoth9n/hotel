package mn.edu.internship.hotel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MySqlConnectionFactory implements ConnectionFactory {
    private final DatabaseConfig config;

    public MySqlConnectionFactory(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
                config.jdbcUrl(),
                config.username(),
                config.password()
        );
    }
}

