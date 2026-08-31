package mn.edu.internship.hotel.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationDateRulesTest {
    private static final LocalDate JULY_1 = LocalDate.of(2026, 7, 1);
    private static final LocalDate JULY_5 = LocalDate.of(2026, 7, 5);

    @Test
    void rejectsCheckoutOnSameDayAsCheckin() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ReservationDateRules.requireValidStay(JULY_1, JULY_1)
        );
    }

    @Test
    void detectsPartialOverlap() {
        assertTrue(ReservationDateRules.overlaps(
                JULY_1,
                JULY_5,
                LocalDate.of(2026, 7, 4),
                LocalDate.of(2026, 7, 6)
        ));
    }

    @Test
    void allowsBackToBackReservations() {
        assertFalse(ReservationDateRules.overlaps(
                JULY_1,
                JULY_5,
                JULY_5,
                LocalDate.of(2026, 7, 8)
        ));
    }

    @Test
    void rejectsNonPositiveGuestCount() {
        assertThrows(IllegalArgumentException.class,
                () -> ReservationDateRules.requireValidGuestCount(0));
    }
}
