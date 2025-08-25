# HƯỚNG DẪN SỬA LỖI OAUTH2 REDIRECT URI MISMATCH

## 🚨 LỖI HIỆN TẠI:
- Google OAuth2: Error 400: redirect_uri_mismatch
- Facebook OAuth2: Redirect sai về trang admin

## ✅ GIẢI PHÁP ĐÃ THỰC HIỆN:

### Backend Changes (ĐÃ HOÀN THÀNH):
1. ✅ Cập nhật application.properties với redirect URIs đúng
2. ✅ Thêm OAuth2 endpoints vào PUBLIC_API_ENDPOINTS 
3. ✅ Cải thiện OAuth2AuthenticationSuccessHandler để parse tên đầy đủ

### BƯỚC TIẾP THEO - CẬP NHẬT OAUTH2 PROVIDERS:

## 1. GOOGLE CLOUD CONSOLE:
1. Truy cập: https://console.cloud.google.com/
2. Chọn project "CosmeticStore"
3. Google Auth Platform > Clients > CosmeticWeb
4. Trong "Authorized redirect URIs", THAY ĐỔI:
   - ❌ XÓA: `http://localhost:8080/login/oauth2/code/google`
   - ✅ THÊM: `http://localhost:8080/login/oauth2/code/google`
5. Nhấn "Save"

## 2. FACEBOOK DEVELOPERS:
1. Truy cập: https://developers.facebook.com/
2. Chọn app "CosmeticWeb" (ID: 738878629065364)
3. Facebook Login > Settings
4. Trong "Valid OAuth Redirect URIs", THAY ĐỔI:
   - ❌ XÓA: các redirect URIs cũ
   - ✅ THÊM: `http://localhost:8080/login/oauth2/code/facebook`
5. Nhấn "Save Changes"

## 3. KIỂM TRA SAU KHI CẬP NHẬT:

### Test Google OAuth2:
1. Khởi động backend: `mvn spring-boot:run`
2. Khởi động frontend: `npm start`
3. Truy cập: http://localhost:3000/login
4. Click "Đăng nhập bằng Google"
5. Kiểm tra redirect flow: Google Auth → Backend Callback → Frontend Redirect

### Test Facebook OAuth2:
1. Click "Đăng nhập bằng Facebook"
2. Kiểm tra redirect flow: Facebook Auth → Backend Callback → Frontend Redirect

## 4. REDIRECT FLOW ĐÃ SỬA:

### Trước (LỖI):
```
Frontend Button → /oauth2/authorization/google
Google Auth → WRONG_REDIRECT_URI (mismatch error)
```

### Sau (ĐÚNG):
```
Frontend Button → /oauth2/authorization/google
Google Auth → /login/oauth2/code/google (Backend)
Backend Success Handler → /oauth2/redirect?token=xxx (Frontend)
Frontend OAuth2Redirect → Save token → Navigate to homepage
```

## 5. DEBUG COMMANDS:

### Kiểm tra backend logs:
```bash
# Trong terminal backend, tìm logs:
2024-xx-xx INFO : OAuth2 callback received for provider: google
2024-xx-xx INFO : User created/found: email@domain.com
2024-xx-xx INFO : JWT token generated successfully
2024-xx-xx INFO : Redirecting to frontend: /oauth2/redirect?token=xxx
```

### Kiểm tra Network tab trong browser:
1. F12 → Network tab
2. Click OAuth2 login button
3. Theo dõi redirect chain:
   - `/oauth2/authorization/google` → 302 redirect to Google
   - Google → 302 redirect to `/login/oauth2/code/google`
   - Backend → 302 redirect to `/oauth2/redirect?token=xxx`
   - Frontend → Save token và navigate

## 6. TROUBLESHOOTING:

### Nếu vẫn lỗi "redirect_uri_mismatch":
- Kiểm tra lại Google Console redirect URI
- Đảm bảo CHÍNH XÁC: `http://localhost:8080/login/oauth2/code/google`
- Không có space hoặc ký tự thừa

### Nếu Facebook vẫn redirect sai:
- Kiểm tra Facebook App settings
- Đảm bảo redirect URI: `http://localhost:8080/login/oauth2/code/facebook`
- Kiểm tra App ID và Secret trong application.properties

### Nếu frontend không nhận token:
- Kiểm tra console.log trong OAuth2Redirect component
- Kiểm tra URL có chứa ?token=xxx không
- Kiểm tra backend logs có generate JWT không

## 7. CÁC FILE ĐÃ ĐƯỢC CẬP NHẬT:
- ✅ application.properties: Redirect URIs đúng format
- ✅ WebSecurityConfig.java: Thêm OAuth2 endpoints vào public
- ✅ OAuth2AuthenticationSuccessHandler.java: Parse tên đầy đủ
