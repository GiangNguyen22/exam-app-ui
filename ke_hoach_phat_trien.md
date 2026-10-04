# KẾ HOẠCH PHÁT TRIỂN & KỊCH BẢN DEMO ẤN TƯỢNG CHO GIÁO VIÊN (INTERNAL EXAM SYSTEM)

> **Dự án**: Hệ thống Thi Trắc nghiệm Nội bộ (Internal Exam Application)  
> **Tập trung**: Tối ưu Trải nghiệm Thực tế & Cực kỳ Dễ dàng Demo (Demo-Friendly & Live Simulator)  
> **Cập nhật**: Tháng 10/2026  

---

## I. ĐÁNH GIÁ TÍNH HỢP LÝ VÀ ĐIỂM CẢI TIẾN ĐỂ "DỄ DEMO" CHO GIÁO VIÊN

### 1. Đánh giá tính hợp lý
Các chức năng đã đề xuất (**Scan ảnh đề thi**, **Tối ưu phần làm bài**, **Giám sát realtime chống gian lận**, **Báo cáo thống kê**) là **RẤT HỢP LÝ**, giải quyết trực tiếp 3 nỗi đau lớn nhất của Giáo viên:
1. **Tốn thời gian nhập liệu đề thi** -> Giải quyết bằng Scan ảnh AI & Import Excel.
2. **Khó phát hiện học sinh gian lận khi thi trên máy tính/điện thoại** -> Giải quyết bằng Dashboard Giám sát Realtime & Cheat Risk Score.
3. **Mất công chấm bài và cộng điểm** -> Giải quyết bằng Tự động chấm bài & Xuất báo cáo PDF/Excel tức thì.

### 2. Yếu tố then chốt để "DỄ DEMO" cho Giáo viên (Demo-Friendly Enhancements)
Khi đi demo trực tiếp cho Giáo viên hoặc Hội đồng nhà trường, Giáo viên **không muốn phải thao tác phức tạp hay chờ đợi lâu**. Vì vậy, kế hoạch này bổ sung thêm **Chế độ Giả lập Demo (Demo Simulator & Presets)**:

| Chức năng | Khó khăn khi Demo thường | Giải pháp Tối ưu để DEMO DỄ NỔI BẬT |
| :--- | :--- | :--- |
| **1. Scan ảnh thành đề thi (AI OCR)** | Phải tìm ảnh trong máy, chờ AI phân tích | Thêm **"Nút Ảnh Mẫu Demo"** (Đề Toán, Đề Tiếng Anh có sẵn). Bấm 1 phát là tự nạp ảnh + bóc tách xong trong 2 giây! |
| **2. Giám sát gian lận Realtime** | Phải lấy 2-3 điện thoại học sinh thật để thoát app, chụp màn hình | Thêm **"Thanh Giả lập Sự kiện (Simulator Bar)"** trên màn hình GV: Bấm `"Simulate HS A thoát App"` -> Thẻ HS nổ cảnh báo đỏ + Risk Score 85% ngay lập tức! |
| **3. Làm bài thi Học sinh** | Phải ngồi chờ 45 phút để xem timer đổi màu | Thêm nút **"Demo Fast-Forward 1 phút cuối"** -> Timer chuyển đỏ rực chớp nháy + Rung Haptic ngay tại chỗ. |
| **4. In & Xuất Đề thi PDF** | Phải kết nối máy in thật | Thêm nút **"Xem bản In PDF đẹp chuẩn Bộ GD&ĐT"** hiển thị ngay trên màn hình dạng Preview PDF có logo trường. |

---

## II. KỊCH BẢN DEMO 3 PHÚT CHUẨN THỰC TẾ CHO GIÁO VIÊN (LIVE DEMO SCRIPT)

