package mn.edu.internship.hotel.model;

public record User(
        long id,
        String username,
        String passwordHash,
        String fullName,
        Role role,
        boolean active
) {
}

