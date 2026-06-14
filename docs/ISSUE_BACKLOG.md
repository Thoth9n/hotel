# Initial Issue Backlog

Эдгээр issue-г GitHub дээр дарааллаар үүсгэнэ. Нэг issue-г нэг branch дээр
хэрэгжүүлж, acceptance criteria бүрийг шалгасны дараа merge хийнэ.

## Issue 1: Database setup smoke test

**Branch:** `feature/issue-1-database-smoke-test`

**Зорилго:** Local MySQL тохиргоо зөв эсэхийг хэрэглэгчид ойлгомжтой шалгах.

**Acceptance criteria:**

- Database connection амжилттай эсэхийг startup үед бус, тусдаа action-аар шалгана.
- Connection амжилттай бол нууц мэдээлэлгүй success message харуулна.
- Connection алдаатай бол password болон JDBC URL-г log-д бүтнээр хэвлэхгүй.
- Resource try-with-resources-ээр хаагдана.
- Config parsing unit test нэмэгдэнэ.

## Issue 2: Authentication service tests

**Branch:** `test/issue-2-auth-service`

**Acceptance criteria:**

- Blank username/password үед DAO дуудагдахгүй.
- Олдоогүй user болон password mismatch ижил ерөнхий алдаа харуулна.
- Зөв password үед User буцаана.
- SQLException үед database-friendly message үүснэ.

## Issue 3: Room type model and DAO

**Branch:** `feature/issue-3-room-types`

**Acceptance criteria:**

- RoomType model нь id, name, capacity, basePrice, description агуулна.
- Мөнгөн утга `BigDecimal` байна.
- Active room type-уудыг нэрээр эрэмбэлж уншина.
- SQL бүр PreparedStatement ашиглана.
- DAO integration test эсвэл manual SQL evidence байна.

## Issue 4: Room model and status enum

**Branch:** `feature/issue-4-room-model`

**Acceptance criteria:**

- RoomStatus enum schema-тай яг таарна.
- Room model нь room type-тай холбоотой мэдээллийг хадгална.
- Status parsing алдаа ойлгомжтой exception үүсгэнэ.

## Issue 5: Room list screen

**Branch:** `feature/issue-5-room-list`

**Acceptance criteria:**

- TableView дээр дугаар, давхар, төрөл, үнэ, багтаамж, төлөв харагдана.
- Дугаар болон төлвөөр filter хийнэ.
- Empty, loading, database error state байна.
- Database query UI thread-ийг удаан хаахгүй.

## Issue 6: Create room form

**Branch:** `feature/issue-6-create-room`

**Acceptance criteria:**

- Room number, room type заавал.
- Давхар дугаар боломжийн хүрээнд байна.
- Duplicate room number үед ойлгомжтой message харуулна.
- Амжилттай хадгалсны дараа list шинэчлэгдэнэ.

## Issue 7: Edit and deactivate room

**Branch:** `feature/issue-7-edit-room`

**Acceptance criteria:**

- Existing room form-д зөв ачаалагдана.
- Room number uniqueness өөрийн мөрийг зөрчил гэж тооцохгүй.
- Ашиглагдсан өрөөг hard delete хийхгүй.
- Active reservation-тай өрөөг deactivate хийхийг хориглоно.

## Issue 8: Customer model and DAO

**Branch:** `feature/issue-8-customer-dao`

**Acceptance criteria:**

- Customer model schema-тай таарна.
- Add, update, findById, search DAO method байна.
- Search нь name, phone, document number дэмжинэ.
- User input SQL string-д concatenate хийгдэхгүй.

## Issue 9: Customer list and form

**Branch:** `feature/issue-9-customer-ui`

**Acceptance criteria:**

- Customer list болон search ажиллана.
- First name, last name, phone required.
- Email optional боловч оруулсан үед format шалгана.
- Form алдаа field-ийн ойролцоо харагдана.

## Issue 10: Available room query

**Branch:** `feature/issue-10-room-availability`

**Acceptance criteria:**

- Check-in, check-out, guest count-аар боломжит өрөө хайна.
- `check_out > check_in` validation байна.
- PENDING, CONFIRMED, CHECKED_IN reservation-тай давхцсан өрөө гарахгүй.
- Existing checkout өдөр шинэ check-in хийхийг зөвшөөрнө.
- Boundary case unit/integration test нэмэгдэнэ.

## Issue хийх тогтмол алхам

```text
1. GitHub issue үүсгэх
2. main-ээс branch нээх
3. Acceptance criteria-г test болгох
4. Implementation хийх
5. mvn clean test
6. Manual UI test
7. Screenshot/test evidence нэмэх
8. Conventional Commit хийх
9. Pull request үүсгэх
10. Review хийж main руу merge хийх
```

