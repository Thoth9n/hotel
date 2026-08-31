package mn.edu.internship.hotel.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Payment(long id, long reservationId, String receiptNumber,
                      BigDecimal amount, PaymentMethod method, LocalDateTime paidAt, String note) {
    public Payment {
        if (amount == null || amount.signum() <= 0 || method == null) {
            throw new IllegalArgumentException("Төлбөрийн мэдээлэл буруу байна.");
        }
    }
}
