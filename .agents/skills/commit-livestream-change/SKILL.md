---
name: commit-livestream-change
description: Tạo Git commit cục bộ an toàn cho live-stream-backend sau khi kiểm tra phạm vi, staged diff, secret, validation evidence và commit message. Gọi trực tiếp `$commit-livestream-change` mà không ghi thêm nội dung được xem là yêu cầu stage và commit toàn bộ thay đổi hiện tại thành một commit; yêu cầu có scope thì chỉ commit scope đó. Tuyệt đối không push. Không dùng khi người dùng chỉ hỏi cách viết commit message hoặc chỉ yêu cầu review.
---

# Commit Livestream Change

## Nguyên tắc

- Xem việc người dùng gọi trực tiếp `$commit-livestream-change` là quyền tạo commit trong turn hiện tại, kể cả khi không có nội dung nào khác.
- Khi skill được gọi trực tiếp mà không có scope, mặc định stage và commit toàn bộ thay đổi tracked, untracked và staged hiện tại trong repository thành một commit.
- Không `push`, `pull`, `fetch`, `rebase`, tạo PR hoặc gọi remote API.
- Không `amend`, reset, force, bỏ qua hook, tạo empty commit hay sửa author trừ khi người dùng yêu cầu rõ.
- Không tự sửa code để làm diff đẹp hơn. Nếu phát hiện vấn đề, dừng và báo trước khi commit.
- Khi người dùng chỉ định scope, giữ nguyên thay đổi ngoài scope. Dù scope mặc định là toàn repository, không dùng `git add .`, `git add -A` hoặc wildcard rộng; stage danh sách path tường minh lấy từ `git status`.
- Mặc định tạo một commit, kể cả khi thay đổi thuộc nhiều concern. Chỉ tạo nhiều commit khi người dùng yêu cầu tách.

## Workflow

### 1. Xác định trạng thái và phạm vi

Chạy:

```powershell
git status --short --branch
git diff --name-status
git diff --cached --name-status
```

Đọc diff liên quan bằng `git diff -- <path>` và `git diff --cached -- <path>`. Với file untracked, đọc trực tiếp nội dung cần thiết vì `git diff` không hiển thị file mới.

Xác định target từ yêu cầu người dùng:

- Nếu skill được gọi trực tiếp mà không kèm nội dung, target là tất cả thay đổi hiện tại trong working tree và index.
- Nếu người dùng chỉ rõ file hoặc change, chỉ stage target đó.
- Với target mặc định, staged content có sẵn và các thay đổi độc lập đều thuộc cùng commit; không hỏi lại phạm vi.
- Với target do người dùng giới hạn, không bỏ staged content có sẵn khỏi index. Nếu staged content nằm ngoài target, dừng và yêu cầu hướng xử lý.
- Nếu không có thay đổi để commit, báo working tree sạch và không tạo empty commit.

### 2. Kiểm tra trước khi stage

Đối chiếu diff với yêu cầu và kiểm tra:

- file secret như `.env`, key, token, credential, database chứa credential hoặc log nhạy cảm;
- generated artifact, IDE file, binary lớn hoặc file ngoài repository convention;
- conflict marker, debug code hoặc thay đổi ngoài scope;
- validation evidence đã có trong session cho hành vi thay đổi.

Tái sử dụng test/evidence còn phù hợp trong cùng session. Không chạy lại chỉ để lặp evidence. Nếu code thay đổi nhưng chưa có kiểm chứng cần thiết, chạy kiểm chứng nhỏ nhất khi nó an toàn, local và nằm trong phạm vi. Nếu không thể chạy, không hỏi lại chỉ vì skill được gọi không kèm nội dung; commit và báo rõ kiểm chứng nào chưa chạy, trừ khi `AGENTS.md` hoặc hook đặt verification thành gate bắt buộc.

### 3. Stage chính xác

Dùng pathspec tường minh. Với target mặc định, lấy toàn bộ path từ `git status --short` rồi truyền từng path cho `git add --`:

```powershell
git add -- <path-1> <path-2>
```

Với partial-file commit, chỉ dùng cơ chế stage hunk không tương tác khi có thể biểu diễn chính xác và đã kiểm tra; nếu không, hỏi người dùng thay vì stage cả file.

Sau khi stage, bắt buộc chạy:

```powershell
git diff --cached --name-status
git diff --cached --check
git diff --cached
```

Dừng nếu staged diff rỗng, chứa file ngoài scope, secret khả nghi hoặc không khớp intent.

### 4. Soạn commit message

Soạn từ staged diff thực tế, không từ tên task hoặc trí nhớ chat. Ưu tiên format:

```text
<type>(<scope>): <imperative summary>
```

Dùng `feat`, `fix`, `test`, `docs`, `refactor`, `chore` hoặc type đã tồn tại trong lịch sử repository. Kiểm tra vài commit gần nhất khi cần khớp convention:

```powershell
git log -8 --pretty=format:"%h %s"
```

Giữ subject ngắn, cụ thể và không thêm claim chưa được diff/evidence chứng minh. Chỉ thêm body khi cần giải thích lý do, migration, compatibility hoặc validation quan trọng. Nếu người dùng cung cấp message, dùng message đó khi nó phản ánh đúng staged diff; nếu sai lệch, báo và đề xuất sửa.

### 5. Commit local

Tạo commit không kèm thao tác remote:

```powershell
git commit -m "<message>"
```

Không dùng `--no-verify`. Nếu hook fail, giữ nguyên staged state, báo lỗi gốc và không che lỗi hoặc tự commit lại bằng cách bỏ qua hook.

### 6. Xác minh và bàn giao

Chạy:

```powershell
git show --stat --oneline --summary HEAD
git status --short
```

Báo ngắn gọn:

- commit SHA và subject;
- file/change đã commit;
- test hoặc validation đã chạy và kết quả;
- thay đổi còn lại trong working tree;
- xác nhận chưa push.

Không tuyên bố working tree sạch nếu `git status --short` còn output.

## Tình huống phải dừng

Dừng trước commit và hỏi người dùng khi:

- staged content có sẵn không thuộc target do người dùng giới hạn;
- phát hiện secret hoặc file nhạy cảm;
- commit hook fail;
- HEAD/branch thay đổi bất ngờ trong lúc chuẩn bị;
- yêu cầu commit kéo theo amend, rebase hoặc thao tác remote chưa được cho phép.
