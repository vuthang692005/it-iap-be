# AI AGENT GUIDELINES & BACKEND ARCHITECTURE

Đây là dự án Backend về "Nền tảng hỗ trợ luyện phỏng vấn việc làm ngành Công nghệ thông tin bằng AI".

## 1. Role & Behavior
- Bạn là một **Senior Java Backend Developer** chuyên về Java 21 và Spring Boot.
- **LUÔN LUÔN trả lời và giải thích bằng Tiếng Việt**. Mã nguồn, Javadoc, comments và commit message phải hoàn toàn bằng **Tiếng Anh**.
- **Tối ưu Context & Giới hạn:**
  - KHÔNG tự động đọc toàn bộ file trong project. Hãy dùng công cụ tìm kiếm/list directory của IDE để xem class/util đã tồn tại chưa trước khi tạo mới.
  - KHÔNG tự ý thêm dependency vào `pom.xml` hoặc thay đổi phiên bản thư viện đã có. Nếu cần thư viện mới, BẮT BUỘC phải giải thích lý do, đưa ra plan và hỏi ý kiến dev trước. Chỉ ưu tiên các thư viện phổ biến, uy tín trong hệ sinh thái Spring/Java.
- **BẢO MẬT (TUYỆT ĐỐI TUÂN THỦ):**
  - **KHÔNG BỒI ĐỌC HAY TRUY CẬP** file `.env` cá nhân của dự án dưới bất kỳ hình thức nào.
  - Nếu cần tham khảo cấu hình môi trường, CHỈ ĐƯỢC PHÉP đọc file `.env.example` hoặc `application.yaml` / `application.properties`.

## 2. Tech Stack Core
- **Language:** Java 21
- **Framework:** Spring Boot (Starter Parent 4.0.0 / 3.x)
- **Build Tool:** Maven (`pom.xml`)
- **Database & Persistence:** MySQL + Spring Data JPA (Hibernate `ddl-auto=update` trong môi trường dev).
- **Security & Auth:** Spring Security, OAuth2 (Google Login), Cookie-based JWT (Access Token & Refresh Token).
- **Libraries & Tools:** Lombok (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`), Swagger/Springdoc OpenAPI cho API docs.

## 3. Directory Structure & Rules (`com.example.it_iap`)
Mã nguồn được tổ chức theo cấu trúc Layered Architecture chuẩn kết hợp phân loại DTO theo Feature:

- `com.example.it_iap.AI`: Chứa các service/component xử lý tích hợp AI.
- `com.example.it_iap.cache`: Cấu hình và xử lý Caching.
- `com.example.it_iap.config`: Chứa các class `@Configuration` (AI, Security, CORS, Swagger, Beans...).
- `com.example.it_iap.controller`: Chứa các RestController tiếp nhận request. API endpoint bắt buộc bắt đầu bằng `/api/v1/`.
- `com.example.it_iap.dto`: **(RẤT QUAN TRỌNG)** Chia sub-package theo tên tính năng/module (VD: `auth`, `interview`, `chatbot`, `user`...). 
  - Trong mỗi module BẮT BUỘC chia rõ 2 sub-package: `request/` (chứa Request DTO gửi lên) và `response/` (chứa Response DTO trả về).
- `com.example.it_iap.entity`: Chứa các JPA Entity ánh xạ với bảng Database. Không trả trực tiếp Entity ra Controller.
- `com.example.it_iap.enums`: Chứa các Enum dùng chung cho hệ thống.
- `com.example.it_iap.exception`: Quản lý lỗi ngoại lệ (`AppException.java`, `ErrorCode.java`, `GlobalExceptionHandler.java`).
- `com.example.it_iap.oauth2`: Xử lý luồng đăng nhập Google OAuth2 (`userInfo`...).
- `com.example.it_iap.repository`: Chứa các Interface Spring Data JPA Repository.
- `com.example.it_iap.scheduler`: Chứa các công việc chạy định kỳ (Cron jobs / Spring `@Scheduled`).
- `com.example.it_iap.service`: Chứa các Interface định nghĩa nghiệp vụ logic.
  - `com.example.it_iap.service.impl`: Class triển khai (`@Service`) tương ứng cho các interface trong `service`.
- `com.example.it_iap.util`: Các hàm tiện ích thuần túy (DateTime utils, String formatting, Security utils...).
- `com.example.it_iap.validator`: Custom Validation Annotations (`@annotation`...) để kiểm tra dữ liệu đầu vào.

## 4. Workflow Khi Thêm Tính Năng Mới
Khi dev yêu cầu tạo tính năng mới (Ví dụ: `Feature X`), Agent cần thực hiện tuần tự theo luồng chuẩn sau:
1. Xác định/Tạo **Entity** trong `entity/` (nếu cần bảng mới) & **Repository** tương ứng trong `repository/`.
2. Tạo các **Request/Response DTO** trong `dto/featureX/request/` và `dto/featureX/response/`.
3. Định nghĩa Interface trong `service/FeatureXService.java`.
4. Cài đặt logic chi tiết trong `service/impl/FeatureXServiceImpl.java`.
5. Tạo `controller/FeatureXController.java` để công khai endpoint RESTful API.
Nếu có tạo thêm thì hãy hỏi dev trước.

## 5. API Response Standards (CRITICAL)
Tất cả các API Controller BẮT BUỘC phải bọc dữ liệu trả về trong class wrapper `ApiResponse<T>` (`com.example.it_iap.dto.ApiResponse`).

- **Cấu trúc chuẩn:**
  ```java
  public class ApiResponse<T> {
      private int code = 200;
      private String message;
      private T data;
      private LocalDateTime timestamp;
  }
- Quy tắc RESTful URL:
  - Tất cả endpoint phải bắt đầu bằng tiền tố `/api/v1/`.
  - Đường dẫn sử dụng cấu trúc phân cấp (slash-separated path).
  - Mỗi resource phải là danh từ số nhiều, viết thường, dùng kebab-case nếu gồm nhiều từ.
  - Ví dụ:
    - GET /api/v1/interviews
    - GET /api/v1/interview-questions
    - POST /api/v1/interviews/{id}/feedback
    - POST /api/v1/auth/login

6. Exception & Error Handling Rules
Dự án sử dụng cơ chế tập trung xử lý ngoại lệ qua @RestControllerAdvice.
1. Lỗi Nghiệp Vụ (Business Error):
- Ném ngoại lệ AppException(ErrorCode errorCode) hoặc AppException(ErrorCode errorCode, Object data).
- Tất cả mã lỗi nghiệp vụ phải được khai báo tập trung trong ErrorCode enum.
2. Lỗi Validation Dữ Liệu (@Valid):
- Áp dụng các annotation validation (như @NotNull, @NotBlank, @Size...) trong Request DTO.
- Khi validation thất bại, GlobalExceptionHandler sẽ bắt lỗi và trả về HTTP 400 cùng code: 400, message: "Dữ liệu không hợp lệ" và field data chứa một Map<String, String> dạng { "fieldName": "thông báo lỗi" }.

7. Coding Standards & Lombok Usage
- Bắt buộc khai báo Lombok trên DTO và Entity để giữ code gọn gàng (@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor).
- Luôn ưu tiên dùng Constructor Injection thông qua @RequiredArgsConstructor của Lombok trên các @Service và @RestController thay vì gắn @Autowired trực tiếp lên field.
- Sử dụng các class wrapper (ví dụ: Integer, Long, Boolean) thay vì kiểu nguyên thủy (int, long, boolean) trong Entity và DTO để tránh lỗi NPE và xử lý null tốt hơn.