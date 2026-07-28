---
name: commit-livestream-change
description: Tạo một Git commit cục bộ nhanh cho live-stream-backend bằng model nhẹ, sau khi kiểm tra secret và staged diff; tuyệt đối không push. Gọi trực tiếp `$commit-livestream-change` mà không ghi thêm nội dung để commit toàn bộ thay đổi hiện tại. Dùng khi người dùng yêu cầu commit, local commit hoặc commit scope cụ thể. Không dùng khi chỉ cần sinh commit message hoặc review read-only.
---

# Commit Livestream Change

## Contract

- Xem invocation trực tiếp là quyền commit trong turn hiện tại.
- Không có scope nghĩa là commit toàn bộ tracked, untracked và staged changes thành một commit.
- Có scope thì chỉ commit scope đó và dừng nếu index chứa staged path ngoài scope.
- Không `push`, `pull`, `fetch`, `rebase`, `amend`, reset, force, empty commit, đổi author hoặc dùng `--no-verify`.
- Không sửa source. Dừng khi script phát hiện secret, conflict marker hoặc commit hook lỗi.

## Fast orchestration

Nếu đang ở primary agent và có thể spawn subagent:

1. Spawn đúng một agent với model `gpt-5.6-terra`, reasoning `low` và không fork lịch sử chat.
2. Truyền scope hiện tại và yêu cầu: đọc skill này, tự nhận là execution agent, không delegate tiếp, chạy Fast local path, tạo commit và không push.
3. Chờ kết quả. Không đọc lại diff, chạy lại validation hoặc lặp verification đã được execution agent báo.
4. Nếu spawn không khả dụng hoặc agent lỗi trước khi thay đổi Git state, chạy Fast local path tại primary agent.

Mục tiêu của delegation là dùng model nhanh/rẻ với context tối thiểu. Không spawn nhiều agent và không parallelize Git operations.

## Fast local path

### 1. Chuẩn bị một lần

Từ repository root, chạy bundled script đúng một lần:

```powershell
& ".agents/skills/commit-livestream-change/scripts/prepare-commit.ps1"
```

Với scope cụ thể:

```powershell
& ".agents/skills/commit-livestream-change/scripts/prepare-commit.ps1" -Scope @("<path-1>", "<path-2>")
```

Script thực hiện trong một pass:

- tìm path thay đổi và stage bằng pathspec tường minh;
- chặn filename/content có dấu hiệu credential và merge conflict;
- chạy `git diff --cached --check`;
- trả `name-status`, stat và staged diff tối đa 180 dòng.

Xử lý kết quả:

- `CLEAN`: báo working tree sạch; không tạo empty commit.
- `BLOCKED`: báo đúng lý do; không commit.
- `PREPARED`: dùng output duy nhất này để soạn message; không gọi lại full diff.

Không chạy test trong commit workflow. Tái sử dụng evidence của task trước đó; nếu không có, báo `not run` sau commit. Commit hook vẫn là gate bắt buộc.

### 2. Soạn message

Từ prepared diff, tạo subject ngắn:

```text
<type>(<scope>): <imperative summary>
```

Ưu tiên `feat`, `fix`, `test`, `docs`, `refactor` hoặc `chore`. Không thêm body trừ khi staged diff cần giải thích compatibility/migration quan trọng. Không gọi model hoặc agent thứ hai chỉ để sửa câu chữ.

### 3. Commit và xác minh trong một call

Chạy một shell call gồm commit, sau đó chỉ khi commit thành công mới in summary và status:

```powershell
git commit -m "<message>"
git show --stat --oneline --summary HEAD
git status --short --branch
```

Không dùng command separator làm commit tiếp tục khi lệnh trước thất bại. Nếu hook fail, giữ staged state và báo lỗi gốc.

## Bàn giao

Chỉ báo:

- SHA và subject;
- số/file đã commit;
- validation evidence đã tái sử dụng hoặc `not run`;
- working-tree status;
- xác nhận chưa push.
