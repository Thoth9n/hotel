package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.config.ConnectionFactory;
import mn.edu.internship.hotel.model.Payment;
import mn.edu.internship.hotel.model.PaymentMethod;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class JdbcPaymentDao implements PaymentDao {
    private final ConnectionFactory connectionFactory;
    public JdbcPaymentDao(ConnectionFactory connectionFactory) { this.connectionFactory = connectionFactory; }

    @Override
    public long save(long reservationId, BigDecimal amount, String method, long receivedBy, String note) throws SQLException {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Төлбөр 0-ээс их байна.");
        PaymentMethod.valueOf(method);
        String sql = "INSERT INTO payments (reservation_id, receipt_number, amount, method, received_by, note) VALUES (?, ?, ?, ?, ?, ?)";
        try (var connection = connectionFactory.openConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, reservationId); statement.setString(2, "PAY-" + System.currentTimeMillis());
            statement.setBigDecimal(3, amount); statement.setString(4, method); statement.setLong(5, receivedBy);
            statement.setString(6, note == null || note.isBlank() ? null : note.trim());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Төлбөрийн баримтын дугаар үүссэнгүй.");
                return keys.getLong(1);
            }
        }
    }

    @Override
    public List<Payment> findByReservation(long reservationId) throws SQLException {
        String sql = "SELECT id, reservation_id, receipt_number, amount, method, paid_at, note FROM payments WHERE reservation_id = ? ORDER BY paid_at DESC";
        List<Payment> payments = new ArrayList<>();
        try (var connection = connectionFactory.openConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reservationId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) payments.add(new Payment(result.getLong("id"), result.getLong("reservation_id"), result.getString("receipt_number"), result.getBigDecimal("amount"), PaymentMethod.valueOf(result.getString("method")), result.getTimestamp("paid_at").toLocalDateTime(), result.getString("note")));
            }
        }
        return List.copyOf(payments);
    }

    @Override
    public BigDecimal totalPaid(long reservationId) throws SQLException {
        try (var connection = connectionFactory.openConnection(); PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE reservation_id = ?")) {
            statement.setLong(1, reservationId);
            try (ResultSet result = statement.executeQuery()) { result.next(); return result.getBigDecimal(1); }
        }
    }
}
