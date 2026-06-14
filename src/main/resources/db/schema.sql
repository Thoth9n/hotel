CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'RECEPTIONIST'))
);

CREATE TABLE IF NOT EXISTS room_types (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL UNIQUE,
    capacity INT NOT NULL,
    base_price DECIMAL(12, 2) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_room_type_capacity CHECK (capacity > 0),
    CONSTRAINT chk_room_type_price CHECK (base_price > 0)
);

CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    floor INT,
    room_type_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rooms_room_type
        FOREIGN KEY (room_type_id) REFERENCES room_types(id),
    CONSTRAINT chk_rooms_status CHECK (
        status IN ('AVAILABLE', 'RESERVED', 'OCCUPIED', 'CLEANING', 'MAINTENANCE')
    )
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(150),
    document_type VARCHAR(30),
    document_number VARCHAR(80),
    address VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_customers_document (document_type, document_number),
    INDEX idx_customers_phone (phone)
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_code VARCHAR(30) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    actual_check_in_at TIMESTAMP NULL,
    actual_check_out_at TIMESTAMP NULL,
    guest_count INT NOT NULL,
    nightly_rate DECIMAL(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    special_request VARCHAR(1000),
    cancel_reason VARCHAR(500),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservations_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_reservations_room
        FOREIGN KEY (room_id) REFERENCES rooms(id),
    CONSTRAINT fk_reservations_user
        FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT chk_reservation_dates CHECK (check_out_date > check_in_date),
    CONSTRAINT chk_reservation_guests CHECK (guest_count > 0),
    CONSTRAINT chk_reservation_rate CHECK (nightly_rate > 0),
    CONSTRAINT chk_reservation_status CHECK (
        status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED', 'NO_SHOW')
    ),
    INDEX idx_reservation_availability (room_id, check_in_date, check_out_date, status),
    INDEX idx_reservation_customer (customer_id)
);

CREATE TABLE IF NOT EXISTS additional_charges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    description VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_charges_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    CONSTRAINT chk_charge_quantity CHECK (quantity > 0),
    CONSTRAINT chk_charge_price CHECK (unit_price >= 0)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    receipt_number VARCHAR(40) NOT NULL UNIQUE,
    amount DECIMAL(12, 2) NOT NULL,
    method VARCHAR(30) NOT NULL,
    paid_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    received_by BIGINT NOT NULL,
    note VARCHAR(500),
    CONSTRAINT fk_payments_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    CONSTRAINT fk_payments_user
        FOREIGN KEY (received_by) REFERENCES users(id),
    CONSTRAINT chk_payment_amount CHECK (amount > 0),
    CONSTRAINT chk_payment_method CHECK (method IN ('CASH', 'CARD', 'TRANSFER')),
    INDEX idx_payments_reservation (reservation_id)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action VARCHAR(80) NOT NULL,
    entity_type VARCHAR(80),
    entity_id BIGINT,
    details VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_audit_created_at (created_at),
    INDEX idx_audit_entity (entity_type, entity_id)
);

