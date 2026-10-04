# HƯỚNG DẪN VÀ QUY TẮC CẤU TRÚC CODE DÀNH CHO AI (AI CODING RULES)

> **Dự án**: Internal Exam Application (`com.internalexam`)  
> **Nền tảng**: Android (Kotlin, Jetpack Compose, Material 3) & Backend Spring Boot  
> **Áp dụng cho**: Tất cả các công cụ AI (Cursor, Antigravity, Copilot...) khi sinh mã hoặc chỉnh sửa codebase này.

---

## 1. QUY NGUYÊN TẮC CỐT LÕI (CORE PRINCIPLES)

1. **Tuân thủ Kiến trúc Hiện tại**: Không tự ý thay đổi cấu trúc package hay tên các module cốt lõi đã có.
2. **Ưu tiên Chế độ Dễ Demo (Demo-Friendly First)**:
   - Các chức năng mới (Scan OCR, Giám sát Realtime, Làm bài thi) phải tích hợp sẵn **Dữ liệu Mẫu (Demo Presets)** và **Thanh Giả lập Sự kiện (Simulator Controls)** để người dùng/giáo viên có thể trải nghiệm trực tiếp tính năng chỉ bằng 1 cú chạm mà không phụ thuộc vào hạ tầng backend hay thao tác phức tạp.
3. **Duy trì Dự phòng Mock Data (Fallback Mechanism)**:
   - Tất cả các màn hình UI khi tương tác với API qua `ApiClient` **BẮT BUỘC** phải có phương án xử lý try-catch và dự phòng bằng `MockData` khi mất mạng hoặc Backend offline.
4. **Không phá vỡ Chống gian lận (Anti-Cheat Security)**:
   - Không được tháo bỏ hoặc can thiệp làm vô hiệu hóa `FLAG_SECURE` (chặn chụp màn hình) hoặc các trình lắng nghe sự kiện thoát ứng dụng (`AppLifecycleMonitor`, `ProctoringEventBuffer`).
5. **Không viết Code Blocking trên Main Thread**:
   - Tất cả thao tác gọi API, đọc/ghi file Excel/PDF, hoặc xử lý ảnh OCR phải được chạy trên Coroutine `Dispatchers.IO` hoặc `Dispatchers.Default`.

---

## 2. QUY CHUẨN CẤU TRÚC THƯ MỤC (PROJECT STRUCTURE)

```
app/src/main/java/com/internalexam/
├── data/                  # Xử lý dữ liệu & API Client
│   ├── auth/              # Phân quyền Auth & Role Mappers
│   ├── examimport/        # Đọc/Xuất dữ liệu Đề thi (Excel)
│   ├── network/           # ApiClient, Retrofit, Data Transfer Objects (DTO)
│   ├── questionimport/    # Đọc/Xuất dữ liệu Câu hỏi (Excel)
│   ├── ExamAttemptStore.kt # Quản lý trạng thái bài làm thi hiện tại
│   └── SessionManager.kt   # Quản lý Session đăng nhập & Token
├── model/                 # Domain Models & Mock Data
│   └── mock/MockData.kt
├── monitor/               # Giám sát bài thi & Chống gian lận
│   ├── AppLifecycleMonitor.kt
│   ├── NetworkMonitor.kt
│   └── ProctoringEventBuffer.kt
├── navigation/            # Điểm điều hướng chính & Routes
│   └── InternalExamApp.kt
├── ui/                    # Giao diện Jetpack Compose
│   ├── admin/             # Màn hình Admin (User, Role, Group, Audit)
│   ├── auth/              # Splash, Login
│   ├── components/        # UI Component dùng chung (Common.kt)
│   ├── profile/           # Màn hình Hồ sơ cá nhân
│   ├── student/           # Màn hình Học sinh (Home, Lobby, Taking, Result)
│   ├── teacher/           # Màn hình Giáo viên (Dashboard, Exam, Monitor, Report)
│   └── theme/             # System Design Theme (Color, Type, Shape, Theme)
```

---

## 3. QUY CHUẨN GIAO DIỆN JETPACK COMPOSE & MATERIAL 3

### 3.1 Bảng màu & Theme (Color Tokens)
Sử dụng trực tiếp các token màu quy định trong `com.internalexam.ui.theme.*`:
- `AppIndigo`: Màu chủ đạo chính (Primary Actions, TopBar, Highlights).
- `AppMint`: Màu thành công, trạng thái Online, điểm đúng.
- `AppAmber`: Màu cảnh báo, đồng hồ đếm ngược sắp hết giờ, câu chưa chắc chắn.
- `AppCoral` / `AppRed`: Màu lỗi, cảnh báo vi phạm gian lận, nút xóa/nộp bài khẩn cấp.
- `AppBg` / `AppSurface`: Màu nền ứng dụng và nền Card.
- `AppCardBorder`: Viền mỏng cho các Card (1dp, `AppCardBorder`).

### 3.2 Thành phần UI & Demo Components
- **Quick Demo Launcher**: Tích hợp menu chuyển nhanh màn hình khi đi demo cho Giáo viên.
- **Nền màn hình**: Tất cả các màn hình chính phải bọc trong `AppBackground { ... }`.
- **TopBar & BottomBar**: Ẩn BottomBar khi trong phòng thi (`Routes.Taking` và `Routes.Submit`).

---

## 4. QUY TRÌNH KIỂM THỬ VÀ XÁC NHẬN (VERIFICATION CHECKLIST)

Mọi mã nguồn do AI tạo ra phải vượt qua các tiêu chí kiểm tra sau:
- [ ] **Biên dịch (Build)**: Chạy `gradlew assembleDebug` thành công không có lỗi syntax.
- [ ] **Chế độ Demo mượt mà**: Các nút giả lập sự kiện (Event Simulator) và Ảnh mẫu Demo hoạt động tức thì, không bị đơ giật.
- [ ] **Preview & Mock**: Chạy ứng dụng hoàn hảo ở chế độ offline (không có backend).
- [ ] **UI Responsiveness**: Đảm bảo hiển thị đẹp mắt, không nén chữ trên màn hình điện thoại.
