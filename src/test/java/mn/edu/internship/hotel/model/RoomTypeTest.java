package mn.edu.internship.hotel.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomTypeTest {
    @Test
    void rejectsInvalidCapacity() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoomType(1, "SINGLE", 0, BigDecimal.valueOf(120000), ""));
    }

    @Test
    void rejectsInvalidPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoomType(1, "SINGLE", 1, BigDecimal.ZERO, ""));
    }
}
