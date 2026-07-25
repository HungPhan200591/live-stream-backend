# Hướng dẫn học và implement cùng AI Agent

> Dành cho: người học sử dụng `live-stream-backend` để luyện Senior Java/Spring Boot<br>
> Cập nhật: 2026-07-25

Đây là file duy nhất bạn cần nhớ để sử dụng learning system. Không đọc toàn bộ `docs/` và không tự chọn hàng chục tài liệu trước mỗi buổi.

## 1. Chỉ cần nhớ 2 file và 1 skill

1. `docs/learning/guide.md`: cách làm việc — chính là file này.
2. [`docs/learning/index.md`](index.md): đang học case nào, checkpoint nào và bước tiếp theo là gì.
3. `$run-senior-java-learning`: AI Agent điều phối theory, question, case, implementation, evidence và teach-back.

Roadmap chỉ dùng để kiểm soát độ phủ dài hạn. Theory, question bank, case, experiment và code chỉ được mở khi checkpoint hiện tại cần chúng.

## 2. Prompt bắt đầu chuẩn

Mỗi khi mở session mới, copy nguyên prompt này:

```text
Dùng $run-senior-java-learning.

Đọc AGENTS.md, docs/learning/guide.md và docs/learning/index.md.
Khôi phục Current session cursor, kiểm tra active case và evidence hiện có.

Trước khi làm, tóm tắt ngắn:
1. Tôi đang học case nào?
2. Current checkpoint là gì?
3. Mục tiêu duy nhất của session này là gì?
4. Artifact nào sẽ được đọc hoặc cập nhật?
5. Exit gate để đi tiếp là gì?

Sau đó thực hiện đúng checkpoint hiện tại.
Chỉ đọc tài liệu/code/test được checkpoint hoặc active case link tới.
Không mở case mới, không đọc toàn bộ docs và không tự nhảy sang implementation.
```

Bạn không cần kể lại lịch sử chat. `Current session cursor` trong learning index phải đủ để Agent khôi phục context.

## 3. Một session diễn ra như thế nào?

| Bước | AI Agent làm | Bạn làm |
| --- | --- | --- |
| 1. Khôi phục | Đọc cursor, active case và evidence | Kiểm tra mục tiêu session có đúng nhu cầu không |
| 2. Học/làm | Giải thích, hỏi, tạo artifact hoặc implement đúng checkpoint | Đọc, trả lời bằng lời của mình, quyết định trade-off |
| 3. Kiểm chứng | Chạy test/experiment/review phù hợp | Không chấp nhận claim thiếu evidence |
| 4. Kết thúc | Cập nhật artifact, case và cursor | Teach-back ngắn và xác nhận gap còn lại |

Mỗi session chỉ nên có **một mục tiêu chính**. Không cố hoàn thành toàn bộ case trong một lượt.

## 4. Khi nào học, khi nào code?

Learning flow chuẩn:

`THEORY_CORE -> THEORY_DEEP_DIVE -> QUESTION_BANK -> LEARNING_CASE -> REPRODUCER -> DESIGN_TRADE_OFF -> IMPLEMENTATION -> EXPERIMENT_EVIDENCE -> REVIEW -> INTERVIEW_NOTE_TEACH_BACK -> CLOSED`

- Ở theory/question checkpoint: đọc, tự giải thích, trả lời câu hỏi; chưa sửa code.
- Ở learning case: nối kiến thức với failure thật của project và khóa scope.
- Ở reproducer: phải tạo actual failing result trước khi sửa.
- Chỉ code khi cursor ở `IMPLEMENTATION` và các gate trước đã đạt.
- Sau code phải có experiment/evidence, review và teach-back; test xanh chưa đồng nghĩa đã hiểu hoặc case đã đóng.

Nếu bạn yêu cầu implement quá sớm, Agent phải chỉ ra gate còn thiếu và tiếp tục checkpoint gần nhất, không âm thầm viết code.

## 5. Prompt theo nhu cầu

### Học tiếp checkpoint hiện tại

```text
Dùng $run-senior-java-learning để tiếp tục checkpoint hiện tại.
Hãy dạy theo mental model -> mechanism -> invariant -> failure -> trade-off.
Sau mỗi phần, hỏi tôi tự giải thích lại; đừng tự trả lời thay toàn bộ.
```

