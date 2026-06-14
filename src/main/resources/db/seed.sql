INSERT INTO room_types (name, capacity, base_price, description)
VALUES
    ('SINGLE', 1, 120000.00, 'Нэг хүний стандарт өрөө'),
    ('DOUBLE', 2, 180000.00, 'Хоёр хүний нэг ортой өрөө'),
    ('TWIN', 2, 190000.00, 'Хоёр тусдаа ортой өрөө'),
    ('DELUXE', 2, 280000.00, 'Нэмэлт тохижилттой өрөө'),
    ('SUITE', 4, 450000.00, 'Тусдаа зочны хэсэгтэй өрөө')
ON DUPLICATE KEY UPDATE
    capacity = VALUES(capacity),
    base_price = VALUES(base_price),
    description = VALUES(description);

INSERT INTO rooms (room_number, floor, room_type_id, status, active)
SELECT '101', 1, id, 'AVAILABLE', TRUE FROM room_types WHERE name = 'SINGLE'
ON DUPLICATE KEY UPDATE room_number = VALUES(room_number);

INSERT INTO rooms (room_number, floor, room_type_id, status, active)
SELECT '102', 1, id, 'AVAILABLE', TRUE FROM room_types WHERE name = 'DOUBLE'
ON DUPLICATE KEY UPDATE room_number = VALUES(room_number);

INSERT INTO rooms (room_number, floor, room_type_id, status, active)
SELECT '201', 2, id, 'AVAILABLE', TRUE FROM room_types WHERE name = 'DELUXE'
ON DUPLICATE KEY UPDATE room_number = VALUES(room_number);

