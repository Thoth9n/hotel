package mn.edu.internship.hotel.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoomStatusTest {
    @Test
    void schemaStatusesAreRepresented() {
        assertEquals(5, RoomStatus.values().length);
        assertEquals(RoomStatus.AVAILABLE, RoomStatus.valueOf("AVAILABLE"));
        assertEquals(RoomStatus.MAINTENANCE, RoomStatus.valueOf("MAINTENANCE"));
    }
}