```
[00:00 - 00:45] DEMO 1: SCAN ĐỀ THI TỪ ẢNH BẰNG AI
  └─ Bấm "Scan đề thi" -> Chọn "Ảnh mẫu Đề thi Toán 10" -> AI bóc tách ra 5 câu hỏi có đáp án A,B,C,D & Công thức Toán LaTeX đẹp mắt -> Bấm "Tạo đề thi".

[00:45 - 01:30] DEMO 2: LÀM BÀI THI TẬP TRUNG (STUDENT EXPERIENCE)
  └─ Học sinh vào thi -> Gắn cờ câu hỏi -> Đổi kích thước chữ to -> Bấm nút "Giả lập còn 1 phút" để xem cảnh báo sắp hết giờ -> Bấm "Nộp bài".

[01:30 - 02:30] DEMO 3: GIÁM SÁT GIAN LẬN REALTIME (LIVE PROCTORING)
  └─ Mở màn hình Live Monitoring -> Bấm nút giả lập "Thí sinh Nguyễn Văn A chụp màn hình" -> Hệ thống nổi badge ĐỎ + Risk Score = 90% -> Giáo viên bấm nút "Gửi cảnh báo" hoặc "Đình chỉ thi".

[02:30 - 03:00] DEMO 4: CHẤM ĐIỂM TỰ ĐỘNG & XUẤT BÁO CÁO PDF/EXCEL
  └─ Xem phổ điểm tự động nhảy -> Xem Top 3 câu hỏi làm sai nhiều nhất -> Bấm "Xuất file Bảng điểm Excel" & "In báo cáo PDF".
```

---

## III. CHI TIẾT CÁC TASK PHÁT TRIỂN (CÓ TÍCH HỢP CHẾ ĐỘ DEMO)

```
[Task 1: AI Scan ảnh đề thi (Preset Demo)] ──┐
                                             ├──> [Task 5: UI/UX & Demo Controls]
[Task 2: Trải nghiệm Học sinh (Fast-Timer)] ──┤
                                             │
[Task 3: Giám sát Realtime (Event Sim)]     ──┤
                                             │
[Task 4: Báo cáo & Xuất PDF/Excel]          ──┘
```

---

### TASK 1: Scan ảnh bài thi thành Đề thi số (Tích hợp Ảnh mẫu Demo nhanh)
- **Tên Task**: `TASK-01-AI-IMAGE-OCR-EXAM-SCANNER`
- **Phụ thuộc**: Không có.
- **Tính năng Dễ Demo**:
  - Có sẵn 2 nút Ảnh mẫu: **[Ảnh Mẫu Đề Toán]** và **[Ảnh Mẫu Đề Tiếng Anh]**.
  - Bấm chọn ảnh mẫu -> Tự động nạp sẵn dữ liệu JSON bóc tách đẹp mắt có hình vẽ và công thức LaTeX mà không cần phụ thuộc vào mạng internet khi đi demo.
  - Cho phép sửa trực tiếp đáp án đúng A, B, C, D ngay trên danh sách.

- **Prompt chi tiết cho AI thực hiện Code**:
```text
Hãy phát triển chức năng Scan ảnh thành đề thi `ScanExamScreen.kt` cho Giáo viên, tối ưu hóa để DEMO CỰC KỲ DỄ DÀNG:

1. Trong `ScanExamScreen.kt`, thiết kế giao diện dạng Wizard 3 bước:
   - Bước 1: Chọn ảnh. Bổ sung ngay 2 nút Nổi bật: "Chụp ảnh thật", "Thử nghiệm với Ảnh Mẫu Toán", "Thử nghiệm với Ảnh Mẫu Tiếng Anh".
   - Bước 2: Khi chọn Ảnh Mẫu Demo, giả lập tiến trình AI đọc trong 1.5 giây (Progress bar chạy từ 0% đến 100%), sau đó tự động nạp danh sách 5 câu hỏi trắc nghiệm chuẩn có công thức LaTeX và đáp án đã được tích chọn trước.
   - Bước 3: Cho phép Giáo viên chạm vào từng câu để chỉnh sửa nội dung, thay đổi đáp án đúng (Radio Button), bấm nút "Thêm câu mới" hoặc "Xóa câu".
2. Bổ sung nút "Tạo đề thi mới" -> Tự động chuyển hướng sang `CreateExamScreen` với danh sách câu hỏi đã được điền sẵn.
3. Đăng ký Route `Routes.ScanExam = "teacher/exams/scan"` và thêm nút Card shortcut nổi bật tại `TeacherDashboardScreen.kt`.
```

---

### TASK 2: Tối ưu Làm bài Học sinh (Tích hợp Nút Giả lập Thời gian & Tùy chỉnh)
- **Tên Task**: `TASK-02-STUDENT-EXAM-TAKING-OPTIMIZATION`
- **Phụ thuộc**: `StudentScreens.kt`, `ExamAttemptStore.kt`.
- **Tính năng Dễ Demo**:
  - Gắn cờ (Flag) câu hỏi -> Bấm Tab lọc "Đã gắn cờ" để thấy ngay danh sách thu gọn.
  - Nút **[Demo: Thử còn 30 giây]** trên thanh TopBar -> Bấm phát Timer lập tức đếm ngược từ 00:30, đổi sang màu đỏ rực chớp nháy + tạo nhịp rung Haptic.
  - Thanh Slider điều chỉnh phông chữ chữ to/nhỏ trực quan.