### Đào sâu câu hỏi Senior/Architect/Expert

```text
Dùng $run-senior-java-learning cho active topic.
Lấy câu hỏi từ question bank theo thứ tự SENIOR -> ARCHITECT -> EXPERT.
Hỏi từng câu, chờ tôi trả lời, rồi đánh giá mental model, missing boundary,
trade-off, red flags và evidence còn thiếu.
```

### Implement learning case

```text
Dùng $run-senior-java-learning để kiểm tra implementation gate của active case.
Nếu checkpoint chưa phải IMPLEMENTATION, không sửa code; tiếp tục gate gần nhất.
Nếu gate đã mở, dùng $implement-livestream-feature để implement vertical slice nhỏ nhất,
thêm test/evidence cần thiết và không mở rộng sang case khác.
```

### Luyện phỏng vấn sau implementation

```text
Dùng $run-senior-java-learning để tiếp tục tới INTERVIEW_NOTE_TEACH_BACK.
Đóng vai interviewer Senior/Architect, hỏi từng câu và phản biện trade-off.
Buộc câu trả lời của tôi link tới test, experiment hoặc review evidence của project.
Cuối cùng giúp tôi cô đọng bản 2 phút và outline 15 phút.
```

### Kết thúc buổi học

```text
Dùng $run-senior-java-learning để kết thúc session.
Cập nhật artifact/evidence đã làm và Current session cursor trong docs/learning/index.md.
Không tăng checkpoint nếu exit gate chưa đạt.
Trả lại cho tôi: đã làm gì, tôi còn hiểu chưa chắc chỗ nào,
next action nhỏ nhất và prompt để bắt đầu session sau.
```

### Mất context hoặc không biết làm gì

```text
Tôi bị mất context. Dùng $run-senior-java-learning và không dựa vào lịch sử chat.
Đọc AGENTS.md, docs/learning/guide.md, docs/learning/index.md và active case.
Xác minh evidence, khôi phục checkpoint rồi thực hiện next action nhỏ nhất.
```

## 6. Bản đồ tài liệu tối giản

| Khi cần biết | Source of truth |
| --- | --- |
| Cách sử dụng hệ học | File guide này |
| Đang học tới đâu | [`learning/index.md`](index.md) |
| Phạm vi Senior Java cần cover | [`001_SENIOR_JAVA_INTERVIEW_ROADMAP.md`](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md) |
| Kiến thức nền dùng lại | `learning/theory/core/...` |
| Internals/failure/scale | `learning/theory/deep-dives/...` |
| Câu hỏi và evaluation rubric | `learning/question-bank/...` |
| Failure cụ thể của project | `learning/cases/...` |
| Số đo và raw result | `learning/experiments/...` |
| Câu trả lời phỏng vấn cá nhân | `learning/interview-notes/...` |

Bạn không cần tự mở các path chưa tồn tại. Agent chỉ tạo artifact khi checkpoint thật sự cần.

## 7. Trách nhiệm của bạn và AI Agent

AI Agent chịu trách nhiệm:

- route đúng tài liệu và giữ scope;
- giải thích, tạo draft, implement, test và thu evidence;
- không báo hoàn thành cho việc chưa chạy;
- cập nhật cursor để session sau tiếp tục được.

Bạn chịu trách nhiệm:

- tự diễn giải mental model và trả lời câu hỏi;
- chọn/bảo vệ trade-off sau khi hiểu alternatives;
- không dùng output AI thay cho teach-back;
- nói rõ khi muốn dừng, đào sâu hoặc chuyển mục tiêu.

Nếu Agent đọc quá nhiều tài liệu, mở case khác, code trước reproducer hoặc tự trả lời phần teach-back của bạn, hãy yêu cầu quay lại `Current session cursor`.

## 8. Definition of a good session

Một buổi học tốt kết thúc với:

1. Một artifact hoặc một phần artifact có chất lượng hơn trước.
2. Một điều bạn tự giải thích được bằng lời của mình.
3. Evidence hoặc gap được ghi rõ, không chỉ là cảm giác “đã hiểu”.
4. Cursor có next action đủ nhỏ để session sau bắt đầu ngay.

Nếu bốn điều này chưa có, giữ nguyên checkpoint.
