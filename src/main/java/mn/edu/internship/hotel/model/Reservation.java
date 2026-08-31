package mn.edu.internship.hotel.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Reservation(
        long id,
        String reservationCode,
        Customer customer,
        Room room,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int guestCount,
        BigDecimal nightlyRate,
        ReservationStatus status,
        String specialRequest
) {
    public Reservation {
        if (reservationCode == null || reservationCode.isBlank() || customer == null || room == null
                || checkInDate == null || checkOutDate == null || !checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Захиалгын мэдээлэл буруу байна.");
        }
        if (guestCount <= 0 || nightlyRate == null || nightlyRate.signum() <= 0 || status == null) {
            throw new IllegalArgumentException("Захиалгын тоон утга буруу байна.");
        }
    }
}
