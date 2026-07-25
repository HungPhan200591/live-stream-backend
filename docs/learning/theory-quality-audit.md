# Theory và Deep-dive Quality Audit

> Status: `ACTIVE QUALITY BACKLOG`<br>
> Scope: `docs/learning/topics/*/theory/{core,deep-dives}`<br>
> Updated: `2026-07-26`

## 1. Vì sao audit này tồn tại?

Các preview pack trước đây tối ưu coverage và số lượng artifact nhưng chưa đạt mục tiêu quan trọng nhất: một developer chưa biết topic phải có thể đọc, hiểu bản chất, tự cô đọng, áp dụng và dùng kiến thức để trả lời phỏng vấn. Nhiều file giống cheat sheet cho người đã biết hơn là bài giảng.

Audit này ghi lại baseline và quality gate, không phải learning roadmap thứ hai. Roadmap vẫn quyết định thứ tự học. Migration toàn bộ preview hiện đã hoàn tất theo yêu cầu của learner; readiness chỉ nói tài liệu đủ để bắt đầu tự học, không nói learner đã học hoặc có runtime evidence.

## 2. Baseline phát hiện

Tại thời điểm audit:

- Có `36` core/deep-dive files.
- Có `0` Mermaid diagrams và rất ít worked code examples.
- Độ dài trung bình khoảng `920` từ/file; Batch 2 chủ yếu khoảng `540–750` từ/file.
- Nhiều file mở đầu bằng `LEARNER TODO` trước khi cung cấp mental model đầy đủ.
- Tables/matrices chứa keyword nhưng thiếu prose giải thích causal chain.
- Deep-dive thường chỉ dài tương đương một outline và chưa có internals walkthrough/diagnostic procedure.

Số từ không tự quyết định chất lượng; các con số chỉ xác nhận pattern mà learner đã phản ánh khi đọc.

## 3. Readiness sau migration

| Domain | `TEACHABLE_DRAFT` | `OUTLINE_ONLY` | Ghi chú |
| --- | ---: | ---: | --- |
| Java | 18 | 0 | Toàn bộ core/deep-dive hiện có đã đạt teaching draft |
| Spring | 10 | 0 | Toàn bộ core/deep-dive hiện có đã đạt teaching draft |
| HTTP/API | 4 | 0 | Core/deep-dive có mental model, example và guided self-check |
| Resilience | 4 | 0 | Core/deep-dive có causal loop, policy integration và guided self-check |
| Testing | 2 | 0 | TEST-01 preview pair đạt teaching gate; harness/test evidence chưa chạy |
| **Tổng** | **38** | **0** | Không file nào được coi là learner/evidence completed |

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

## 5. Kết quả migration và nguyên tắc tiếp theo

Migration đã nâng `32` file còn lại, ngoài `4` file pilot ban đầu, theo cùng teaching contract:

- mental model do người dạy cung cấp nằm trước learner task;
- mechanism có sequence, worked example/counterexample và causal explanation;
- Mermaid chỉ dùng khi quan hệ khó hiểu bằng prose; toàn corpus hiện có `48` diagrams;
- interview outline/recap và learner write-back tách riêng;
- guided self-check có section đọc lại và answer rubric;
- mọi status vẫn `DRAFT`, evidence vẫn `NOT RUN`.

Sau migration, cặp theory `TEST-01` được tạo theo đúng roadmap successor và đạt `TEACHABLE_DRAFT` ngay từ đầu. Không bulk-generate topic mới: mỗi topic tiếp theo phải có owner/checkpoint rõ hoặc trung thực gắn `OUTLINE_ONLY`; learning cursor vẫn mở từng artifact theo roadmap, không yêu cầu learner đọc cả corpus.

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
