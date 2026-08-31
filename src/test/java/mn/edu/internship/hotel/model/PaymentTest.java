package mn.edu.internship.hotel.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentTest {
    @Test
    void rejectsNonPositivePayment() {
        assertThrows(IllegalArgumentException.class, () -> new Payment(0, 1, "PAY-1", BigDecimal.ZERO, PaymentMethod.CASH, null, null));
    }
}
