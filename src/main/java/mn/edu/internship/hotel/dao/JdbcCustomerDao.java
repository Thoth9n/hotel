package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.config.ConnectionFactory;
import mn.edu.internship.hotel.model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JdbcCustomerDao implements CustomerDao {
    private static final String COLUMNS = """
            SELECT id, first_name, last_name, phone, email,
                   document_type, document_number, address
            FROM customers
            """;
    private final ConnectionFactory connectionFactory;

    public JdbcCustomerDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Customer> search(String query) throws SQLException {
        String sql = COLUMNS + " WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? "
                + "OR document_number LIKE ? ORDER BY last_name, first_name";
        String pattern = "%" + (query == null ? "" : query.trim()) + "%";
        List<Customer> customers = new ArrayList<>();
        try (var connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 1; index <= 4; index++) {
                statement.setString(index, pattern);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    customers.add(map(resultSet));
                }
            }
        }
        return List.copyOf(customers);
    }

    @Override
    public Optional<Customer> findById(long id) throws SQLException {
        try (var connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(COLUMNS + " WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    public long save(Customer customer) throws SQLException {
        String sql = """
                INSERT INTO customers (first_name, last_name, phone, email,
                    document_type, document_number, address)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (var connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, customer);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Үйлчлүүлэгчийн ID үүссэнгүй.");
                }
                return keys.getLong(1);
            }
        }
    }

    @Override
    public void update(Customer customer) throws SQLException {
        String sql = """
                UPDATE customers SET first_name = ?, last_name = ?, phone = ?, email = ?,
                    document_type = ?, document_number = ?, address = ?
                WHERE id = ?
                """;
        try (var connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, customer);
            statement.setLong(8, customer.id());
            statement.executeUpdate();
        }
    }

    private static void bind(PreparedStatement statement, Customer customer) throws SQLException {
        statement.setString(1, customer.firstName().trim());
        statement.setString(2, customer.lastName().trim());
        statement.setString(3, customer.phone().trim());
        statement.setString(4, emptyToNull(customer.email()));
        statement.setString(5, emptyToNull(customer.documentType()));
        statement.setString(6, emptyToNull(customer.documentNumber()));
        statement.setString(7, emptyToNull(customer.address()));
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static Customer map(ResultSet resultSet) throws SQLException {
        return new Customer(
                resultSet.getLong("id"), resultSet.getString("first_name"),
                resultSet.getString("last_name"), resultSet.getString("phone"),
                resultSet.getString("email"), resultSet.getString("document_type"),
                resultSet.getString("document_number"), resultSet.getString("address")
        );
    }
}
