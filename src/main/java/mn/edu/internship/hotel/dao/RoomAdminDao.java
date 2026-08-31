package mn.edu.internship.hotel.dao;

import java.sql.SQLException;

public interface RoomAdminDao {
    long create(String roomNumber, Integer floor, long roomTypeId, String notes) throws SQLException;
    void update(long id, String roomNumber, Integer floor, long roomTypeId, String notes) throws SQLException;
    void deactivate(long id) throws SQLException;
}
