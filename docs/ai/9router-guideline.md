# Hướng dẫn dùng 9Router cục bộ

> Phạm vi: cài đặt và vận hành 9Router như một AI gateway trên máy phát triển Windows.
>
> Cập nhật: 2026-07-28. Đã kiểm tra từ Windows Command Prompt với Node.js `24.18.0`, npm `11.6.2` và 9Router `0.5.40`.
>
> Nguồn: [9Router repository](https://github.com/decolua/9router), [npm package](https://www.npmjs.com/package/9router).

## 1. 9Router nằm ở đâu trong môi trường dự án?

9Router là tiến trình Node.js đứng giữa AI client và các provider. Nó cung cấp endpoint tương thích OpenAI tại `http://127.0.0.1:20128/v1`, quản lý API key/OAuth của provider và định tuyến request theo cấu hình trên dashboard.

```text
Codex / IDE / CLI
        |
        | OpenAI-compatible request
        v
9Router trên localhost:20128
        |
        | API key hoặc OAuth do người dùng cấu hình
        v
AI provider
```

9Router không phải dependency của Spring Boot và không tham gia build hoặc runtime của `live-stream-backend`. Node.js là runtime của 9Router; JDK vẫn là runtime của Maven/Spring Boot. Khi kiểm tra platform baseline, tiếp tục dùng [Java Interview Question Bank](../learning/topics/java/question-bank/jdk-platform.md) làm nguồn cho Java/JDK, không dùng phiên bản Node.js hay 9Router làm evidence cho Java.

## 2. Cài đặt và kiểm tra

Yêu cầu Node.js 20 trở lên. Kiểm tra runtime trước khi cài:

```powershell
node --version
npm.cmd --version
```

Cài CLI toàn cục và xác minh executable. npm 11 có thể chặn lifecycle script mặc định; 9Router cần `postinstall` để chuẩn bị SQLite runtime, nên chỉ cho phép script của đúng package/version đang cài:

```powershell
npm.cmd install --global 9router@0.5.40 --allow-scripts=9router@0.5.40
9router --version
9router --help
```

Nếu dùng phiên bản npm cũ không hỗ trợ `--allow-scripts`, bỏ option đó và theo dõi output cài đặt để bảo đảm `postinstall` không bị chặn.

Nếu PowerShell không tìm thấy `9router`, mở terminal mới rồi kiểm tra thư mục npm global:

```powershell
npm.cmd prefix --global
Get-Command 9router
```

Trong Command Prompt, dùng:

```bat
npm prefix --global
where node
where npm
where 9router
```

Thư mục trả về bởi `npm prefix --global` phải nằm trên `PATH` của chính terminal sẽ chạy 9Router. Một IDE hoặc AI Agent có thể kèm Node.js riêng và đổi npm global prefix chỉ trong tiến trình của nó; việc `9router --version` chạy được trong terminal đó không chứng minh Command Prompt bên ngoài cũng tìm thấy executable. Với NVM for Windows, cài bằng npm của Node đang active và xác minh lại bằng `where 9router`.

Không thêm 9Router vào `pom.xml` hoặc `package.json` của repository. Đây là công cụ cục bộ của developer, không phải dependency ứng dụng.

## 3. Khởi động an toàn

CLI hiện tại bind vào `0.0.0.0` theo mặc định, khiến dashboard và gateway có thể được truy cập từ máy khác trong cùng mạng. Với môi trường phát triển cá nhân, luôn giới hạn vào loopback:

```powershell
9router --host 127.0.0.1
```

Không tự mở trình duyệt:

```powershell
9router --host 127.0.0.1 --no-browser
```

Sau khi tiến trình chạy:

- Dashboard: `http://127.0.0.1:20128/dashboard`
- OpenAI-compatible base URL: `http://127.0.0.1:20128/v1`
- Kiểm tra port trên Windows: `Test-NetConnection 127.0.0.1 -Port 20128`

Nếu port `20128` đã bị chiếm, chọn port khác và dùng cùng giá trị trong cấu hình client:

```powershell
9router --host 127.0.0.1 --port 20129
```

## 4. Thiết lập lần đầu

1. Mở dashboard và đổi password mặc định ngay trong phần Settings.
2. Chỉ kết nối provider mà bạn được phép sử dụng; kiểm tra điều khoản của provider trước khi dùng OAuth token hoặc subscription qua gateway.
3. Tạo API key riêng cho client trên dashboard.
4. Chọn model/provider và kiểm tra một request nhỏ trước khi chuyển workflow chính sang 9Router.
5. Cấu hình client bằng ba giá trị: base URL, API key do 9Router sinh và model ID hiển thị trên dashboard.

Không ghi API key, OAuth token, cookie hoặc nội dung database vào repository, file `.http`, log, ảnh chụp hay tài liệu này. Không dùng webhook secret hoặc credential của `live-stream-backend` làm key cho 9Router.

## 5. Dữ liệu cục bộ và ranh giới bảo mật

Trên Windows, dữ liệu mặc định nằm dưới `%APPDATA%\9router`; database thường ở `%APPDATA%\9router\db\data.sqlite`. Thư mục này có thể chứa cấu hình và credential nhạy cảm:

- Không commit, đồng bộ công khai hoặc gửi nguyên thư mục để debug.
- Không copy database giữa nhiều người dùng hoặc máy nếu chưa rà soát credential.
- Khi backup, coi file backup như secret và bảo vệ tương đương password manager.
- Chỉ bind ra LAN khi có nhu cầu rõ ràng, đã đổi password, cấu hình firewall và hiểu ai có thể kết nối.
- Cập nhật 9Router thường xuyên vì gateway xử lý credential và request tới provider.

Kiểm tra phiên bản mới trước khi nâng cấp:

```powershell
npm.cmd view 9router version
9router --version
```

Nâng cấp:

```powershell
npm.cmd install --global 9router@latest
9router --version
```

## 6. Chẩn đoán nhanh

| Hiện tượng | Kiểm tra | Hướng xử lý |
| --- | --- | --- |
| Không chạy được lệnh | `npm prefix --global`, `where node`, `where npm`, `where 9router` | Cài bằng npm của Node đang active; bảo đảm global prefix nằm trên `PATH`, rồi mở terminal mới |
| Dashboard không mở | `Test-NetConnection 127.0.0.1 -Port 20128` | Xem terminal của 9Router; kiểm tra port hoặc đổi port |
| Client nhận `401`/`403` | API key của client và trạng thái provider | Tạo/chép lại key từ dashboard; không dùng key của provider thay cho key gateway |
| Không tìm thấy model | Model ID trên dashboard | Dùng đúng model ID có prefix do 9Router công bố |
| Request timeout | Trạng thái provider, quota và log 9Router | Gửi request nhỏ; kiểm tra quota/provider trước khi đổi code backend |
| Port bị chiếm | `Get-NetTCPConnection -LocalPort 20128` | Dừng đúng process nếu thuộc quyền quản lý hoặc chạy 9Router trên port khác |

Khi cần log để chẩn đoán, chạy:

```powershell
9router --host 127.0.0.1 --log
```

Trước khi chia sẻ log, xóa token, authorization header, prompt/source code nhạy cảm và payload có dữ liệu người dùng.

## 7. Dừng và gỡ cài đặt

Với tiến trình foreground, nhấn `Ctrl+C` trong terminal đang chạy. Gỡ CLI:

```powershell
npm.cmd uninstall --global 9router
```

Lệnh gỡ CLI không đồng nghĩa với xóa dữ liệu trong `%APPDATA%\9router`. Không xóa thư mục dữ liệu cho tới khi đã xác nhận không cần cấu hình, credential hoặc lịch sử liên quan.

## Checklist vận hành

- [ ] Chạy bằng `--host 127.0.0.1`.
- [ ] Đã đổi password mặc định.
- [ ] Không có secret 9Router trong Git hoặc log chia sẻ.
- [ ] Provider và model ID được kiểm tra trên dashboard.
- [ ] Client dùng đúng base URL, API key và port.
- [ ] Phiên bản 9Router được kiểm tra trước khi điều tra lỗi tương thích.
- [ ] JDK và Node.js được đánh giá như hai runtime độc lập.
