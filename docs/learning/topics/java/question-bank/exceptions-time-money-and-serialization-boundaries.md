# Java Interview Question Bank — Exceptions, Time, Money and Serialization Boundaries

> Status: `DRAFT`<br>
> Domain owner: `Java language / domain boundaries / compatibility`<br>
> Active slice: `NONE`; preview target `JAVA-01 — exception, time, money and serialization contracts`<br>
> Runtime baseline: `Java 21`; later-LTS migration policy belongs to `JDK-02`<br>
> Related roadmap: [Stage 1](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-1---java-core-state-và-concurrency)<br>
> Related depth rubric: [Java language, collections, algorithm and complexity](../../../knowledge-depth-rubric.md#31-java-language-collections-algorithm-và-complexity--p0-target-d3)<br>
> Related theory: [Exceptions, Time, Money and Serialization Boundaries](../theory/core/exceptions-time-money-and-serialization-boundaries.md), [compatibility deep-dive](../theory/deep-dives/time-money-and-serialization-compatibility.md) — `DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview này không implement `JAVA-01`, không active case và không tạo evidence. `Interview likelihood` là heuristic trong phạm vi Senior Java backend, không phải tỷ lệ thị trường đã đo. Mọi câu giữ `UNANSWERED`, test `NOT RUN`.

## Scope boundary

File này sở hữu Java exception/resource contract, `java.time`/Clock/locale, `BigDecimal`/Money và serialization compatibility. Optional nằm ở Stream bank; HTTP error contract thuộc `API-01`, transaction rollback thuộc `TX-01`, database timestamp/schema migration thuộc database stages và unsafe deserialization threat model thuộc security stage.

## Project anchor

Current code gọi `LocalDateTime.now()` trực tiếp ở session/stream/cache paths, có `catch (Exception)` trong request/security flow, dùng `BigDecimal` cho wallet DTO và versioned Jackson metadata trong `SessionCacheDTO`. Đây là các điểm để tạo deterministic tests và compatibility evidence khi `JAVA-01` active.

## Coverage

| Topic | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Exception and resource contracts | 4 | 4 | 0 | 0 |
| Time, clock and locale | 3 | 4 | 1 | 1 |
| Money and serialization | 3 | 3 | 1 | 0 |
| **Tổng** | **10** | **11** | **2** | **1** |

## Recommended practice order

1. First pass — câu phổ biến: `JAVA-BOUNDARY-001` đến `JAVA-BOUNDARY-009`, `JAVA-BOUNDARY-011`, `JAVA-BOUNDARY-012`, `JAVA-BOUNDARY-014`, `JAVA-BOUNDARY-015`, `JAVA-BOUNDARY-017`, `JAVA-BOUNDARY-019`, `JAVA-BOUNDARY-020`.
2. Senior/Architect follow-up xác suất vừa: `JAVA-BOUNDARY-010`, `JAVA-BOUNDARY-013`, `JAVA-BOUNDARY-016`, `JAVA-BOUNDARY-018`, `JAVA-BOUNDARY-021`, `JAVA-BOUNDARY-022`.
3. Project application: `JAVA-BOUNDARY-012`, `JAVA-BOUNDARY-015`, `JAVA-BOUNDARY-017`, `JAVA-BOUNDARY-019`, `JAVA-BOUNDARY-020`.
4. Architect/Expert stretch: `JAVA-BOUNDARY-023`, `JAVA-BOUNDARY-024`.

## Questions

### JAVA-BOUNDARY-001 — `FOUNDATION`
**Question:** Checked và unchecked exception khác nhau thế nào? Khi nào nên dùng mỗi loại?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu exception phổ biến nhất.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Compile-time catch/declare rule và recoverability/API contract.<br>
**Answer outline:** Checked exception là `Exception` không thuộc `RuntimeException`, caller phải catch/declare; unchecked thường biểu diễn programming error, invariant hoặc failure khó phục hồi tại mỗi call site. Chọn theo caller có hành động recovery hữu ích, không theo thói quen.<br>
**Required trade-offs:** Checked làm failure explicit nhưng gây propagation/boilerplate; unchecked giữ API gọn nhưng cần documentation/central handling tốt.<br>
**Follow-up ladder:** `IOException`? Validation error? Spring rollback default? Library API?<br>
**Red flags:** Checked luôn dành cho business error hoặc unchecked không cần document.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-002 — `FOUNDATION`
**Question:** `Error`, `Exception` và `RuntimeException` khác nhau thế nào? Có nên catch `Throwable`/`Error` không?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — exception hierarchy question rất thường gặp.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** JVM/system failure boundary và catch scope.<br>
**Answer outline:** `Throwable` gồm Error và Exception; Error thường là serious runtime/JVM condition mà application không nên xử lý như business failure; RuntimeException là unchecked Exception. Catch broad chỉ ở boundary có mục tiêu cleanup/report/rethrow, không tiếp tục giả vờ healthy.<br>
**Required trade-offs:** Top-level containment có thể bảo vệ process/request boundary nhưng không được nuốt fatal/cancellation signals.<br>
**Follow-up ladder:** `OutOfMemoryError`? `StackOverflowError`? `InterruptedException`?<br>
**Red flags:** Catch `Throwable` rồi log và tiếp tục mọi operation.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-003 — `FOUNDATION`
**Question:** `throw`, `throws`, `try/catch/finally` khác nhau thế nào? `return` trong `finally` nguy hiểm ra sao?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu syntax/flow kinh điển.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Declaration vs actual throw, stack unwinding và control-flow override.<br>
**Answer outline:** `throw` phát exception instance; `throws` khai báo possible checked failures; finally thường chạy khi rời try/catch để cleanup. Return/throw trong finally có thể override result/exception gốc, làm mất causal failure nên phải tránh.<br>
**Required trade-offs:** Finally phù hợp cleanup chung, nhưng try-with-resources an toàn/rõ hơn cho closeable resources.<br>
**Follow-up ladder:** Finally không chạy khi nào? Catch order? Rethrow?<br>
**Red flags:** Finally được đảm bảo chạy trong mọi tình huống kể cả process halt/crash.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-004 — `FOUNDATION`
**Question:** Try-with-resources hoạt động với `AutoCloseable` thế nào và resources được đóng theo thứ tự nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — resource-management question phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Deterministic close, reverse order và suppressed exception concept.<br>
**Answer outline:** Resources khai báo trong try được close tự động theo thứ tự ngược; primary exception từ body được giữ, close failures thành suppressed. Resource ownership phải rõ, không close object do caller sở hữu.<br>
**Required trade-offs:** Deterministic cleanup bảo vệ connection/file; abstraction trả lazy stream có thể chuyển close responsibility cho caller.<br>
**Follow-up ladder:** `Closeable` vs `AutoCloseable`? Multiple resources? Suppressed retrieval?<br>
**Red flags:** Garbage collector sẽ đóng file/DB connection kịp thời.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-005 — `FOUNDATION`
**Question:** Vì sao ưu tiên `java.time` thay legacy `Date`, `Calendar`, `SimpleDateFormat`?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — modern Java date/time question phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Immutable types, clearer domain concepts và formatter thread safety.<br>
**Answer outline:** `java.time` tách date/time/instant/zone rõ, types immutable; `DateTimeFormatter` immutable/thread-safe. Legacy API mutable/confusing và `SimpleDateFormat` không thread-safe khi share.<br>
**Required trade-offs:** Legacy integration cần conversion boundary; không trộn types/timezones ngầm xuyên domain.<br>
**Follow-up ladder:** Convert Date↔Instant? Formatter cache? JDBC mapping?<br>
**Red flags:** `LocalDateTime` là replacement đúng cho mọi `Date` use case.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-006 — `FOUNDATION`
**Question:** Phân biệt `Instant`, `LocalDate`, `LocalDateTime`, `OffsetDateTime` và `ZonedDateTime`.<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu date/time type selection rất thường gặp.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Timeline point, civil time, offset và region zone rules.<br>
**Answer outline:** Instant là point trên UTC timeline; LocalDate/LocalDateTime không có offset/zone; OffsetDateTime gắn numeric offset; ZonedDateTime gắn ZoneId/rules. Chọn theo domain fact cần lưu, không chuyển đổi ngầm.<br>
**Required trade-offs:** Instant dễ compare/audit; local/zoned cần cho lịch business và DST semantics.<br>
**Follow-up ladder:** Birthday? Meeting? CreatedAt? Scheduled local midnight?<br>
**Red flags:** LocalDateTime luôn là UTC vì server cấu hình UTC.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-007 — `FOUNDATION`
**Question:** UTC, offset và time zone khác nhau thế nào? Vì sao offset `+07:00` không thay thế `Asia/Ho_Chi_Minh` cho lịch tương lai?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — timezone misconception phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Zone rules/DST/history khác fixed offset.<br>
**Answer outline:** UTC là reference timeline; offset là chênh lệch tại instant; ZoneId mang rule lịch sử/tương lai có thể đổi offset. Lịch theo địa phương cần zone, event đã xảy ra thường lưu instant và zone nếu cần tái hiện context.<br>
**Required trade-offs:** Chỉ lưu UTC đơn giản cho audit nhưng mất intended civil-time rule nếu schedule tương lai.<br>
**Follow-up ladder:** Client timezone? tzdb update? Serialize zone?<br>
**Red flags:** Time zone chỉ là offset có tên.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-008 — `FOUNDATION`
**Question:** Vì sao không dùng `double`/`float` cho tiền? Tạo `BigDecimal` đúng cách thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — money/precision question rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Binary floating representation và explicit decimal semantics.<br>
**Answer outline:** Nhiều decimal không biểu diễn chính xác bằng binary floating; tiền cần decimal precision/scale/rounding rõ. Dùng string constructor hoặc `BigDecimal.valueOf` theo input contract, tránh `new BigDecimal(double)` từ giá trị binary không mong muốn.<br>
**Required trade-offs:** BigDecimal chính xác/configurable nhưng chậm và verbose hơn primitive; correctness tiền ưu tiên hơn micro-speed.<br>
**Follow-up ladder:** Minor units long? Currency? RoundingMode? Database numeric?<br>
**Red flags:** Làm tròn double ở cuối là đủ cho mọi phép tính tiền.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-009 — `FOUNDATION`
**Question:** `BigDecimal.equals()` và `compareTo()` khác nhau thế nào? Scale ảnh hưởng equality/hash collection ra sao?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — BigDecimal trap thường được hỏi.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Numeric comparison vs representation-sensitive equality.<br>
**Answer outline:** `compareTo` so numeric value nên 1.0 và 1.00 bằng về ordering; `equals` còn xét scale nên khác, hash cũng khác. Value object phải canonicalize hoặc định nghĩa equality policy nhất quán trước khi dùng làm key/set.<br>
**Required trade-offs:** Canonical scale đơn giản equality nhưng phải theo currency/business rule, không `stripTrailingZeros` mù quáng ở mọi boundary.<br>
**Follow-up ladder:** TreeSet vs HashSet? Division? Rounding required exception?<br>
**Red flags:** `compareTo == 0` luôn đồng nghĩa `equals` cho BigDecimal.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-010 — `FOUNDATION`
**Question:** Serialization/deserialization là gì? Java native serialization khác JSON/Jackson như thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `MEDIUM` — concept phổ biến nhưng native serialization ít là trọng tâm backend mới.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** In-memory object graph vs explicit wire schema và trust boundary.<br>
**Answer outline:** Serialization biến state thành representation lưu/truyền; deserialization tái tạo model. Java native serialization gắn chặt class/object graph; JSON là text/schema contract qua mapper. Cả hai cần version/security policy, không deserialize input không tin cậy tùy tiện.<br>
**Required trade-offs:** Native tiện trong Java-only legacy nhưng coupling/risk cao; explicit DTO JSON interoperable nhưng cần mapping/schema evolution.<br>
**Follow-up ladder:** `Serializable` marker? Transient? Polymorphic JSON? Unknown fields?<br>
**Red flags:** JSON an toàn tự động vì đọc được bằng mắt.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-011 — `SENIOR`
**Question:** Exception translation nên diễn ra ở layer nào và làm sao giữ nguyên root cause/context mà không leak internals?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — Senior exception-design question phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Boundary ownership, cause chaining, stable error taxonomy.<br>
**Answer outline:** Translate infrastructure exception tại boundary thành domain/application exception có cause; controller/global handler map ra stable public code/status và redact internals. Log một lần ở ownership boundary với correlation/context không nhạy cảm.<br>
**Required trade-offs:** Abstraction bảo vệ API nhưng quá nhiều wrapper làm stack/cause khó đọc.<br>
**Follow-up ladder:** SQL exception? 404 vs 409? Error code? Retry metadata?<br>
**Red flags:** `throw new BusinessException(e.getMessage())` làm mất cause hoặc trả stack trace cho client.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-012 — `SENIOR`
**Question:** `catch (Exception)` hoặc catch-and-ignore trong authentication/request flow gây lỗi gì?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — production correctness/security scenario rất phổ biến.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Failure classification, security downgrade và observability.<br>
**Answer outline:** Broad catch gom programming/infrastructure/auth failures thành fallback giả, có thể biến authenticated path thành anonymous hoặc che incident. Catch đúng expected exception, giữ interrupt/cancellation, fail policy rõ và metric/log reason đã redact.<br>
**Required trade-offs:** Graceful degradation tăng availability nhưng chỉ hợp lệ khi fallback không phá security/business invariant.<br>
**Follow-up ladder:** User lookup down? Invalid token? Redis timeout? InterruptedException?<br>
**Red flags:** Catch broad là an toàn vì request không bị 500.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-013 — `SENIOR`
**Question:** Khi body và `close()` cùng throw trong try-with-resources, exception nào được propagate và bạn debug suppressed exceptions thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — resource-management deep follow-up.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Primary vs suppressed causal chain.<br>
**Answer outline:** Exception từ try body là primary; close exceptions được attach qua `getSuppressed`; nếu body không throw thì close failure có thể propagate. Logging/translation phải giữ full throwable, không chỉ message.<br>
**Required trade-offs:** Giữ causal chain tăng diagnostic value; response/log vẫn phải redact sensitive details.<br>
**Follow-up ladder:** Multiple close failures? Manual finally khác gì? Custom AutoCloseable?<br>
**Red flags:** Close exception luôn thay exception gốc hoặc hoàn toàn bị mất.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-014 — `SENIOR`
**Question:** Phân loại failure retryable và non-retryable như thế nào? Exception type có đủ quyết định retry không?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — reliability/error-contract follow-up phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Operation semantics, idempotency và retry budget.<br>
**Answer outline:** Retry phụ thuộc transient cause, operation/idempotency, deadline/budget và side effect đã commit hay chưa; validation/auth/conflict thường không tự hết. Exception taxonomy/cause/status hỗ trợ policy nhưng không thay operation context.<br>
**Required trade-offs:** Retry tăng recovery chance nhưng khuếch đại load/duplicate nếu classification sai.<br>
**Follow-up ladder:** Timeout sau commit? Backoff/jitter? Deadlock? Rate limit?<br>
**Red flags:** Retry mọi RuntimeException ba lần.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-015 — `SENIOR`
**Question:** Vì sao inject `Clock` thay vì gọi `LocalDateTime.now()`/`Instant.now()` trực tiếp trong domain logic?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — deterministic-test design question phổ biến.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Time as dependency, deterministic boundary tests và one-now-per-operation.<br>
**Answer outline:** Clock cho fixed/offset test, kiểm expiry/boundary không sleep và dùng cùng instant trong operation; production inject system clock. Chọn Instant/zone conversion theo domain, không rải now calls trong entity/service.<br>
**Required trade-offs:** Dependency thêm wiring nhưng loại flaky test và hidden temporal coupling.<br>
**Follow-up ladder:** Scheduler test? Token expiry? DB clock? Multiple services?<br>
**Red flags:** Mock static now hoặc chèn sleep để test expiry.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-016 — `SENIOR`
**Question:** DST gap/overlap ảnh hưởng lịch chạy “02:30 theo local time” ra sao, và bạn test thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — timezone failure-mode question có giá trị nhưng không ở mọi vòng.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Nonexistent/ambiguous local times và explicit resolution policy.<br>
**Answer outline:** Zone rule có thể bỏ qua hoặc lặp local time; policy phải chọn shift/skip/earlier/later offset và lưu intent+zone. Test vùng có transition bằng fixed clock/known zone rules, không dựa timezone máy.<br>
**Required trade-offs:** Lưu next instant đơn giản runtime nhưng cần recompute đúng khi rule/schedule thay đổi.<br>
**Follow-up ladder:** Monthly billing? tzdb update? Cron? Duplicate execution?<br>
**Red flags:** Cộng 24 giờ luôn bằng cùng giờ ngày mai.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-017 — `SENIOR`
**Question:** Thiết kế contract thời gian qua Java, PostgreSQL và JSON thế nào để không phụ thuộc timezone máy chạy?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — persistence/API boundary scenario phổ biến.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Domain meaning, explicit wire format và driver/database mapping evidence.<br>
**Answer outline:** Event timestamp dùng instant/offset ISO-8601 rõ; civil schedule lưu local+ZoneId nếu cần; database column/mapping được pin/test; JSON luôn có format/offset contract. Không serialize ambiguous LocalDateTime cho global event.<br>
**Required trade-offs:** Chuẩn UTC instant dễ compare; giữ zone/local intent thêm fields nhưng cần cho schedule/audit display.<br>
**Follow-up ladder:** PostgreSQL `timestamp` vs `timestamptz`? Precision? Client locale?<br>
**Red flags:** Thêm chữ `Z` vào LocalDateTime mà không convert instant.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-018 — `SENIOR`
**Question:** Locale ảnh hưởng parse/format số, tiền và ngày ra sao? Machine contract khác display contract thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — internationalization boundary follow-up.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Locale-neutral wire format và explicit presentation locale.<br>
**Answer outline:** Dấu thập phân/grouping, tên tháng/currency format thay theo locale; machine API dùng canonical locale-neutral schema, UI format bằng explicit locale/currency/zone. Không dùng default locale của server để parse request.<br>
**Required trade-offs:** Localized input thân thiện nhưng ambiguous; canonical input dễ validate/interoperate.<br>
**Follow-up ladder:** Turkish case mapping? Currency fraction digits? Decimal comma?<br>
**Red flags:** UTF-8 giải quyết luôn locale/timezone semantics.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-019 — `SENIOR`
**Question:** Thiết kế Money value object và rounding boundary cho deposit/gift/ledger thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — money-domain scenario phổ biến ở Senior backend.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Amount+currency invariant, canonical scale và no hidden rounding.<br>
**Answer outline:** Immutable amount/currency; validate sign/range/currency; normalize theo currency/business rule; arithmetic explicit MathContext/RoundingMode khi division/conversion; round tại defined boundary, ledger lưu exact posted amounts.<br>
**Required trade-offs:** Minor-unit integer đơn giản cho fixed-scale currency; BigDecimal linh hoạt cho multi-scale nhưng policy phức tạp hơn.<br>
**Follow-up ladder:** JPY/KWD? FX rate? Allocation remainder? Database constraint?<br>
**Red flags:** Một global scale=2 và HALF_UP cho mọi currency/use case.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-020 — `SENIOR`
**Question:** Thêm, xóa, rename hoặc đổi type JSON field ảnh hưởng backward/forward compatibility thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — API/event/cache evolution question phổ biến.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Tolerant reader, default semantics và rolling-version coexistence.<br>
**Answer outline:** Add optional field thường backward-safe nếu old reader ignore unknown và new reader có default; remove/rename/type/meaning change thường breaking. Version schema/type alias, test old↔new payload và dùng expand-contract.<br>
**Required trade-offs:** Ignore unknown tăng forward tolerance nhưng có thể che typo/security field; strict mode tốt cho internal validation nhưng cần rollout coordination.<br>
**Follow-up ladder:** Enum value mới? Required field? Cache TTL? Polymorphic type ID?<br>
**Red flags:** JSON schemaless nên mọi thay đổi compatible.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-021 — `SENIOR`
**Question:** `serialVersionUID`, default Java serialization và native deserialization có rủi ro gì trong backend hiện đại?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — classic Java question nhưng ít ưu tiên hơn JSON/event schema.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Class-version compatibility, gadget/security risk và long-lived storage coupling.<br>
**Answer outline:** UID kiểm compatibility danh nghĩa nhưng không bảo đảm semantic compatibility; default field graph gắn implementation; untrusted native deserialization có gadget/code-execution risk. Ưu tiên explicit allowlisted DTO/schema và tránh native serialized blob lâu dài.<br>
**Required trade-offs:** Native serialization tiện trong trusted/short-lived legacy use nhưng migration/interoperability/security kém.<br>
**Follow-up ladder:** `transient`? Custom readObject? ObjectInputFilter? Session storage?<br>
**Red flags:** Chỉ thêm fixed `serialVersionUID` là giải quyết version/security.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-022 — `ARCHITECT`
**Question:** Chuẩn hóa time và money contract giữa nhiều service/region thế nào để tránh drift và double rounding?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `MEDIUM` — distributed-domain architecture follow-up.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Canonical representation, ownership và invariant across boundaries.<br>
**Answer outline:** Pin currency/scale/rounding owner, transmit decimal string/minor units with currency; timestamp dùng instant+precision, schedule dùng local+zone; schema/version/property tests và reconciliation. Không để mỗi service dùng default locale/rounding/timezone.<br>
**Required trade-offs:** Shared contract/schema giảm drift nhưng shared code library có thể coupling rollout; conformance tests linh hoạt hơn.<br>
**Follow-up ladder:** Region clock skew? FX version? Event replay? Database precision?<br>
**Red flags:** Đồng bộ timezone server và một utility jar là đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-023 — `ARCHITECT`
**Question:** Thiết kế rolling migration cho versioned JSON cache/event khi producer và consumer cũ/mới cùng tồn tại.<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — compatibility/operations stretch.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Expand-contract, dual-read/write, cache key/version và rollback.<br>
**Answer outline:** Inventory consumers; add tolerant reader/default first; deploy readers trước writers; version key/type/schema; optional dual-write/backfill có deadline; observe decode failures; retire old after retention/TTL; rollback không phát payload mới cho reader cũ.<br>
**Required trade-offs:** Dual formats tăng storage/code/consistency cost nhưng giảm big-bang risk.<br>
**Follow-up ladder:** Poison message? Long cache TTL? Enum evolution? Reprocessing history?<br>
**Red flags:** Deploy producer mới trước rồi xử lý consumer failure sau.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-BOUNDARY-024 — `EXPERT`
**Question:** Vì sao wall clock không phù hợp đo timeout/duration? Thiết kế khi clock jump, skew hoặc leap adjustment thế nào?<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — time/distributed-system discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Wall-clock timestamp vs monotonic elapsed time và cross-process limit.<br>
**Answer outline:** Wall clock có thể nhảy do sync/manual adjustment; dùng monotonic source như `System.nanoTime()` cho elapsed duration trong process, Instant/Clock cho business timestamp. Cross-process ordering cần protocol/version/database/event order, không so nanoTime giữa máy.<br>
**Required trade-offs:** Monotonic đo duration đúng nhưng không map calendar/audit; wall clock cần cho human/event time nhưng không là total order tin cậy.<br>
**Follow-up ladder:** Deadline propagation? Clock skew window? Lease expiry? Test fake clock?<br>
**Red flags:** `currentTimeMillis` luôn tăng hoặc timestamp đủ tạo global ordering.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `JAVA-01` active: tạo core/deep-dive, inject Clock, viết boundary/property tests cho exception cause/suppression, DST, Money equality/rounding và old↔new JSON payload; review broad catch/current serialization metadata bằng evidence. HTTP/transaction mapping thuộc `API-01`/`TX-01`; cross-service schema migration được tái sử dụng ở event/database stages. Stable IDs không tái sử dụng.
