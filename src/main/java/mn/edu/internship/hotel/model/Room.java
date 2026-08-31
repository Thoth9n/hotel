package mn.edu.internship.hotel.model;

public record Room(
        long id,
        String roomNumber,
        Integer floor,
        RoomType roomType,
        RoomStatus status,
        boolean active,
        String notes
) {
    public Room {
        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException("Өрөөний дугаар хоосон байж болохгүй.");
        }
        if (roomType == null || status == null) {
            throw new IllegalArgumentException("Өрөөний төрөл болон төлөв заавал байна.");
        }
    }
}
