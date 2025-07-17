# HƯỚNG DẪN TEST BẢO MẬT BẰNG POSTMAN - ĐÃ CẬP NHẬT

## 1. CÀI ĐẶT POSTMAN COLLECTION

### Base URL
```
http://localhost:8080
```

### Tạo Environment Variables:
- `baseUrl`: http://localhost:8080
- `jwtToken`: (sẽ được set tự động)

---

## 2. TEST CASES BẢO MẬT

### A. TEST ĐĂNG KÝ (/api/auth/signup)

#### Test 1: Đăng ký thành công
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Headers:**
```
Content-Type: application/json
```
**Body:** (CẤU TRÚC CHÍNH XÁC)
```json
{
    "username": "testuser",
    "email": "test@example.com",
    "password": "123456789",
    "phone": "0123456789",
    "gender": "Male",
    "roleName": "ROLE_USER"
}
```
**Expected Result:** Status 200, Message "User registered successfully!"

#### Test 2: Username đã tồn tại
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "testuser",
    "email": "test2@example.com",
    "password": "123456789",
    "phone": "0987654321",
    "gender": "Female"
}
```
**Expected Result:** Status 400, "Error: Username is already taken!"

#### Test 3: Email đã tồn tại
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "testuser2",
    "email": "test@example.com",
    "password": "123456789",
    "phone": "0987654321"
}
```
**Expected Result:** Status 400, "Error: Email is already in use!"

#### Test 4: Validation - Username quá ngắn
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "ab",
    "email": "test3@example.com",
    "password": "123456789"
}
```
**Expected Result:** Status 400, Validation error về username

#### Test 5: Validation - Password quá ngắn
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "testuser3",
    "email": "test3@example.com",
    "password": "123"
}
```
**Expected Result:** Status 400, Validation error về password

#### Test 6: Validation - Email không hợp lệ
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "testuser3",
    "email": "invalid-email",
    "password": "123456789"
}
```
**Expected Result:** Status 400, Validation error về email

#### Test 7: Validation - Trường bắt buộc trống
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "",
    "email": "test4@example.com",
    "password": "123456789"
}
```
**Expected Result:** Status 400, "Username không được để trống"

#### Test 8: SQL Injection trong username
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "admin'; DROP TABLE users; --",
    "email": "hacker@example.com",
    "password": "123456789"
}
```
**Expected Result:** Username được treat như string bình thường hoặc validation error

#### Test 9: XSS trong các trường
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "<script>alert('xss')</script>",
    "email": "xss@example.com",
    "password": "123456789"
}
```
**Expected Result:** Dữ liệu được escape hoặc validation error

### B. TEST ĐĂNG NHẬP (/api/auth/signin)

#### Test 10: Đăng nhập thành công
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signin`  
**Headers:**
```
Content-Type: application/json
```
**Body:**
```json
{
    "username": "testuser",
    "password": "123456789"
}
```
**Expected Result:** Status 200, JWT token trong response
**Expected Response Format:**
```json
{
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER"
}
```
**Post-request Script:**
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("jwtToken", response.accessToken);
}
```

#### Test 11: Sai mật khẩu
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signin`  
**Body:**
```json
{
    "username": "testuser",
    "password": "wrongpassword"
}
```
**Expected Result:** Status 401, Unauthorized

#### Test 12: Username không tồn tại
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signin`  
**Body:**
```json
{
    "username": "nonexistentuser",
    "password": "123456789"
}
```
**Expected Result:** Status 401, Unauthorized

