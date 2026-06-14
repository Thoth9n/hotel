package mn.edu.internship.hotel.tools;

import mn.edu.internship.hotel.util.BCryptPasswordHasher;

public final class AdminUserSqlGenerator {
    private AdminUserSqlGenerator() {
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: <username> <password> <full name>");
            return;
        }

        String username = escapeSql(args[0]);
        String passwordHash = escapeSql(new BCryptPasswordHasher().hash(args[1]));
        String fullName = escapeSql(String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)));

        System.out.printf("""
                INSERT INTO users (username, password_hash, full_name, role, active)
                VALUES ('%s', '%s', '%s', 'ADMIN', TRUE);
                %n""", username, passwordHash, fullName);
    }

    private static String escapeSql(String value) {
        return value.replace("'", "''");
    }
}

