package mn.edu.internship.hotel.model;

public record Customer(
        long id,
        String firstName,
        String lastName,
        String phone,
        String email,
        String documentType,
        String documentNumber,
        String address
) {
    public Customer {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Нэр заавал байна.");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Овог заавал байна.");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Утасны дугаар заавал байна.");
        }
        if (email != null && !email.isBlank() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Имэйл хаяг буруу байна.");
        }
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}
