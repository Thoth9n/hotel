package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.model.Room;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityDao {
    List<Room> findAvailable(LocalDate checkIn, LocalDate checkOut, int guestCount) throws SQLException;
}
