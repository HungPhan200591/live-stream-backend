# Database Interview Question Bank — Replica Lag and Read-Your-Writes

> Status: `DRAFT`<br>
> Domain owner: `PostgreSQL Replication`<br>
> Active slice: `NONE`; preview target: `DB-02`<br>
> Related roadmap: [Stage 9](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-9---primaryreplica-partitioning-và-data-lifecycle)<br>
> Related depth rubric: [Data operations](../../../knowledge-depth-rubric.md#319-data-operations-và-lifecycle--p1-target-d2-d3)<br>
> Related theory: [Core theory](../theory/core/replica-lag-and-read-your-writes.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DB-02`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `DB-REPL-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### DB-REPL-001 — `FOUNDATION`
**Question:** Primary-replica replication dùng WAL và asynchronous apply ở mức khái niệm thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Write owner, transport/replay and lag.<br>
**Answer outline:** Primary commits/WAL; standby receives and replays. Async commit need not wait replica so reads may be stale and failover may lose recent writes; sync changes ack policy.<br>
**Required trade-offs:** Async latency/availability vs durability/freshness.<br>
**Follow-up ladder:** Physical vs logical replication?<br>
**Red flags:** Replica là synchronous copy mặc định.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-REPL-002 — `FOUNDATION`
**Question:** Replication lag được đo theo bytes, time và replay position khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Signal semantics and idle caveat.<br>
**Answer outline:** Compare LSN sent/write/flush/replay and wall-clock delay; time lag can appear zero/unknown when idle, bytes reflect backlog not apply time. Endpoint staleness needs business observation.<br>
**Required trade-offs:** Detailed metrics improve diagnosis but not guarantee per-row freshness.<br>
**Follow-up ladder:** Replay timestamp? Queueing?<br>
**Red flags:** One lag seconds metric proves read consistency.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-REPL-003 — `FOUNDATION`
**Question:** Read-your-writes, monotonic reads và eventual consistency khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Session guarantees vs convergence.<br>
**Answer outline:** RYW means session sees own committed writes; monotonic avoids going backward; eventual only converges without new writes. Replica routing must state guarantees per endpoint.<br>
**Required trade-offs:** Stronger session guarantees increase primary load/coordination.<br>
**Follow-up ladder:** Causal consistency?<br>
**Red flags:** Eventual consistency implies RYW soon enough.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-REPL-004 — `FOUNDATION`
**Question:** `@Transactional(readOnly=true)` có tự route/guarantee replica consistency không?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Framework hint vs routing and database state.<br>
**Answer outline:** readOnly may alter flush/driver; routing needs explicit datasource/context before connection acquisition, and says nothing about replica freshness. Transaction nesting can pin datasource unexpectedly.<br>
**Required trade-offs:** Automatic routing convenient but hidden context errors.<br>
**Follow-up ladder:** Lazy connection proxy?<br>
**Red flags:** readOnly=true luôn chạy replica.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-REPL-005 — `SENIOR`
**Question:** Sau create rồi GET trả 404 từ replica: thiết kế policy nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Sticky primary, consistency token or lag-aware fallback.<br>
**Answer outline:** For RYW endpoint route user/session to primary for bounded window, pass commit LSN token and wait/fallback until replica caught up, or return created representation; define timeout/load behavior.<br>
**Required trade-offs:** Freshness increases primary load/latency.<br>
**Follow-up ladder:** Token spoofing? Cache interaction?<br>
**Red flags:** Retry GET replica nhanh nhiều lần.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-REPL-006 — `SENIOR`
**Question:** Replica lag tăng: phân biệt network, WAL generation, apply IO/CPU, locks và long query thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Stage-specific positions and resource evidence.<br>
**Answer outline:** Compare send/write/flush/replay LSN, network, disk, replay conflicts, CPU/IO, slots and long snapshots; correlate write spike/DDL. Throttle workload or route reads with policy before scaling blindly.<br>
**Required trade-offs:** Containment may reduce analytics/read capacity.<br>
**Follow-up ladder:** Hot standby conflict? WAL receiver?<br>
**Red flags:** Restart replica là bước đầu tiên.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-REPL-007 — `SENIOR`
**Question:** Failover ảnh hưởng connection pools, DNS, transaction và idempotency thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Role change and ambiguous commits.<br>
**Answer outline:** Clients may hold dead/old-primary connections; refresh pools/discovery, fence old primary, retry only idempotent transactions because commit outcome ambiguous, verify timeline and cache/broker dependencies.<br>
**Required trade-offs:** Fast failover vs split-brain/data loss risk.<br>
**Follow-up ladder:** Read-only old primary? DNS TTL?<br>
**Red flags:** Driver reconnect tự đảm bảo không duplicate.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-REPL-008 — `SENIOR`
**Question:** Test stale read và routing bằng local primary/standby thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Controlled apply delay, commit marker and endpoint assertions.<br>
**Answer outline:** Inject replay delay/pause, write marker on primary, issue reads with/without consistency policy, assert stale/fallback and capture LSN/lag; remove delay and verify recovery.<br>
**Required trade-offs:** Real replication lab costly/flaky if timing not controlled.<br>
**Follow-up ladder:** Testcontainers topology?<br>
**Red flags:** Mock replica datasource proves WAL lag behavior.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-REPL-009 — `ARCHITECT`
**Question:** Chọn sync/async replication và endpoint routing theo RPO/SLO thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Per-use-case durability, latency and availability.<br>
**Answer outline:** Financial intent may require sync quorum or single owner; feeds/analytics tolerate async replica; classify RYW, stale budget, failover RPO and capacity, then document degradation.<br>
**Required trade-offs:** Sync reduces RPO but adds commit latency/availability coupling.<br>
**Follow-up ladder:** Remote region? Quorum?<br>
**Red flags:** Một replication mode cho toàn database.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-REPL-010 — `EXPERT`
**Question:** Replica được promote khi còn lag và old primary quay lại: ngăn split brain/reconcile ra sao?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Fencing, timeline and divergent writes.<br>
**Answer outline:** Use consensus-controlled failover/fencing, never accept writes on both; preserve timelines/WAL, quantify lost/diverged transactions, rebuild old primary from new authority and reconcile external side effects with IDs.<br>
**Required trade-offs:** Strict fencing may delay recovery but protects correctness.<br>
**Follow-up ladder:** STONITH? Logical reconciliation?<br>
**Red flags:** Last writer wins merge hai primary.<br>
**Evidence:** Theory [Core](../theory/core/replica-lag-and-read-your-writes.md); case `DB-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DB-02` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.
