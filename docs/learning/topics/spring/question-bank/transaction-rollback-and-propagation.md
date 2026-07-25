# Spring Interview Question Bank — Transaction Rollback and Propagation

> Status: `DRAFT`<br>
> Domain owner: `Spring Transactions`<br>
> Active slice: `NONE`; preview target: `TX-01`<br>
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)<br>
> Related depth rubric: [Transaction](../../../knowledge-depth-rubric.md#37-transaction-và-data-consistency--p0-target-d3)<br>
> Related theory: [Transaction, Rollback and Propagation](../theory/core/transaction-rollback-and-propagation.md), [transaction/crash-window deep-dive](../theory/deep-dives/transaction-propagation-isolation-and-crash-windows.md) — `DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `TX-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `TX-PROP-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### TX-PROP-001 — `FOUNDATION`
**Question:** Transaction và ACID bảo đảm điều gì, không bảo đảm điều gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Atomicity, consistency invariant, isolation và durability.<br>
**Answer outline:** DB cung cấp atomic commit, isolation theo level, durability; consistency cần schema/business rules đúng. Không tự bao gồm HTTP call, cache hay message broker.<br>
**Required trade-offs:** Isolation/durability mạnh thường đổi lấy throughput/latency.<br>
**Follow-up ladder:** Consistency trong ACID vs distributed consistency?<br>
**Red flags:** Transaction làm toàn hệ thống exactly-once.<br>
**Evidence:** Theory `NOT CREATED`; case `TX-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-PROP-002 — `FOUNDATION`
**Question:** Nên đặt `@Transactional` ở đâu và vì sao controller không phải boundary tốt?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Atomic use case và service ownership.<br>
**Answer outline:** Đặt ở public service method bao trọn một invariant/use case DB; controller chỉ parse/auth/response. Boundary quá rộng giữ connection lâu, quá hẹp tạo partial commit.<br>
**Required trade-offs:** Use-case transaction rõ nhưng đôi khi cần split và eventual consistency.<br>
**Follow-up ladder:** Repository annotation? Read transaction?<br>
**Red flags:** Annotate toàn bộ class/controller theo thói quen.<br>
**Evidence:** Theory `NOT CREATED`; case `TX-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-PROP-003 — `FOUNDATION`
**Question:** Spring mặc định rollback với exception nào; checked exception xử lý ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Runtime/Error default và rollback rules.<br>
**Answer outline:** Mặc định rollback với unchecked `RuntimeException`/`Error`, không phải mọi checked exception; cấu hình `rollbackFor` có chủ đích hoặc translate exception. Catch rồi nuốt exception có thể commit.<br>
**Required trade-offs:** Rollback quá rộng có thể retry business rejection vô ích.<br>
**Follow-up ladder:** `noRollbackFor`? Mark rollback-only?<br>
**Red flags:** Bất kỳ exception nào cũng rollback mặc định.<br>
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-PROP-004 — `FOUNDATION`
**Question:** `REQUIRED`, `REQUIRES_NEW` và `NESTED` khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Join, suspend/new physical transaction và savepoint.<br>
**Answer outline:** REQUIRED join existing hoặc mở mới; REQUIRES_NEW suspend outer và dùng transaction/resource riêng; NESTED thường savepoint trong physical transaction và phụ thuộc manager/driver.<br>
**Required trade-offs:** REQUIRES_NEW cô lập commit nhưng tiêu thụ thêm connection và phá atomicity tổng.<br>
**Follow-up ladder:** Outer rollback thì audit inner? Savepoint support?<br>
**Red flags:** NESTED luôn là transaction độc lập.<br>
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-PROP-005 — `SENIOR`
**Question:** Vì sao catch exception trong inner method vẫn có thể gây `UnexpectedRollbackException`?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Shared transaction và rollback-only marker.<br>
**Answer outline:** Inner REQUIRED có thể đánh dấu physical transaction rollback-only; outer catch không xóa marker, lúc commit framework báo unexpected rollback. Thiết kế exception/boundary đúng thay vì suppress.<br>
**Required trade-offs:** Partial recovery trong cùng transaction thường mâu thuẫn atomic invariant.<br>
**Follow-up ladder:** Khi nào REQUIRES_NEW? Programmatic transaction?<br>
**Red flags:** Catch rồi return success chắc chắn commit.<br>
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-PROP-006 — `SENIOR`
**Question:** Tại sao gọi remote API trong DB transaction là nguy hiểm?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Connection/lock hold, ambiguous failure và non-atomic side effect.<br>
**Answer outline:** Network latency/timeout giữ connection và locks; remote success rồi DB rollback tạo inconsistency. Thu transaction DB, outbox/after-commit workflow và idempotent compensation tùy invariant.<br>
**Required trade-offs:** Eventual consistency thêm state machine/reconciliation.<br>
**Follow-up ladder:** Payment authorization? Saga?<br>
**Red flags:** Tăng transaction timeout để chữa.<br>
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-PROP-007 — `SENIOR`
**Question:** `readOnly`, timeout và isolation trong `@Transactional` thực sự có ý nghĩa gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Hint vs enforcement và DB support.<br>
**Answer outline:** `readOnly` có thể tối ưu flush/driver nhưng không phải universal write guard; timeout/isolation được manager/DB hỗ trợ khác nhau. Xác minh generated behavior và DB session, không dựa tên annotation.<br>
**Required trade-offs:** Per-method tuning tăng precision nhưng config complexity.<br>
**Follow-up ladder:** Hibernate flush mode? Existing transaction override?<br>
**Red flags:** readOnly tuyệt đối chặn mọi SQL write.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-PROP-008 — `SENIOR`
**Question:** Viết integration test nào để chứng minh rollback và propagation thay vì chỉ mock repository?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Real DB boundary và test transaction trap.<br>
**Answer outline:** Gọi service qua Spring proxy với DB thật/Testcontainers; inject failure sau write; kết thúc transaction rồi query từ transaction mới. Test REQUIRED/REQUIRES_NEW riêng và tránh outer test transaction che commit.<br>
**Required trade-offs:** Integration test chậm hơn nhưng mock không chứng minh transaction manager.<br>
**Follow-up ladder:** `@Transactional` trên test? Await async?<br>
**Red flags:** Verify `save()` proves rollback.<br>
**Evidence:** Test design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-PROP-009 — `ARCHITECT`
**Question:** Chia transaction boundaries cho một workflow nhiều aggregate/service thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Invariant ownership và consistency model.<br>
**Answer outline:** Atomic transaction chỉ nơi cùng database/owner và invariant thật sự cần; ngoài boundary dùng explicit state, outbox/inbox, idempotency, compensation và reconciliation SLO.<br>
**Required trade-offs:** Strong consistency đơn giản cho consumer nhưng giới hạn autonomy/availability.<br>
**Follow-up ladder:** Saga orchestration vs choreography?<br>
**Red flags:** Distributed transaction mặc định cho mọi workflow.<br>
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-PROP-010 — `EXPERT`
**Question:** `REQUIRES_NEW` lồng nhau gây pool starvation/deadlock như thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Suspended connection, concurrent demand và lock ordering.<br>
**Answer outline:** Mỗi outer giữ connection rồi chờ inner mượn thêm; nếu concurrency chiếm hết pool, tất cả chờ. Inner còn có thể chờ lock outer giữ. Model connection demand, metrics, timeout và redesign boundary.<br>
**Required trade-offs:** Pool lớn giảm symptom nhưng tăng DB pressure, không sửa lock cycle.<br>
**Follow-up ladder:** Công thức pool floor? Deadlock evidence?<br>
**Red flags:** REQUIRES_NEW luôn an toàn hơn REQUIRED.<br>
**Evidence:** Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `TX-01` active, tạo propagation reproducer với DB thật và evidence; không đổi/reuse stable IDs.
