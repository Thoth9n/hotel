package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.model.RoomType;

import java.sql.SQLException;
import java.util.List;

public interface RoomTypeDao {
    List<RoomType> findAll() throws SQLException;
}
