package mn.edu.internship.hotel.util;

public interface PasswordHasher {
    String hash(String plainText);

    boolean matches(String plainText, String passwordHash);
}

