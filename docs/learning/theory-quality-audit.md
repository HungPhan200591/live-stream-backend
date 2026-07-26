# Theory và Deep-dive Quality Audit

> Status: `RE-AUDIT IN PROGRESS`; learner feedback: prose còn tối nghĩa, nhiều đoạn quá nặng English term và một số deep-dive chưa đủ sức tự học<br>
> Scope: `docs/learning/topics/*/theory/{core,deep-dives}`<br>
> Updated: `2026-07-26`

## 1. Vì sao audit này tồn tại?

Các preview pack trước đây tối ưu coverage và số lượng artifact nhưng chưa đạt mục tiêu quan trọng nhất: một developer chưa biết topic phải có thể đọc, hiểu bản chất, tự cô đọng, áp dụng và dùng kiến thức để trả lời phỏng vấn. Nhiều file giống cheat sheet cho người đã biết hơn là bài giảng.

Audit này ghi lại baseline và quality gate, không phải learning roadmap thứ hai. Roadmap vẫn quyết định thứ tự học. Đợt migration trước đã đạt các gate cấu trúc và liên kết, nhưng phản hồi đọc thực tế của learner cho thấy hai gate quan trọng chưa được chứng minh đủ chắc: **tiếng Việt tự nhiên** và **độ sâu giải thích**. Vì vậy claim `CORPUS AUDIT PASSED` được rút lại trong lúc rà soát; số file, số link hay word count không được dùng để phủ nhận trải nghiệm đọc này.

## 2. Baseline phát hiện

Tại thời điểm audit:

- Có `36` core/deep-dive files.
- Có `0` Mermaid diagrams và rất ít worked code examples.
- Độ dài trung bình khoảng `920` từ/file; Batch 2 chủ yếu khoảng `540–750` từ/file.
- Nhiều file mở đầu bằng `LEARNER TODO` trước khi cung cấp mental model đầy đủ.
- Tables/matrices chứa keyword nhưng thiếu prose giải thích causal chain.
- Deep-dive thường chỉ dài tương đương một outline và chưa có internals walkthrough/diagnostic procedure.

Số từ không tự quyết định chất lượng; các con số chỉ xác nhận pattern mà learner đã phản ánh khi đọc.

### 2.1. Baseline của lượt tái kiểm định ngày 2026-07-26

Phản hồi đọc thực tế đã phát hiện một lỗi mà audit cấu trúc trước đó bỏ lọt:

- `751` dòng prose trong theory là ứng viên **viết hoàn toàn bằng tiếng Anh** sau khi đã loại code block, heading, metadata, link/reference và dòng table;
- deep-dive ngắn nhất chỉ khoảng `992` từ, trung bình khoảng `1.500` từ, trong khi nhiều file dưới ngưỡng không phải do topic hẹp mà do causal chain bị nén thành câu điện tín hoặc danh sách keyword;
- ví dụ rõ nhất là các đoạn kiểu `First inspect...`, `Contain lower concurrency...`, `Common: ...` và rubric/self-check bằng English noun phrase: người đọc biết tên signal nhưng không được dạy quan hệ nguyên nhân.

Các con số trên là detector để tìm ứng viên cần đọc lại, không phải bộ chấm tiếng Việt tuyệt đối. Exit gate của lượt này là sửa từng đoạn theo ngữ cảnh và audit chéo bằng mắt, sau đó mới chạy lại detector để tìm residual issue.

## 3. Readiness sau migration