- **Prompt chi tiết cho AI thực hiện Code**:
```text
Hãy tối ưu hóa màn hình làm bài thi của Học sinh `ExamTakingScreen` trong `StudentScreens.kt` để vừa nâng cấp trải nghiệm vừa DỄ DEMO TRỰC TIẾP:

1. Thêm nút Đánh dấu cờ (Flag) cho câu hỏi + Bộ lọc Tab ngang: [Tất cả], [Chưa chọn], [Đã cờ (N)].
2. Trên TopBar màn hình thi, bổ sung một nút ẩn/hiện nhỏ `[Demo: Fast-Forward]` dành cho người thuyết trình:
   - Khi bấm vào, đếm ngược thời gian bài thi lập tức nhảy về `00:30` (30 giây cuối).
   - Thanh Timer hiển thị màu Đỏ chớp nháy (AppRed) và gọi `LocalHapticFeedback` rung nhẹ báo hiệu sắp hết giờ.
3. Thêm BottomSheet Cài đặt hiển thị: Cho phép kéo Slider để phóng to/thu nhỏ cỡ chữ đề thi từ 14sp lên 22sp.
4. Đảm bảo trạng thái câu trả lời tự động lưu vết `lastSavedTime` hiển thị rõ dòng chữ: "Tự động lưu bài lúc HH:mm:ss".
```

---

### TASK 3: Giám sát Trực tiếp & Chống Gian lận cho Giáo viên (Tích hợp Thanh Giả lập Sự kiện Realtime)
- **Tên Task**: `TASK-03-TEACHER-LIVE-PROCTORING-ANOMALY`
- **Phụ thuộc**: `LiveMonitoringScreen.kt`, `ProctoringEventBuffer.kt`.
- **Tính năng Dễ Demo**:
  - Thanh điều khiển Giả lập **[Demo Event Simulator Bar]** cố định ở cuối màn hình Giám sát của Giáo viên:
    - Nút 1: `[Simulate HS Thoát App]` -> Giả lập học sinh "Trần Văn B" thoát app -> Thẻ chuyển màu ĐỎ, hiện icon cảnh báo + Risk Score nhảy lên 85%.
    - Nút 2: `[Simulate HS Chụp màn hình]` -> Nổi Toast thông báo vi phạm nghiêm trọng.
    - Nút 3: `[Reset Trạng thái]`.
  - Giáo viên chạm vào học sinh vi phạm -> Hiện Modal chi tiết mốc thời gian vi phạm + 2 Nút bấm ăn tiền: **[Gửi Cảnh Báo]**, **[Đình Chỉ Thi]**.

- **Prompt chi tiết cho AI thực hiện Code**:
```text
Hãy nâng cấp màn hình Giám sát Trực tiếp `LiveMonitoringScreen` trong `TeacherScreens.kt` thành Dashboard chống gian lận siêu trực quan và DỄ DEMO:

1. Thêm thanh công cụ `DemoSimulatorBar` ở cuối màn hình Giám sát (chỉ hiển thị trong môi trường Demo):
   - Nút `[Thoát App]`: Tự động bắn 1 sự kiện `APP_EXIT` giả lập cho 1 thí sinh ngẫu nhiên.
   - Nút `[Chụp Ảnh]`: Bắn sự kiện `SCREENSHOT` làm tăng Cheat Risk Score của thí sinh lên > 80%.
   - Nút `[Rớt Mạng]`: Chuyển trạng thái thí sinh sang "Mất kết nối".
2. Cập nhật UI Card thí sinh:
   - Màu xanh: An toàn | Màu cam: Cảnh báo nhẹ | Màu đỏ: Vi phạm nặng.
   - Badge hiển thị Điểm rủi ro (VD: `Risk: 85%`).
3. Click vào Card thí sinh -> Mở Sheet xem Chi tiết Nhật ký Vi phạm (Timeline thời gian) kèm nút "Gửi nhắc nhở" và "Đình chỉ bài thi".
```

---

