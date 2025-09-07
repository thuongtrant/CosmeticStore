# Phân Tích Mối Quan Hệ Giữa Các Bảng - Cosmetic Store

## Tổng Quan Các Entity

Hệ thống bao gồm các entity chính sau:
- **User** (Người dùng)
- **Role** (Vai trò)  
- **Product** (Sản phẩm)
- **Category** (Danh mục)
- **Image** (Hình ảnh)
- **Ingredient** (Thành phần)
- **SkinType** (Loại da)
- **Cart** (Giỏ hàng)
- **CartItem** (Item trong giỏ hàng)
- **Order** (Đơn hàng)
- **OrderItem** (Item trong đơn hàng)
- **Payment** (Thanh toán)
- **ShippingAddress** (Địa chỉ giao hàng)
- **ChatRoom** (Phòng chat)
- **PasswordResetToken** (Token đặt lại mật khẩu)

---

## Chi Tiết Các Mối Quan Hệ

### 1. **User ↔ Role** (Many-to-One)
```java
// User.java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "role_id", nullable = false)
private Role role;

// Role.java  
@OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private Set<User> users = new HashSet<>();
```

**Phân tích:**
- **Loại quan hệ:** N:1 (Many User - One Role)
- **Ý nghĩa:** Nhiều người dùng có thể có cùng một vai trò
- **Cascade:** `CascadeType.ALL` từ Role → User
- **Khi xóa:** 
  - ❌ **Xóa Role** → Tất cả User có role đó sẽ bị xóa (nguy hiểm)
  - ✅ **Xóa User** → Role không bị ảnh hưởng

---

### 2. **User ↔ Cart** (One-to-One)
```java
// Cart.java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;
```

**Phân tích:**
- **Loại quan hệ:** 1:1 (One User - One Cart)
- **Ý nghĩa:** Mỗi người dùng có duy nhất một giỏ hàng
- **Cascade:** Không có cascade từ Cart → User
- **Khi xóa:**
  - ✅ **Xóa User** → Cart không tự động bị xóa (cần xử lý thủ công)
  - ✅ **Xóa Cart** → User không bị ảnh hưởng

---

### 3. **Cart ↔ CartItem** (One-to-Many)
```java
// Cart.java
@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
private List<CartItem> cartItems = new ArrayList<>();

// CartItem.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "cart_id", nullable = false)
private Cart cart;
```

**Phân tích:**
- **Loại quan hệ:** 1:N (One Cart - Many CartItem)
- **Cascade:** `CascadeType.ALL` + `orphanRemoval = true`
- **Khi xóa:**
  - ✅ **Xóa Cart** → Tất cả CartItem sẽ bị xóa theo
  - ✅ **Xóa CartItem** → Cart không bị ảnh hưởng

---

### 4. **Product ↔ CartItem** (One-to-Many)
```java
// CartItem.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "product_id", nullable = false)
private Product product;
```

**Phân tích:**
- **Loại quan hệ:** 1:N (One Product - Many CartItem)
- **Cascade:** Không có
- **Khi xóa:**
  - ❌ **Xóa Product** → CartItem vẫn tồn tại nhưng tham chiếu lỗi (cần xử lý trước)
  - ✅ **Xóa CartItem** → Product không bị ảnh hưởng

---

### 5. **Product ↔ Category** (Many-to-One)
```java
// Product.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
private Category category;
```

**Phân tích:**
- **Loại quan hệ:** N:1 (Many Product - One Category)
- **Cascade:** Không có
- **Khi xóa:**
  - ❌ **Xóa Category** → Product vẫn tồn tại nhưng category = null
  - ✅ **Xóa Product** → Category không bị ảnh hưởng

---

### 6. **Product ↔ Image** (One-to-Many) ⭐ **QUAN TRỌNG**
```java
// Product.java
@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Image> images = new ArrayList<>();

// Image.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "product_id")
private Product product;
```

**Phân tích:**
- **Loại quan hệ:** 1:N (One Product - Many Image)
- **Cascade:** `CascadeType.ALL` + `orphanRemoval = true`
- **Khi xóa:**
  - ✅ **Xóa Product** → **TẤT CẢ Image sẽ bị xóa theo** (trả lời câu hỏi của bạn)
  - ✅ **Xóa Image** → Product không bị ảnh hưởng

**💡 Trả lời câu hỏi: "Xóa sản phẩm thì ảnh có bị xóa theo hay không?"**
**→ CÓ! Khi xóa Product, tất cả Image liên quan sẽ tự động bị xóa theo.**

---

### 7. **Product ↔ Ingredient** (Many-to-Many)
```java
// Product.java
@ManyToMany
@JoinTable(
    name = "product_ingredient",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "ingredient_id")
)
private List<Ingredient> ingredients = new ArrayList<>();
```

**Phân tích:**
- **Loại quan hệ:** N:N (Many Product - Many Ingredient)
- **Bảng trung gian:** `product_ingredient`
- **Cascade:** Không có
- **Khi xóa:**
  - ✅ **Xóa Product** → Chỉ xóa record trong bảng trung gian, Ingredient không bị xóa
  - ✅ **Xóa Ingredient** → Chỉ xóa record trong bảng trung gian, Product không bị xóa

---

### 8. **Product ↔ SkinType** (Many-to-Many)
```java
// Product.java
@ManyToMany
@JoinTable(
    name = "product_skin_type",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "skin_type_id")
)
private List<SkinType> skinTypes = new ArrayList<>();
```

**Phân tích:** Tương tự như Product ↔ Ingredient

---

