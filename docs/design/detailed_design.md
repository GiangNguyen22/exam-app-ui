# Tài Liệu Thiết Kế Chi Tiết: Hệ Thống Thi Trắc Nghiệm Nội Bộ

Tài liệu này mô tả chi tiết kiến trúc và giải pháp kỹ thuật cho ứng dụng thi trắc nghiệm trên Android, phục vụ môi trường trường học.

## 1. Tổng Quan Công Nghệ (Tech Stack)
- **Mobile App**: Kotlin (ưu tiên) hoặc Java, sử dụng Jetpack Compose cho UI.
- **Backend**: Java (Spring Boot framework).
- **Database**: MySQL 8.0+ (Phổ biến, dễ quản lý trên hạ tầng local).
- **Real-time**: WebSocket (STOMP) để giám sát và thông báo.
- **Caching/Queue**: Redis (nếu cần xử lý tải cao cho 100+ user đồng thời trên hạ tầng local).
- **Hạ tầng**: Chạy local (Server trong mạng nội bộ trường).

---

## 2. Kiến Trúc Hệ Thống

### 2.1 Sơ đồ tổng quát
- **Client (Android)** <-> **Load Balancer/Nginx** <-> **Spring Boot API** <-> **MySQL**.
- **Real-time Channel**: WebSocket được duy trì giữa Mobile và Backend để gửi trạng thái "đang làm bài", "cảnh báo gian lận" và nhận lệnh "thu bài".

### 2.2 Xử lý Offline & Đồng bộ
- **Local DB**: Sử dụng **Room Database** trên Android để lưu trữ đề thi và câu trả lời tạm thời.
- **Cơ chế**:
    1. Khi bắt đầu thi, App tải toàn bộ đề về máy.
    2. Mỗi câu trả lời của thí sinh được lưu vào Room ngay lập tức.
    3. Một `WorkManager` sẽ chạy ngầm để đẩy dữ liệu lên Server khi có kết nối mạng (Auto-sync).

---

## 3. Thiết Kế Chi Tiết Cơ Sở Dữ Liệu (MySQL)

Dưới đây là mô tả chi tiết công dụng của từng bảng trong hệ thống:

### 3.1 Bảng `users` (Quản lý người dùng)
Lưu trữ thông tin của Admin, Giáo viên và Học sinh.
- `id`: Khóa chính tự tăng.
- `username`: Tên đăng nhập (duy nhất).
- `password_hash`: Mật khẩu đã được mã hóa (Bcrypt).
- `role`: Phân quyền (ADMIN, TEACHER, STUDENT).
- `student_id`: Mã số sinh viên/học sinh (chỉ dành cho role STUDENT).

### 3.2 Bảng `subjects` (Môn học)
- `id`: Khóa chính.
- `name`: Tên môn học (Toán, Lý, Hóa...).
- `description`: Mô tả chi tiết về môn học.

### 3.3 Bảng `questions` (Ngân hàng câu hỏi)
Lưu trữ nội dung câu hỏi, hỗ trợ định dạng LaTeX.
- `content`: Nội dung câu hỏi (VD: "Tính giá trị của $x$ trong phương trình...").
- `type`: Loại câu hỏi (SINGLE: Một đáp án, MULTI: Nhiều đáp án, TF: Đúng/Sai, FILL: Điền khuyết).
- `difficulty`: Mức độ (EASY, MEDIUM, HARD).
- `image_url`: Đường dẫn ảnh minh họa (nếu có).

### 3.4 Bảng `answers` (Đáp án câu hỏi)
- `question_id`: Liên kết với bảng questions.
- `content`: Nội dung đáp án (có thể chứa LaTeX).
- `is_correct`: Đánh dấu đáp án đúng.
- `explanation`: Giải thích tại sao đáp án này đúng (hiển thị sau khi thi xong).

### 3.5 Bảng `exams` (Đề thi)
- `duration_minutes`: Thời gian làm bài (phút).
- `start_time` & `end_time`: Khoảng thời gian cho phép thí sinh vào thi.
- `config_json`: Cấu hình linh hoạt (VD: `{"shuffle": true, "show_result_instantly": false}`).

### 3.6 Bảng `exam_questions` (Chi tiết đề thi)
Bảng trung gian liên kết đề thi với các câu hỏi trong ngân hàng.
- `order_index`: Thứ tự câu hỏi hiển thị trong đề.

### 3.7 Bảng `exam_results` (Kết quả thi)
Ghi nhận thông tin tổng quát về một lượt thi của thí sinh.
- `score`: Điểm số cuối cùng.
- `status`: Trạng thái (DOING: Đang làm, SUBMITTED: Đã nộp, CANCELLED: Vi phạm/Hủy).

### 3.8 Bảng `student_responses` (Chi tiết bài làm)
Lưu lại từng câu trả lời của thí sinh để chấm điểm và đối soát.
- `selected_answer_ids`: Danh sách ID các đáp án đã chọn (ngăn cách bởi dấu phẩy cho MULTI).
- `fill_content`: Nội dung điền vào (cho câu hỏi điền khuyết).

### 3.9 Bảng `activity_logs` (Nhật ký giám sát)
Phục vụ tính năng giám sát Real-time và chống gian lận.
- `event_type`: Loại sự kiện (APP_EXIT: Thoát ứng dụng, SCREENSHOT: Chụp màn hình, LOST_CONNECTION: Mất mạng).
- `details`: Thông tin bổ sung (VD: "Thoát ứng dụng 20 giây").

---

## 4. Giải Pháp Kỹ Thuật Đặc Thù

### 4.1 Chống gian lận (Anti-cheat)
- **Chặn Screenshot**: Sử dụng `WindowManager.LayoutParams.FLAG_SECURE` trong Activity.
- **Phát hiện chuyển App**: Override `onPause()` và `onStop()` để gửi log cảnh báo lên server qua WebSocket.
- **Khóa thiết bị**: Sử dụng **Kiosk Mode** (Android Lock Task Mode) nếu trường quản lý thiết bị, hoặc yêu cầu quyền Overlay để cảnh báo.

### 4.2 Hiển thị Công thức Toán học (LaTeX)
- Sử dụng thư viện **MathJax** hoặc **KaTeX** tích hợp trong WebView (hoặc thư viện native như `Android-MathView`) để render công thức từ chuỗi văn bản.

### 4.3 Giám sát Real-time
- Server lắng nghe nhịp tim (heartbeat) từ App qua WebSocket.
- Nếu thí sinh mất kết nối > 30s hoặc thoát app, Dashboard của giáo viên sẽ hiển thị cảnh báo đỏ.

---

## 5. Kế Hoạch Triển Khai
1. **Giai đoạn 1**: Thiết kế DB và API cốt lõi (Quản lý câu hỏi, Đăng nhập).
2. **Giai đoạn 2**: Xây dựng App Mobile (Làm bài thi, Offline storage).
3. **Giai đoạn 3**: Tích hợp Real-time và các tính năng bảo mật.
4. **Giai đoạn 4**: Kiểm thử tải (100 user đồng thời) và tối ưu hạ tầng local.
