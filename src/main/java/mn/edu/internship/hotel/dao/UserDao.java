package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.model.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserDao {
    Optional<User> findActiveByUsername(String username) throws SQLException;
}

