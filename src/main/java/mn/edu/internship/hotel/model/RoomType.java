package mn.edu.internship.hotel.model;

import java.math.BigDecimal;

public record RoomType(
        long id,
        String name,
        int capacity,
        BigDecimal basePrice,
        String description
) {
    public RoomType {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Өрөөний төрлийн нэр хоосон байж болохгүй.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Багтаамж 0-ээс их байна.");
        }
        if (basePrice == null || basePrice.signum() <= 0) {
            throw new IllegalArgumentException("Үндсэн үнэ 0-ээс их байна.");
        }
    }
}
