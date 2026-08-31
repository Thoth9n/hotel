package mn.edu.internship.hotel.dao;

import mn.edu.internship.hotel.model.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> search(String query) throws SQLException;

    Optional<Customer> findById(long id) throws SQLException;

    long save(Customer customer) throws SQLException;

    void update(Customer customer) throws SQLException;
}