### 9. **User ↔ Order** (One-to-Many)
```java
// Order.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

**Phân tích:**
- **Loại quan hệ:** 1:N (One User - Many Order)
- **Cascade:** Không có
- **Khi xóa:**
  - ❌ **Xóa User** → Order vẫn tồn tại nhưng tham chiếu lỗi
  - ✅ **Xóa Order** → User không bị ảnh hưởng

---

### 10. **Order ↔ OrderItem** (One-to-Many)
```java
// Order.java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> orderItems = new ArrayList<>();

// OrderItem.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "order_id", nullable = false)
private Order order;
```

**Phân tích:**
- **Loại quan hệ:** 1:N (One Order - Many OrderItem)
- **Cascade:** `CascadeType.ALL` + `orphanRemoval = true`
- **Khi xóa:**
  - ✅ **Xóa Order** → Tất cả OrderItem sẽ bị xóa theo
  - ✅ **Xóa OrderItem** → Order không bị ảnh hưởng

---

### 11. **Product ↔ OrderItem** (One-to-Many)
```java
// OrderItem.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "product_id", nullable = false)
private Product product;
```

**Phân tích:**
- **Loại quan hệ:** 1:N (One Product - Many OrderItem)
- **Cascade:** Không có
- **Khi xóa:**
  - ❌ **Xóa Product** → OrderItem vẫn tồn tại nhưng tham chiếu lỗi (cần xử lý cẩn thận)

---

### 12. **Order ↔ Payment** (One-to-One)
```java
// Order.java
@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
private Payment payment;

// Payment.java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "order_id", nullable = false)
private Order order;
```

**Phân tích:**
- **Loại quan hệ:** 1:1 (One Order - One Payment)
- **Cascade:** `CascadeType.ALL` từ Order → Payment
- **Khi xóa:**
  - ✅ **Xóa Order** → Payment sẽ bị xóa theo
  - ❌ **Xóa Payment** → Order không bị ảnh hưởng (nhưng sẽ có vấn đề logic)

---

### 13. **Order ↔ ShippingAddress** (Many-to-One)
```java
// Order.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "shipping_address_id", nullable = false)
private ShippingAddress shippingAddress;
```

**Phân tích:**
- **Loại quan hệ:** N:1 (Many Order - One ShippingAddress)
- **Cascade:** Không có
- **Khi xóa:**
  - ❌ **Xóa ShippingAddress** → Order vẫn tồn tại nhưng tham chiếu lỗi
  - ✅ **Xóa Order** → ShippingAddress không bị ảnh hưởng

---

### 14. **User ↔ ShippingAddress** (One-to-Many)
```java
// ShippingAddress.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

**Phân tích:**
- **Loại quan hệ:** 1:N (One User - Many ShippingAddress)
- **Cascade:** Không có

---

### 15. **User ↔ ChatRoom** (One-to-Many)
```java
// ChatRoom.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "customer_id", nullable = false)
private User customer;

// User.java
@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<ChatRoom> chatRooms;
```

**Phân tích:**
- **Loại quan hệ:** 1:N (One User - Many ChatRoom)
- **Cascade:** `CascadeType.ALL` từ User → ChatRoom
- **Khi xóa:**
  - ✅ **Xóa User** → Tất cả ChatRoom sẽ bị xóa theo
  - ✅ **Xóa ChatRoom** → User không bị ảnh hưởng

---

### 16. **User ↔ PasswordResetToken** (One-to-One)
```java
// PasswordResetToken.java
@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
@JoinColumn(nullable = false, name = "user_id")
private User user;
```

**Phân tích:**
- **Loại quan hệ:** 1:1 (One User - One PasswordResetToken)
- **Cascade:** Không có

---

## Tóm Tắt Các Trường Hợp Xóa Cascade

### ✅ **Xóa Cascade (Tự động xóa theo)**
1. **Xóa Role** → Xóa tất cả User (nguy hiểm)
2. **Xóa Cart** → Xóa tất cả CartItem
3. **Xóa Product** → **Xóa tất cả Image** ⭐
4. **Xóa Order** → Xóa tất cả OrderItem
5. **Xóa Order** → Xóa Payment
6. **Xóa User** → Xóa tất cả ChatRoom

### ❌ **Không Cascade (Cần xử lý thủ công)**
1. **Xóa User** → Cart, Order, ShippingAddress vẫn tồn tại
2. **Xóa Category** → Product vẫn tồn tại (category = null)
3. **Xóa Product** → CartItem, OrderItem vẫn tồn tại (nguy hiểm)
4. **Xóa ShippingAddress** → Order vẫn tồn tại (nguy hiểm)

---

## Khuyến Nghị Xử Lý

### 🚨 **Vấn đề cần chú ý:**

1. **Xóa Product:**
   - ✅ Image sẽ tự động bị xóa
   - ❌ CartItem và OrderItem không bị xóa → Có thể gây lỗi
   - **Giải pháp:** Kiểm tra và xóa/cập nhật CartItem, OrderItem trước khi xóa Product

2. **Xóa User:**
   - ❌ Cart, Order, ShippingAddress không bị xóa tự động
   - **Giải pháp:** Xử lý thủ công hoặc thêm cascade

3. **Xóa Category:**
   - ❌ Product vẫn tồn tại nhưng category = null
   - **Giải pháp:** Kiểm tra và gán category khác hoặc cảnh báo

### 💡 **Best Practices:**
- Luôn kiểm tra dependencies trước khi xóa
- Sử dụng soft delete cho các entity quan trọng
- Implement validation logic trong service layer
- Sử dụng transaction để đảm bảo tính nhất quán dữ liệu
