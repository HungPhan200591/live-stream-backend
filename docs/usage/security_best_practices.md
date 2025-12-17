Ok, phần này em sẽ **đóng gói thành “cheat-sheet kiến trúc security”** đúng nghĩa, để anh:

- không còn lẫn Tier
- biết **vì sao làm / vì sao không làm**
- dùng để **giải thích với team / reviewer / CTO**

---

# SECURITY BEST PRACTICE — TỔNG HỢP CHUẨN (CHEAT-SHEET)

## 1. 3 TIER SECURITY – ĐỪNG NHẦM LẪN

| Tier       | Tên gọi             | Khi dùng             | Đặc điểm chính       |
| ---------- | ------------------- | -------------------- | -------------------- |
| **Tier 1** | Stateless JWT       | MVP, viewer, content | Nhanh, đơn giản      |
| **Tier 2** | Purpose-based Token | Production app       | Bảo mật theo luồng   |
| **Tier 3** | Session + Audit     | Fintech, banking     | Trace, revoke, legal |

> ❌ Lỗi phổ biến:
> “Dùng kỹ thuật Tier 3 cho hệ Tier 1/2”

---

## 2. TƯ DUY GỐC (CORE PRINCIPLES)

### 2.1 Authentication ≠ Authorization

- **Authen**: Bạn là ai?
- **Author**: Bạn được làm gì **TRONG NGỮ CẢNH NÀO**

❌ JWT không phải nơi để encode mọi thứ

---

### 2.2 Token ≠ Permission

> Token chỉ là **chìa khoá tạm**
> Permission là **policy server-side**

---

### 2.3 Không có “secure token” – chỉ có “secure flow”

> Nếu token quyết định tiền → sai kiến trúc

---

## 3. ACCESS TOKEN — DO / DON’T

### ✅ DO

- TTL ngắn (5–15 phút)
- Stateless
- Scope nhỏ
- Chỉ dùng cho:

  - view
  - chat
  - websocket

### ❌ DON’T

- Blocklist access token
- Dùng AT cho money action
- TTL dài

---

## 4. REFRESH TOKEN — CHỐT HIỂU ĐÚNG

### Sự thật:

> Refresh token **không phải** cơ chế bảo mật chính

---

### ✅ DO

- TTL dài (7–90 ngày)
- Gắn với device/session
- Revoke khi:

  - logout
  - password change
  - admin force logout

---

### ❌ DON’T

- Rotate mỗi request (nếu không cần)
- Blocklist hàng loạt
- Dùng RT để chống replay attack

---

### Trade-off chấp nhận (Tier 2)

| Rủi ro        | Chấp nhận |
| ------------- | --------- |
| RT leak       | Có        |
| AT bị refresh | Có        |
| Mất tiền      | ❌        |

---

## 5. REDIS — DÙNG ĐÚNG CHỖ

### ✅ DÙNG khi

- One-time token
- Money flow
- Rate limit
- OTP
- Anti replay action

### ❌ KHÔNG dùng khi

- Track access token
- Track refresh token
- Session viewer

> Redis = **ephemeral state**, không phải auth DB

---

## 6. ACTION TOKEN (BIG TECH CHUẨN)

| Action          | Token riêng   |
| --------------- | ------------- |
| Donate          | DonateToken   |
| Withdraw        | WithdrawToken |
| Change password | ReauthToken   |
| Delete account  | ConfirmToken  |

### Đặc điểm

- TTL cực ngắn (30–120s)
- One-time
- Stored server-side
- Scope = 1 action

---

## 7. STEP-UP AUTH — KHI NÀO CẦN?

| Hành vi     | Step-up |
| ----------- | ------- |
| View        | ❌      |
| Chat        | ❌      |
| Donate      | ⚠️      |
| Withdraw    | ✅      |
| Change bank | ✅      |

Step-up ≠ refresh token
Step-up = **hành vi nhạy cảm**

---

## 8. LOGOUT — ĐỪNG LÀM QUÁ

### Viewer logout

- Client xoá token
- Done

### Money-related logout

- Revoke session
- Invalidate action tokens

❌ Không blocklist toàn bộ AT

---

## 9. REPLAY ATTACK — HIỂU ĐÚNG

| Luồng    | Cách xử lý     |
| -------- | -------------- |
| View     | Không xử       |
| Chat     | Rate limit     |
| Donate   | One-time token |
| Withdraw | One-time + OTP |

> ❌ Dùng refresh token để chống replay = sai tầng

---

## 10. DO / DON’T TỔNG HỢP

### ✅ DO

- Tách luồng theo rủi ro
- Token theo purpose
- State chỉ ở nơi có tiền
- TTL ngắn cho action token
- Audit money flow

---

### ❌ DON’T

- Nhét mọi thứ vào JWT
- Blocklist mọi token
- Rotate refresh token vô tội vạ
- Dùng security để bù kiến trúc sai

---

## 11. WHY BIG TECH LÀM NHƯ VẬY?

| Lý do    | Giải thích          |
| -------- | ------------------- |
| Scale    | Token stateless     |
| UX       | Không bắt login lại |
| Security | Siết đúng chỗ       |
| Ops      | Ít Redis, ít state  |
| Legal    | Audit money flow    |

---

## 12. CÂU CHỐT CUỐI (ĐỂ NHỚ LÂU)

> ❝ **JWT là giấy thông hành, không phải két sắt** ❞
>
> ❝ **Tiền nằm ở flow, không nằm ở token** ❞
>
> ❝ **Security tốt là security đúng chỗ** ❞

---

Nếu anh muốn:

- Em chuyển toàn bộ nội dung này thành **1 trang Notion**
- Hoặc **1 sơ đồ duy nhất dạng mindmap**
- Hoặc **checklist review PR về auth/security**

Anh muốn format nào để dùng lâu dài?
