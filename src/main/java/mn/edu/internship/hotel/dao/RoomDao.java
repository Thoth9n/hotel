package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.model.Room;
import mn.edu.internship.hotel.model.RoomStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoomDao {
    List<Room> findAll(String roomNumber, RoomStatus status) throws SQLException;

    Optional<Room> findById(long id) throws SQLException;
}
