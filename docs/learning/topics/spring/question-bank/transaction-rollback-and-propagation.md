# Spring Interview Question Bank — Transaction Rollback and Propagation

> Status: `DRAFT`  
> Domain owner: `Spring Transactions`  
> Active slice: `NONE`; preview target: `TX-01`  
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)  
> Related depth rubric: [Transaction](../../../knowledge-depth-rubric.md#37-transaction-và-data-consistency--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/spring/theory/core/transaction-rollback-and-propagation.md`  
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
**Question:** Transaction và ACID bảo đảm điều gì, không bảo đảm điều gì?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Atomicity, consistency invariant, isolation và durability.  
**Answer outline:** DB cung cấp atomic commit, isolation theo level, durability; consistency cần schema/business rules đúng. Không tự bao gồm HTTP call, cache hay message broker.  
**Required trade-offs:** Isolation/durability mạnh thường đổi lấy throughput/latency.  
**Follow-up ladder:** Consistency trong ACID vs distributed consistency?  
**Red flags:** Transaction làm toàn hệ thống exactly-once.  
**Evidence:** Theory `NOT CREATED`; case `TX-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-PROP-002 — `FOUNDATION`
**Question:** Nên đặt `@Transactional` ở đâu và vì sao controller không phải boundary tốt?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Atomic use case và service ownership.  
**Answer outline:** Đặt ở public service method bao trọn một invariant/use case DB; controller chỉ parse/auth/response. Boundary quá rộng giữ connection lâu, quá hẹp tạo partial commit.  
**Required trade-offs:** Use-case transaction rõ nhưng đôi khi cần split và eventual consistency.  
**Follow-up ladder:** Repository annotation? Read transaction?  
**Red flags:** Annotate toàn bộ class/controller theo thói quen.  
**Evidence:** Theory `NOT CREATED`; case `TX-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-PROP-003 — `FOUNDATION`
**Question:** Spring mặc định rollback với exception nào; checked exception xử lý ra sao?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Runtime/Error default và rollback rules.  
**Answer outline:** Mặc định rollback với unchecked `RuntimeException`/`Error`, không phải mọi checked exception; cấu hình `rollbackFor` có chủ đích hoặc translate exception. Catch rồi nuốt exception có thể commit.  
**Required trade-offs:** Rollback quá rộng có thể retry business rejection vô ích.  
**Follow-up ladder:** `noRollbackFor`? Mark rollback-only?  
**Red flags:** Bất kỳ exception nào cũng rollback mặc định.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-PROP-004 — `FOUNDATION`
**Question:** `REQUIRED`, `REQUIRES_NEW` và `NESTED` khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Join, suspend/new physical transaction và savepoint.  
**Answer outline:** REQUIRED join existing hoặc mở mới; REQUIRES_NEW suspend outer và dùng transaction/resource riêng; NESTED thường savepoint trong physical transaction và phụ thuộc manager/driver.  
**Required trade-offs:** REQUIRES_NEW cô lập commit nhưng tiêu thụ thêm connection và phá atomicity tổng.  
**Follow-up ladder:** Outer rollback thì audit inner? Savepoint support?  
**Red flags:** NESTED luôn là transaction độc lập.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-PROP-005 — `SENIOR`
**Question:** Vì sao catch exception trong inner method vẫn có thể gây `UnexpectedRollbackException`?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Shared transaction và rollback-only marker.  
**Answer outline:** Inner REQUIRED có thể đánh dấu physical transaction rollback-only; outer catch không xóa marker, lúc commit framework báo unexpected rollback. Thiết kế exception/boundary đúng thay vì suppress.  
**Required trade-offs:** Partial recovery trong cùng transaction thường mâu thuẫn atomic invariant.  
**Follow-up ladder:** Khi nào REQUIRES_NEW? Programmatic transaction?  
**Red flags:** Catch rồi return success chắc chắn commit.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-PROP-006 — `SENIOR`
**Question:** Tại sao gọi remote API trong DB transaction là nguy hiểm?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Connection/lock hold, ambiguous failure và non-atomic side effect.  
**Answer outline:** Network latency/timeout giữ connection và locks; remote success rồi DB rollback tạo inconsistency. Thu transaction DB, outbox/after-commit workflow và idempotent compensation tùy invariant.  
**Required trade-offs:** Eventual consistency thêm state machine/reconciliation.  
**Follow-up ladder:** Payment authorization? Saga?  
**Red flags:** Tăng transaction timeout để chữa.  
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-PROP-007 — `SENIOR`
**Question:** `readOnly`, timeout và isolation trong `@Transactional` thực sự có ý nghĩa gì?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Hint vs enforcement và DB support.  
**Answer outline:** `readOnly` có thể tối ưu flush/driver nhưng không phải universal write guard; timeout/isolation được manager/DB hỗ trợ khác nhau. Xác minh generated behavior và DB session, không dựa tên annotation.  
**Required trade-offs:** Per-method tuning tăng precision nhưng config complexity.  
**Follow-up ladder:** Hibernate flush mode? Existing transaction override?  
**Red flags:** readOnly tuyệt đối chặn mọi SQL write.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-PROP-008 — `SENIOR`
**Question:** Viết integration test nào để chứng minh rollback và propagation thay vì chỉ mock repository?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Real DB boundary và test transaction trap.  
**Answer outline:** Gọi service qua Spring proxy với DB thật/Testcontainers; inject failure sau write; kết thúc transaction rồi query từ transaction mới. Test REQUIRED/REQUIRES_NEW riêng và tránh outer test transaction che commit.  
**Required trade-offs:** Integration test chậm hơn nhưng mock không chứng minh transaction manager.  
**Follow-up ladder:** `@Transactional` trên test? Await async?  
**Red flags:** Verify `save()` proves rollback.  
**Evidence:** Test design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-PROP-009 — `ARCHITECT`
**Question:** Chia transaction boundaries cho một workflow nhiều aggregate/service thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Invariant ownership và consistency model.  
**Answer outline:** Atomic transaction chỉ nơi cùng database/owner và invariant thật sự cần; ngoài boundary dùng explicit state, outbox/inbox, idempotency, compensation và reconciliation SLO.  
**Required trade-offs:** Strong consistency đơn giản cho consumer nhưng giới hạn autonomy/availability.  
**Follow-up ladder:** Saga orchestration vs choreography?  
**Red flags:** Distributed transaction mặc định cho mọi workflow.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-PROP-010 — `EXPERT`
**Question:** `REQUIRES_NEW` lồng nhau gây pool starvation/deadlock như thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Suspended connection, concurrent demand và lock ordering.  
**Answer outline:** Mỗi outer giữ connection rồi chờ inner mượn thêm; nếu concurrency chiếm hết pool, tất cả chờ. Inner còn có thể chờ lock outer giữ. Model connection demand, metrics, timeout và redesign boundary.  
**Required trade-offs:** Pool lớn giảm symptom nhưng tăng DB pressure, không sửa lock cycle.  
**Follow-up ladder:** Công thức pool floor? Deadlock evidence?  
**Red flags:** REQUIRES_NEW luôn an toàn hơn REQUIRED.  
**Evidence:** Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `TX-01` active, tạo propagation reproducer với DB thật và evidence; không đổi/reuse stable IDs.
