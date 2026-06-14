package mn.edu.internship.hotel.util;

import java.time.LocalDate;

public final class ReservationDateRules {
    private ReservationDateRules() {
    }

    public static void requireValidStay(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Орох болон гарах огноо заавал шаардлагатай.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Гарах огноо орох огнооноос хойш байна.");
        }
    }

    public static boolean overlaps(
            LocalDate existingCheckIn,
            LocalDate existingCheckOut,
            LocalDate requestedCheckIn,
            LocalDate requestedCheckOut
    ) {
        requireValidStay(existingCheckIn, existingCheckOut);
        requireValidStay(requestedCheckIn, requestedCheckOut);
        return existingCheckIn.isBefore(requestedCheckOut)
                && existingCheckOut.isAfter(requestedCheckIn);
    }
}

