package mn.edu.internship.hotel.config;

public record DatabaseConfig(
        String host,
        int port,
        String database,
        String username,
        String password
) {
    public static DatabaseConfig fromEnvironment() {
        return new DatabaseConfig(
                envOrDefault("DB_HOST", "127.0.0.1"),
                parsePort(envOrDefault("DB_PORT", "3306")),
                envOrDefault("DB_NAME", "hotel_management"),
                envOrDefault("DB_USER", "hotel_app"),
                System.getenv().getOrDefault("DB_PASSWORD", "")
        );
    }

    public String jdbcUrl() {
        return ("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
                + "&connectTimeout=5000&socketTimeout=5000")
                .formatted(host, port, database);
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static int parsePort(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("DB_PORT бүхэл тоо байх ёстой.", exception);
        }
    }
}
