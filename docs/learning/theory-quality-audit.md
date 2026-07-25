# Theory và Deep-dive Quality Audit

> Status: `ACTIVE QUALITY BACKLOG`<br>
> Scope: `docs/learning/topics/*/theory/{core,deep-dives}`<br>
> Updated: `2026-07-26`

## 1. Vì sao audit này tồn tại?

Các preview pack trước đây tối ưu coverage và số lượng artifact nhưng chưa đạt mục tiêu quan trọng nhất: một developer chưa biết topic phải có thể đọc, hiểu bản chất, tự cô đọng, áp dụng và dùng kiến thức để trả lời phỏng vấn. Nhiều file giống cheat sheet cho người đã biết hơn là bài giảng.

Audit này là migration backlog, không phải learning roadmap thứ hai. Roadmap vẫn quyết định thứ tự học; khi topic sắp active, Agent phải nâng file `OUTLINE_ONLY` thành `TEACHABLE_DRAFT` trước khi yêu cầu learner đọc/trả lời.

## 2. Baseline phát hiện

Tại thời điểm audit:

- Có `36` core/deep-dive files.
- Có `0` Mermaid diagrams và rất ít worked code examples.
- Độ dài trung bình khoảng `920` từ/file; Batch 2 chủ yếu khoảng `540–750` từ/file.
- Nhiều file mở đầu bằng `LEARNER TODO` trước khi cung cấp mental model đầy đủ.
- Tables/matrices chứa keyword nhưng thiếu prose giải thích causal chain.
- Deep-dive thường chỉ dài tương đương một outline và chưa có internals walkthrough/diagnostic procedure.

Số từ không tự quyết định chất lượng; các con số chỉ xác nhận pattern mà learner đã phản ánh khi đọc.

## 3. Readiness sau audit

| Domain | `TEACHABLE_DRAFT` | `OUTLINE_ONLY` | Ghi chú |
| --- | ---: | ---: | --- |
| Java | 2 | 16 | Java 21 core và Virtual Threads deep-dive đã migrate |
| Spring | 2 | 8 | IoC core và bean lifecycle deep-dive đã migrate |
| HTTP/API | 0 | 4 | Chưa dùng để tự học |
| Resilience | 0 | 4 | Chưa dùng để tự học |
| **Tổng** | **4** | **32** | Không file nào được coi là learner/evidence completed |

Question banks vẫn giữ nguyên coverage/rubric. Readiness của theory không làm câu hỏi thành `ANSWERED` và không tăng checkpoint.

## 4. Definition of `TEACHABLE_DRAFT`

Một file chỉ được nâng readiness khi đạt [theory note template](../templates/theory-note-template.md), tối thiểu:

1. Giải thích problem và intuition cho người chưa biết.
2. Định nghĩa term trước khi dùng dày đặc.
3. Agent cung cấp mental model; learner write-back nằm ở cuối.
4. Mechanism có sequence và diagram nếu quan hệ khó hiểu bằng prose.
5. Core có worked example nhỏ, ví dụ thực tế và phản ví dụ.
6. Failure được kể theo `trigger -> mechanism -> symptom -> evidence -> mitigation`.
7. Deep-dive có internals, ít nhất hai pathological cases, version/cross-layer boundary và diagnostic/experiment walkthrough.
8. Có phần cô đọng và interview outline.
9. Self-check chỉ rõ section đọc lại và rubric câu trả lời tốt.
10. Status/evidence vẫn trung thực; không claim test hoặc depth chưa đạt.

## 5. Migration order

Không migrate hàng loạt chỉ để đổi label. Thứ tự:

1. Topic/case đang active.
2. Topic prerequisite ngay kế tiếp trong roadmap.
3. Câu hỏi `HIGH` foundation/senior của topic đó.
4. Deep-dive cần cho failure/case sắp thực hành.
5. Preview còn lại giữ `OUTLINE_ONLY` cho tới lượt.

Mỗi migration session nên hoàn thiện một core/deep-dive pair, learner đọc thử và phản hồi câu tối nghĩa trước khi áp format sang topic khác. Không tạo topic mới trong khi topic active còn `OUTLINE_ONLY`.

## 6. Validation cho Agent

Trước khi bàn giao một migration:

- readiness metadata đúng;
- Mermaid qua `$mermaid-styling` readability gate;
- relative links và anchors tồn tại;
- không có `LEARNER TODO` trước phần learner write-back/self-check;
- tables có prose giải thích;
- official/version-specific references đúng baseline;
- cursor vẫn ở earliest incomplete checkpoint;
- `git diff --check` sạch và không có evidence giả.
