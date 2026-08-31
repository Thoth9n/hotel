package mn.edu.internship.hotel.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerTest {
    @Test
    void validatesRequiredFieldsAndEmail() {
        assertThrows(IllegalArgumentException.class, () -> customer("", "Bat", "9911", "a@b.com"));
        assertThrows(IllegalArgumentException.class, () -> customer("Bold", "Bat", "9911", "bad-email"));
    }

    @Test
    void buildsFullName() {
        assertEquals("Bold Bat", customer("Bold", "Bat", "9911", null).fullName());
    }

    private static Customer customer(String firstName, String lastName, String phone, String email) {
        return new Customer(0, firstName, lastName, phone, email, null, null, null);
    }
}
