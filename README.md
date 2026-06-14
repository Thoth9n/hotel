# Hotel Management System

Java 21, Maven, JavaFX, FXML, CSS, MySQL, JDBC ашигласан зуны дадлагын
зочид буудлын удирдлагын desktop төсөл.

## Одоогийн боломж

- JavaFX login дэлгэц
- BCrypt password шалгалт
- JDBC + PreparedStatement хэрэглэгч хайлт
- Нэвтэрсэн хэрэглэгчийн session
- Role харуулах dashboard shell
- MySQL schema болон development seed
- Захиалгын огноо, давхцлын дүрмийн unit test
- GitHub Actions build

## Шаардлагатай программ

- Java 21 буюу түүнээс шинэ JDK
- Maven 3.9+
- MySQL 8+

## Database бэлтгэх

MySQL administrator хэрэглэгчээр:

```sql
CREATE DATABASE hotel_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER 'hotel_app'@'localhost' IDENTIFIED BY 'change_me';
GRANT SELECT, INSERT, UPDATE, DELETE ON hotel_management.* TO 'hotel_app'@'localhost';
FLUSH PRIVILEGES;
```

Дараа нь:

```powershell
mysql -u root -p hotel_management < src/main/resources/db/schema.sql
mysql -u root -p hotel_management < src/main/resources/db/seed.sql
```

## Environment variables

`.env.example`-ийг лавлагаа болгон дараах утгуудыг IDE run configuration эсвэл
terminal session-д тохируулна. `.env` файлыг программ автоматаар уншихгүй.

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="hotel_management"
$env:DB_USER="hotel_app"
$env:DB_PASSWORD="change_me"
```

## Admin хэрэглэгч үүсгэх

BCrypt hash агуулсан SQL үүсгэх:

```powershell
mvn exec:java `
  "-Dexec.mainClass=mn.edu.internship.hotel.tools.AdminUserSqlGenerator" `
  "-Dexec.args=admin Admin123! System Administrator"
```

Гарсан `INSERT` statement-ийг MySQL дээр нэг удаа ажиллуулна. Production
нууц үгийг command history-д оруулахгүй; энэ utility нь зөвхөн local demo-д
зориулагдсан.

## Ажиллуулах

```powershell
mvn clean test
mvn javafx:run
```

## Төслийн бүтэц

```text
controller/  JavaFX дэлгэцийн controller
dao/         JDBC болон PreparedStatement
model/       domain model, enum
service/     validation болон business logic
session/     нэвтэрсэн хэрэглэгчийн төлөв
util/        navigation, password, date helper
resources/
  fxml/      JavaFX layout
  css/       theme
  db/        schema, seed
```

## Git дүрэм

- Нэг issue = нэг богино branch
- Нэг commit = нэг логик өөрчлөлт
- Commit-оос өмнө `mvn test`
- Database password болон local config commit хийхгүй

## Дараагийн milestone

1. Room type болон room CRUD
2. Customer CRUD
3. Reservation availability query
4. Overlap prevention
5. Check-in/check-out transaction
6. Payment and dashboard