### TASK 4: Thống kê Báo cáo Nâng cao & In/Xuất File PDF/Excel
- **Tên Task**: `TASK-04-TEACHER-ANALYTICS-PDF-EXCEL-EXPORT`
- **Phụ thuộc**: `ReportDashboardScreen.kt`.
- **Tính năng Dễ Demo**:
  - Bấm nút **[Xuất Báo cáo Excel]** -> Tự động sinh file `.xlsx` và hiển thị Dialog xem trước bảng điểm.
  - Bấm nút **[Xem Bản In PDF]** -> Hiển thị ngay Modal Preview bản in PDF kết quả thi có đầy đủ Header trường học, Phổ điểm và Chữ ký Giáo viên.

- **Prompt chi tiết cho AI thực hiện Code**:
```text
Hãy hoàn thiện màn hình Báo cáo Thống kê `ReportDashboardScreen` trong `TeacherScreens.kt` với tính năng Xuất File trực quan dễ Demo:

1. Thiết kế UI Báo cáo:
   - Thẻ chỉ số tổng quan (Điểm TB, Điểm Cao nhất, Tỷ lệ Đạt).
   - Biểu đồ phổ điểm dạng Cột (Bar Chart) vẽ trực tiếp bằng Compose Canvas.
   - Danh sách Top 3 câu hỏi có tỷ lệ làm sai cao nhất lớp.
2. Thêm tính năng Demo Xuất File:
   - Nút "Xuất Bảng Điểm Excel": Tạo file Excel giả định và bật Intent Chia sẻ/Tải về.
   - Nút "Xem Báo Cáo In PDF": Mở Modal hiển thị trang báo cáo đẹp mắt dạng văn bản in ấn chính thức.
```

---

### TASK 5: Tối ưu UI/UX & Bộ nút Lối tắt Demo (System Design & Demo Launcher)
- **Tên Task**: `TASK-05-SYSTEM-DESIGN-SYSTEM-DEMO-LAUNCHER`
- **Phụ thuộc**: Toàn bộ dự án.
- **Tính năng Dễ Demo**:
  - Thêm một Floating Action Button hoặc Menu ẩn **[Quick Demo Menu]** ở góc màn hình giúp người trình bày có thể chuyển nhanh đến bất kỳ màn hình nào (Scan Đề, Phòng Thi Học Sinh, Giám Sát Realtime, Báo Cáo Thống Kê) chỉ bằng 1 cú chạm.

- **Prompt chi tiết cho AI thực hiện Code**:
```text
Hãy bổ sung Menu Lối tắt Demo `QuickDemoFloatingMenu` vào `InternalExamApp.kt` để người trình bày dễ dàng chuyển đổi giữa các chức năng khi demo cho Giáo viên.

1. Thiết kế một Nút Nổi (FAB) nhỏ ở góc màn hình: "🚀 Demo Menu".
2. Khi bấm vào, hiển thị Dialog chọn nhanh màn hình:
   - 📸 1. Scan Ảnh thành Đề thi
   - ✍️ 2. Giao diện Làm bài Học sinh (Chế độ thi)
   - 🛡️ 3. Dashboard Giám sát Realtime (Chống gian lận)
   - 📊 4. Báo cáo Thống kê & Xuất PDF/Excel
3. Giúp quá trình thuyết trình diễn ra liên tục, mượt mà mà không cần qua nhiều bước đăng nhập/chuyển menu thủ công.
```

---

## IV. BẢNG TỔNG HỢP TIẾN ĐỘ & PHỤ THUỘC

| Task ID | Tên Task | Mức độ Ưu tiên | Khả năng DEMO | Thời gian |
| :--- | :--- | :---: | :---: | :---: |
| **TASK-01** | AI Scan OCR Đề thi (Preset Demo) | 🔴 Cao | ⭐⭐⭐⭐⭐ (Rất ấn tượng) | 3 - 4 ngày |
| **TASK-02** | Tối ưu Làm bài Học sinh (Fast-Timer) | 🔴 Cao | ⭐⭐⭐⭐ (Rất trực quan) | 2 - 3 ngày |
| **TASK-03** | Giám sát Realtime (Event Simulator) | 🔴 Cao | ⭐⭐⭐⭐⭐ (Gây bất ngờ lớn) | 3 ngày |
| **TASK-04** | Thống kê & Xuất PDF/Excel | 🟡 Trung bình | ⭐⭐⭐⭐ (Chuyên nghiệp) | 2 ngày |
| **TASK-05** | Demo Quick Launcher & UI/UX | 🟢 Khá | ⭐⭐⭐⭐⭐ (Hỗ trợ demo) | 1 - 2 ngày |
