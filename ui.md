Thiết kế và code UI Android app cho hệ thống thi trắc nghiệm nội bộ.

Công nghệ:
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Dữ liệu mock bằng data class
- Chỉ code UI, chưa cần gọi API thật
- Có hỗ trợ trạng thái offline/sync/realtime bằng UI mock

Phong cách UI:
- Modern education app
- Nền sáng, màu chủ đạo xanh dương/tím
- Card bo góc 16-24dp
- Typography rõ ràng
- Ưu tiên thao tác nhanh trên mobile
- UI thi phải tối giản, tập trung, dễ bấm đáp án
- Có trạng thái: Online, Offline, Syncing, Synced

1. Splash Screen
- Logo app
- Tên app: Internal Exam
- Loading indicator

2. Login Screen
- Ô nhập MSSV / mã nhân viên / username
- Ô nhập mật khẩu
- Button Đăng nhập
- Hiển thị lỗi tài khoản bị khóa / sai mật khẩu
- Sau đăng nhập điều hướng theo role: Admin, Teacher, Student

3. Student Home
- Lời chào thí sinh
- Card kỳ thi sắp diễn ra
- Card bài thi đang mở
- Trạng thái mạng: Online / Offline
- Nút “Vào phòng thi”
- Danh sách kết quả gần đây

4. Exam Lobby Screen
- Thông tin đề thi: tên đề, môn học, thời lượng, số câu
- Thời gian mở / đóng
- Cảnh báo quy định thi
- Trạng thái đã tải đề về máy hay chưa
- Button “Bắt đầu làm bài”

5. Exam Taking Screen
- Top bar có:
  - Tên bài thi
  - Timer đếm ngược
  - Trạng thái sync
- Hiển thị câu hỏi hiện tại
- Đáp án dạng radio / checkbox
- Nút Câu trước / Câu sau
- Grid số câu để nhảy nhanh
- Đánh dấu câu đã trả lời / chưa trả lời / đang xem
- Auto save indicator: “Đã lưu lúc 10:32”
- Cảnh báo nếu mất mạng
- Button Nộp bài cố định cuối màn hình

6. Submit Confirmation Screen
- Tổng số câu đã làm
- Tổng số câu chưa làm
- Cảnh báo sau khi nộp không thể sửa
- Button “Nộp bài”
- Button “Quay lại kiểm tra”

7. Result Screen
- Điểm số lớn ở đầu màn hình
- Trạng thái: Đã chấm / Chờ chấm
- Số câu đúng, sai, bỏ trống
- Danh sách câu hỏi với đáp án đúng/sai
- Giải thích đáp án nếu có

8. Teacher Dashboard
- Tổng số đề thi
- Tổng số câu hỏi
- Bài thi đang diễn ra
- Cảnh báo gian lận realtime
- Shortcut:
  - Quản lý câu hỏi
  - Tạo đề thi
  - Sinh đề tự động
  - Xem kết quả
  - Thống kê

9. Question Bank Screen
- Search câu hỏi
- Filter theo môn học, chủ đề, độ khó, loại câu
- Danh sách câu hỏi dạng card
- Button thêm câu hỏi
- Button import Excel

10. Create Question Screen
- Nhập nội dung câu hỏi
- Chọn môn học
- Chọn chủ đề
- Chọn độ khó
- Chọn loại câu hỏi: SINGLE, MULTI, TRUE_FALSE, FILL_BLANK
- Nhập danh sách đáp án
- Chọn đáp án đúng
- Nhập giải thích

11. Create Exam Screen
- Tên đề thi
- Môn học
- Thời lượng
- Thời gian mở / đóng
- Số câu
- Điểm mỗi câu
- Toggle random câu hỏi
- Toggle random đáp án
- Chọn tạo thủ công hoặc sinh tự động

12. Auto Generate Exam Screen
- Chọn môn học
- Chọn chủ đề
- Chọn số câu theo độ khó:
  - Dễ
  - Trung bình
  - Khó
- Preview cấu hình đề
- Button “Sinh đề”

13. Live Monitoring Screen
- Danh sách thí sinh đang làm bài
- Trạng thái từng thí sinh:
  - Đang làm
  - Mất kết nối
  - Đã nộp
  - Có cảnh báo gian lận
- Log realtime:
  - APP_EXIT
  - SCREENSHOT
  - LOST_CONNECTION
  - FOCUS_LOST

14. Report Dashboard
- Biểu đồ điểm trung bình
- Top điểm cao
- Tỷ lệ đúng theo từng câu
- Danh sách kết quả thí sinh
- Button xuất Excel/PDF

15. Admin Dashboard
- Quản lý tài khoản
- Gán vai trò
- Quản lý quyền
- Audit logs
- Thống kê hệ thống

16. User Management Screen
- Danh sách user
- Filter theo role: Admin / Teacher / Student
- Trạng thái ACTIVE / LOCKED / PENDING
- Button tạo user
- Button khóa / mở khóa tài khoản


Student:
- Trang chủ
- Bài thi
- Kết quả
- Thông báo
- Cá nhân

Teacher:
- Dashboard
- Câu hỏi
- Đề thi
- Theo dõi
- Báo cáo

Admin:
- Dashboard
- Người dùng
- Vai trò
- Audit
- Cấu hình

Hãy tạo toàn bộ UI Android bằng Kotlin Jetpack Compose theo mô tả trên.
Tách code theo package:
- ui/auth
- ui/student
- ui/teacher
- ui/admin
- ui/components
- navigation
- model/mock

Không gọi API thật. Tạo mock data để preview đầy đủ các màn hình.
Ưu tiên giao diện đẹp, rõ ràng, đúng nghiệp vụ thi trắc nghiệm nội bộ.