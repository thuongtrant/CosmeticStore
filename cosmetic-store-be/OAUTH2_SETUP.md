# OAuth2 Setup Guide

## 1. Google OAuth2 Setup
1. Đi đến [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project hiện có
3. Enable Google+ API
4. Tạo OAuth 2.0 credentials:
   - Application type: Web application
   - Authorized redirect URIs: `http://localhost:8080/oauth2/callback/google`
5. Copy Client ID và Client Secret

## 2. Facebook OAuth2 Setup  
1. Đi đến [Facebook Developers](https://developers.facebook.com/)
2. Tạo app mới
3. Thêm Facebook Login product
4. Cấu hình Valid OAuth Redirect URIs: `http://localhost:8080/oauth2/callback/facebook`
5. Copy App ID và App Secret

## 3. Cập nhật application.properties
```properties
# Thay thế các giá trị YOUR_* bằng credentials thực
spring.security.oauth2.client.registration.google.client-id=YOUR_ACTUAL_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_ACTUAL_GOOGLE_CLIENT_SECRET

spring.security.oauth2.client.registration.facebook.client-id=YOUR_ACTUAL_FACEBOOK_APP_ID  
spring.security.oauth2.client.registration.facebook.client-secret=YOUR_ACTUAL_FACEBOOK_APP_SECRET
```

## 4. Frontend Integration
Thêm các nút OAuth2 login vào trang login:
```html
<!-- Google Login -->
<a href="/oauth2/authorization/google" class="btn btn-google">
    Login with Google
</a>

<!-- Facebook Login -->  
<a href="/oauth2/authorization/facebook" class="btn btn-facebook">
    Login with Facebook
</a>
```

## 5. Xử lý OAuth2 Redirect trong Frontend
Tạo page `/oauth2/redirect` để nhận JWT token:
```javascript
// Lấy token từ URL parameters
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get('token');
const role = urlParams.get('role');

if (token) {
    // Lưu token vào localStorage
    localStorage.setItem('authToken', token);
    localStorage.setItem('userRole', role);
    
    // Redirect về dashboard
    window.location.href = role === 'ADMIN' ? '/dashboard' : '/customer-dashboard';
} else {
    // Redirect về login với error
    window.location.href = '/login?error=oauth2_failed';
}
```
