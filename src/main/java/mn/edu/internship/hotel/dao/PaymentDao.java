package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.model.Payment;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface PaymentDao {
    long save(long reservationId, BigDecimal amount, String method, long receivedBy, String note) throws SQLException;
    List<Payment> findByReservation(long reservationId) throws SQLException;
    BigDecimal totalPaid(long reservationId) throws SQLException;
}
