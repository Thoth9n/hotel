package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.model.Reservation;
import mn.edu.internship.hotel.model.ReservationStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ReservationDao {
    long create(long customerId, long roomId, LocalDate checkIn, LocalDate checkOut,
                int guestCount, String specialRequest, long createdBy) throws SQLException;

    List<Reservation> findAll(ReservationStatus status) throws SQLException;

    void changeStatus(long reservationId, ReservationStatus status) throws SQLException;
}
