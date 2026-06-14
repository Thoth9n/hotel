package mn.edu.internship.hotel.session;

import mn.edu.internship.hotel.model.User;

import java.util.Optional;

public final class UserSession {
    private static User currentUser;

    private UserSession() {
    }

    public static void start(User user) {
        currentUser = user;
    }

    public static Optional<User> currentUser() {
        return Optional.ofNullable(currentUser);
    }

    public static void clear() {
        currentUser = null;
    }
}

