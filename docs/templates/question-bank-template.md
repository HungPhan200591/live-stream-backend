# <Domain> Interview Question Bank

> Status: `DRAFT | ACTIVE | REVIEWED`<br>
> Domain owner: `<learning domain>`<br>
> Active slice: `<CASE-ID hoặc GENERAL>`<br>
> Related theory: `<links>`<br>
> Updated: `YYYY-MM-DD`

Question bank chứa câu hỏi và evaluation rubric, không chứa bài luận hoàn chỉnh. Người học trả lời trước khi mở `Answer outline`. Link câu trả lời nền tảng tới theory và câu trả lời dựa trên trải nghiệm tới interview note; không dùng `EVIDENCE_BACKED` khi test/experiment/review chưa tồn tại.

Toàn bộ câu hỏi, intent, dàn ý, trade-off, follow-up và red flag phải viết bằng tiếng Việt tự nhiên cho developer Việt. Giữ nguyên tên API/type/metric và IT English term cần thiết, nhưng giải nghĩa term chuyên biệt tại lần đầu; không dùng English noun phrase như `Interviewer evaluates: recovery objectives and spare capacity` thay cho một câu giải thích. Các field label canonical như `Answer outline`, `Evidence`, `Self-assessment` có thể giữ để Agent parse ổn định, nhưng nội dung bên dưới không được là prose thuần tiếng Anh.

`Interview likelihood` là heuristic về khả năng gặp khi interviewer hỏi đúng topic/role, không phải số liệu thị trường. Ưu tiên câu phổ biến trước; câu riêng của project và câu pathological dùng để đào sâu sau.

## Level rubric

| Level | Trọng tâm |
| --- | --- |
| `FOUNDATION` | Khái niệm, vocabulary và mechanism cơ bản |
| `SENIOR` | Failure mode, debugging, test, transaction boundary và trade-off |
| `ARCHITECT` | Scale, HA, consistency, migration, ownership, security, cost và vận hành |
| `EXPERT` | Pathological case, cross-layer interaction, formal invariant, failure injection và giới hạn solution |

Level được gắn trên từng câu hỏi; không tách file/folder theo level.

## Likelihood rubric

| Likelihood | Cách dùng |
| --- | --- |
| `HIGH` | Câu core hoặc scenario rất thường dùng để kiểm tra topic; luyện ở first pass |
| `MEDIUM` | Follow-up Senior hoặc biến thể phụ thuộc hệ thống; luyện sau nhóm `HIGH` |
| `LOW` | Architect/Expert discriminator, pathological hoặc project-specific hẹp; dùng làm stretch |

## Recommended practice order

1. `HIGH FOUNDATION`: `<IDs>`
2. `HIGH/MEDIUM SENIOR`: `<IDs>`
3. Project application: `<IDs>`
4. Architect/Expert stretch: `<IDs>`

## Coverage

| Topic | Foundation | Senior | Architect | Expert | Theory source |
| --- | ---: | ---: | ---: | ---: | --- |
| | 0 | 0 | 0 | 0 | |

## Questions

### <PREFIX>-<TOPIC>-001 — `FOUNDATION | SENIOR | ARCHITECT | EXPERT`

**Question:**

`<Câu hỏi>`

**Target depth:** `D1 | D1-D2 | D2-D3 | D3-D4 | D4`

**Interview likelihood:** `HIGH | MEDIUM | LOW` — `<lý do ngắn; không tuyên bố tần suất giả>`

**Question type:** `COMMON_CORE | COMMON_SCENARIO | PROJECT_APPLICATION | ARCHITECT_STRETCH`

**Interviewer evaluates:**

- `<Mental model/invariant/failure/trade-off cần quan sát>`

**Answer outline:**

1. `<Các ý bắt buộc; không viết full essay>`
2. `<Boundary hoặc example>`

**Required trade-offs:**

- `<Alternative và consequence phải đề cập>`

**Follow-up ladder:**

- Foundation:
- Senior:
- Architect:
- Expert:

**Red flags:**

- `<Câu trả lời sai hoặc dấu hiệu học thuộc>`

**Evidence:**

- Theory:
- Deep-dive:
- Learning case:
- Tests/experiment:
- Interview note:

**Self-assessment:** `UNANSWERED | NEEDS_WORK | EVIDENCE_BACKED`
