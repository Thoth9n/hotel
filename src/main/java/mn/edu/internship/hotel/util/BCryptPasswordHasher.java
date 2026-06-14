package mn.edu.internship.hotel.util;

import org.mindrot.jbcrypt.BCrypt;

public final class BCryptPasswordHasher implements PasswordHasher {
    private static final int LOG_ROUNDS = 12;

    @Override
    public String hash(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(LOG_ROUNDS));
    }

    @Override
    public boolean matches(String plainText, String passwordHash) {
        try {
            return BCrypt.checkpw(plainText, passwordHash);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}