#### Test 13: Trường bắt buộc trống
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signin`  
**Body:**
```json
{
    "username": "",
    "password": "123456789"
}
```
**Expected Result:** Status 400, "Username không được để trống"

### C. TEST PHÂN QUYỀN VÀ JWT

#### Test 14: Truy cập endpoint công khai
**Method:** GET  
**URL:** `{{baseUrl}}/api/test/all`  
**Expected Result:** Status 200, "Public Content."

#### Test 15: Truy cập endpoint user không có token
**Method:** GET  
**URL:** `{{baseUrl}}/api/test/user`  
**Expected Result:** Status 401, Unauthorized

#### Test 16: Truy cập endpoint user với token hợp lệ
**Method:** GET  
**URL:** `{{baseUrl}}/api/test/user`  
**Headers:**
```
Authorization: Bearer {{jwtToken}}
```
**Expected Result:** Status 200, "User Content."

#### Test 17: Truy cập endpoint admin với user token
**Method:** GET  
**URL:** `{{baseUrl}}/api/test/admin`  
**Headers:**
```
Authorization: Bearer {{jwtToken}}
```
**Expected Result:** Status 403, Forbidden (vì user bình thường không có quyền admin)

#### Test 18: Token không hợp lệ
**Method:** GET  
**URL:** `{{baseUrl}}/api/test/user`  
**Headers:**
```
Authorization: Bearer invalidtoken123
```
**Expected Result:** Status 401, Unauthorized

#### Test 19: Đăng ký admin và test quyền admin
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "adminuser",
    "email": "admin@example.com",
    "password": "123456789",
    "roleName": "ROLE_ADMIN"
}
```
**Expected Result:** Status 200, sau đó login và test endpoint admin

### D. TEST BẢO MẬT NÂNG CAO

#### Test 20: CORS Headers
**Method:** OPTIONS  
**URL:** `{{baseUrl}}/api/auth/signin`  
**Headers:**
```
Origin: http://malicious-site.com
Access-Control-Request-Method: POST
Access-Control-Request-Headers: Content-Type
```
**Expected Result:** Kiểm tra CORS headers có phù hợp không

#### Test 21: Large Payload Attack
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:** JSON với string rất dài trong username (>1000 ký tự)
```json
{
    "username": "a".repeat(1000),
    "email": "test@example.com",
    "password": "123456789"
}
```
**Expected Result:** Status 400, validation error về độ dài username

#### Test 22: Phone validation test
**Method:** POST  
**URL:** `{{baseUrl}}/api/auth/signup`  
**Body:**
```json
{
    "username": "phonetest",
    "email": "phone@example.com", 
    "password": "123456789",
    "phone": "12345678901234567890"
}
```
**Expected Result:** Status 400, validation error về độ dài phone

---

## 3. NGUYÊN NHÂN LỖI 400 BAD REQUEST VÀ CÁCH KHẮC PHỤC

### Lỗi phổ biến:
1. **Thiếu DTO classes:** Đã khắc phục bằng cách tạo SignupRequest, LoginRequest, JwtResponse
2. **Cấu trúc JSON sai:** Phải include đầy đủ các trường như phone, gender, roleName
3. **Validation constraints:** Username 3-20 ký tự, password 6-40 ký tự, email hợp lệ

### Cấu trúc JSON chính xác cho signup:
```json
{
    "username": "string (3-20 chars)",
    "email": "valid email (max 50 chars)",
    "password": "string (6-40 chars)",
    "phone": "string (max 15 chars, optional)",
    "gender": "string (optional)",
    "roleName": "ROLE_USER hoặc ROLE_ADMIN (optional, default: ROLE_USER)"
}
```

### Để debug lỗi:
1. Kiểm tra server console logs
2. Verify database connection
3. Đảm bảo tất cả dependencies đã được inject
4. Check validation messages trong response

---

## 4. ĐIỂM MẠNH BẢO MẬT ĐÃ TRIỂN KHAI

✅ **Input Validation:** Bean Validation với messages tiếng Việt  
✅ **Password Hashing:** BCrypt encoding  
✅ **JWT Authentication:** Token với expiration time  
✅ **Role-based Access Control:** USER/ADMIN roles  
✅ **Unique Constraints:** Username và email không trùng lặp  
✅ **CORS Configuration:** Cross-origin support  
✅ **Error Handling:** Proper HTTP status codes
