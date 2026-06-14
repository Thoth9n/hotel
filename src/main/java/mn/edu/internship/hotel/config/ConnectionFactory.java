package mn.edu.internship.hotel.config;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionFactory {
    Connection openConnection() throws SQLException;
}