| Domain | `TEACHABLE_DRAFT` | `OUTLINE_ONLY` | Ghi chú |
| --- | ---: | ---: | --- |
| Java | 21 | 0 | `10` core + `11` deep-dive, gồm Java 21 và JDK 25 decision knowledge; nối `166/166` câu |
| Spring | 10 | 0 | `5` core + `5` deep-dive, nối `52/52` câu |
| HTTP/API | 4 | 0 | Core/deep-dive có mental model, example, guided self-check và nối `20/20` câu |
| Resilience | 6 | 0 | `3` core + `3` deep-dive có timeout/retry/circuit/overload causal loops và nối `30/30` câu |
| Testing | 4 | 0 | Hermetic/Testcontainers và strategy/contract/concurrency/load/mutation đủ `2` pairs; test evidence chưa chạy |
| Database | 20 | 0 | PostgreSQL/JPA/SQL đủ `10` core + `10` deep-dive, nối `106/106` câu qua `10` slices; chưa active case/evidence |
| Redis | 4 | 0 | Cache consistency và atomic data-structure đủ `2` core + `2` deep-dive, nối `20/20` câu; RED-01 chưa active |
| Security | 14 | 0 | `7` core + `7` deep-dive nối đủ `7/7` security question-bank slices; learner/case evidence chưa chạy |
| Messaging | 6 | 0 | RabbitMQ, Kafka và reliable event workflow đủ `3` core + `3` deep-dive, nối `30/30` câu; Kafka vẫn là comparison, không phải dependency project |
| Architecture | 10 | 0 | Distributed consistency, DDD/module, microservice extraction, capacity/multi-region và storage selection đủ `5` core + `5` deep-dive, nối `50/50` câu |
| Leadership | 2 | 0 | Review, ADR, incident, mentoring, facilitation và systemic learning đủ core/deep, nối `10/10` câu; behavioral evidence chưa được tạo giả |
| Observability/Realtime/Reactive | 6 | 0 | Mỗi lane đủ core/deep và nối tổng `30/30` câu; runtime/load/fault evidence `NOT RUN` |
| Operations/Cloud/Platform | 8 | 0 | Runtime, delivery, Kubernetes/IaC và platform options đủ `4` core + `4` deep-dive, nối `40/40` câu; không tự thêm stack P3 |
| **Tổng** | **115** | **0** | `58` question-bank slices/`671` câu đã nối core + deep-dive; không file nào được coi là learner/evidence completed |

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

Migration bắt đầu từ `4` pilot, sau đó mở rộng và audit lại toàn bộ `115` core/deep-dive files theo cùng teaching contract:

- mental model do người dạy cung cấp nằm trước learner task;
- mechanism có sequence, worked example/counterexample và causal explanation;
- Mermaid chỉ dùng khi quan hệ khó hiểu bằng prose; toàn corpus hiện có `125` diagrams;
- interview outline/recap và learner write-back tách riêng;
- guided self-check có section đọc lại và answer rubric;
- mọi status vẫn `DRAFT`, evidence vẫn `NOT RUN`.

Sau migration, các domain hiện hữu gồm Java, Spring, HTTP/API, Testing, Database, Redis, Security, Messaging, Architecture, Leadership, Observability/Realtime/Reactive/Operations/Cloud/Platform và Resilience đã có hai tầng knowledge, đạt `TEACHABLE_DRAFT` và nối question bank tương ứng. Các domain này chưa có learner/case evidence. Việc chuẩn bị trước không thay execution order hay `JDK-01/THEORY_CORE` cursor.

### 5.1. Quality audit cuối corpus

Audit ngày `2026-07-26` không chỉ đếm file. Kết quả kiểm tra lại:

- `115/115` files có `Teaching readiness: TEACHABLE_DRAFT`, `Status: DRAFT` và `Evidence status: NOT RUN`;
- `58` question-bank slices chứa `671` câu; `671/671` question sections có direct link tới một core theory và một deep-dive;
- `115/115` theory files có ít nhất một incoming reference từ question bank; không có knowledge artifact mồ côi;
- không còn `Theory/Deep-dive NOT CREATED` trong question-bank corpus;
- `185` Markdown files trong learning/routing scope chứa `2.074` local links không có relative target hỏng; `229` links có anchor đều resolve tới heading;
- `125/125` Mermaid blocks dùng layout `TB/TD`; mọi node/subgraph có explicit high-contrast style theo `$mermaid-styling`;
- `LEARNER TODO` chỉ nằm ở learner write-back/self-check sau teaching content;
- `57/57` core files có ít nhất hai worked examples/scenarios và một phản ví dụ/misconception được giải thích; `115/115` theory files có ít nhất ba guided self-check questions kèm section đọc lại, rubric và learner answer;
- hai core mới yếu nhất (`JDK-02` migration strategy và testing strategy) đã được mở rộng thêm compatibility/evidence walkthrough thay vì chỉ đạt gate bằng nhãn;
- mười database deep-dives ngắn nhất đã được bổ sung ít nhất hai pathology walkthrough, diagnostic/experiment, cross-layer/version boundary, trade-off và guided self-check; các deep-dive ngắn ở Resilience, Redis, Testing, Java, Spring và HTTP/API cũng được audit/nâng tương tự.

Word count chỉ là heuristic. Sau audit, core trung bình khoảng `1.601` từ và deep-dive khoảng `1.500` từ; một số topic hẹp ngắn hơn template range nhưng vẫn phải qua content gate ở mục 4, không được padding để đủ số. Readiness này chỉ nói corpus đủ để tự học và learner review; không chứng minh người học đã đạt depth hay system đã pass runtime experiment.

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
