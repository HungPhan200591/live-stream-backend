# Architecture Interview Question Bank — Storage Selection, Search, Object and Columnar Systems

> Status: `DRAFT`<br>
> Domain owner: `Storage Architecture`<br>
> Active slice: `NONE`; preview target: `DATA-01`<br>
> Related roadmap: [Stage 11 extensions](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-11---solution-architecture-capstones)<br>
> Related depth rubric: [Storage selection](../../../knowledge-depth-rubric.md#322-storage-selection-ngoài-rdbms--p2-target-d1-d2)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/architecture/theory/core/storage-selection-search-object-and-columnar.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DATA-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `DATA-STORE-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### DATA-STORE-001 — `FOUNDATION`
**Question:** Chọn storage từ access pattern cần hỏi những câu nào trước công nghệ?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Reads/writes, query shape, consistency, retention, scale and recovery.<br>
**Answer outline:** Quantify keys/ranges/full-text/aggregates, write/read QPS, item size/growth, latency/freshness, transactions, retention, RPO/RTO, operations/cost. Start with simplest system meeting constraints.<br>
**Required trade-offs:** More specialized fit improves workload but adds data copies/ops.<br>
**Follow-up ladder:** Data residency? Multi-tenancy?<br>
**Red flags:** Chọn database theo độ phổ biến.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DATA-STORE-002 — `FOUNDATION`
**Question:** RDBMS, key-value, document và wide-column stores khác nhau ở model/query nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Relational constraints/joins vs key/document/partition access.<br>
**Answer outline:** RDBMS for transactions/relations/ad-hoc SQL; KV for known key/simple values; document for aggregate-shaped flexible data; wide-column for massive partition-key/range workloads. Products vary.<br>
**Required trade-offs:** Schema/query flexibility vs predictable scale/consistency.<br>
**Follow-up ladder:** Secondary indexes?<br>
**Red flags:** NoSQL nghĩa schema-less và infinitely scalable.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DATA-STORE-003 — `FOUNDATION`
**Question:** Search index khác source-of-truth database thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Inverted index/relevance and derived projection.<br>
**Answer outline:** Search optimizes token/text/filter/ranking and may be eventually updated; authoritative writes/invariants stay DB, index rebuildable with version/checkpoint and deletion handling.<br>
**Required trade-offs:** Search UX/latency vs freshness/dual-write/ops.<br>
**Follow-up ladder:** Autocomplete? Vector search?<br>
**Red flags:** Elasticsearch nên giữ wallet balance.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DATA-STORE-004 — `FOUNDATION`
**Question:** Object storage và columnar analytics store phù hợp dữ liệu nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Large immutable blobs vs scan/aggregate columns.<br>
**Answer outline:** Object store for media/files/backups with metadata/lifecycle; columnar systems compress/scan selected columns for analytics, not OLTP row updates. Keep ownership/catalog/retention.<br>
**Required trade-offs:** Low storage/query cost vs retrieval latency/consistency/tooling.<br>
**Follow-up ladder:** Data lake/table format?<br>
**Red flags:** Lưu video blob trực tiếp PostgreSQL luôn tốt hơn.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DATA-STORE-005 — `SENIOR`
**Question:** Thiết kế PostgreSQL source + search projection không mất/duplicate update thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Outbox/CDC, version and rebuild.<br>
**Answer outline:** Commit entity+outbox, project idempotently by entity version, handle delete tombstone, monitor lag/DLQ, full rebuild into new index then alias cutover/checksum.<br>
**Required trade-offs:** Fresh synchronous index vs durable async lag.<br>
**Follow-up ladder:** Reindex while writes continue?<br>
**Red flags:** Try/catch save DB rồi call search đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DATA-STORE-006 — `SENIOR`
**Question:** Storage decision matrix cần tính total cost gì ngoài license?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Compute/storage/IO/egress, people, backups and migration.<br>
**Answer outline:** Model peak/headroom, replicas/backups, network/egress, managed premium, on-call expertise, security/compliance, data transfer/rebuild and exit cost; compare cost per workload/SLO.<br>
**Required trade-offs:** Managed higher bill may lower staffing/risk.<br>
**Follow-up ladder:** Reserved tiers? Data gravity?<br>
**Red flags:** Open source tự host miễn phí.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DATA-STORE-007 — `SENIOR`
**Question:** Polyglot persistence trở thành liability khi nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Duplicate systems without distinct justified workload.<br>
**Answer outline:** Each store needs owner, contract, sync/rebuild, backup/security/monitoring and skill; reject new store if PostgreSQL/index/cache can meet measured SLO. Define exit/revisit criteria.<br>
**Required trade-offs:** One store simpler but may force poor workload/cost.<br>
**Follow-up ladder:** Platform team threshold?<br>
**Red flags:** Mỗi microservice chọn database riêng tùy thích.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DATA-STORE-008 — `SENIOR`
**Question:** Retention/privacy deletion xuyên DB, search, cache, object và backup được thiết kế ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Data lineage, tombstones and legal/recovery constraints.<br>
**Answer outline:** Inventory copies/owners, propagate versioned deletion, verify derived stores, lifecycle object versions, document backup expiry/legal hold and prevent restore resurrecting data via deletion ledger.<br>
**Required trade-offs:** Immediate deletion vs immutable backup/compliance.<br>
**Follow-up ladder:** Right to erasure? Crypto-shredding?<br>
**Red flags:** Xóa row source là toàn bộ dữ liệu biến mất.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DATA-STORE-009 — `ARCHITECT`
**Question:** Thiết kế near-real-time analytics từ Kafka với replay/backfill thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Event log, stream processing, warehouse/columnar sink and correctness.<br>
**Answer outline:** Versioned events/outbox, partitioning, idempotent transforms/checkpoints, columnar sink, lag SLO, late event/watermark, replay into shadow tables and reconcile before cutover.<br>
**Required trade-offs:** Freshness/complexity/cost vs OLTP isolation.<br>
**Follow-up ladder:** Lambda vs Kappa? Schema registry?<br>
**Red flags:** Consumer chạy được nghĩa analytics correct.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DATA-STORE-010 — `EXPERT`
**Question:** Primary store/vendor không còn phù hợp: migrate petabyte-scale online thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Dual-read/write/CDC, validation and rollback.<br>
**Answer outline:** Define authoritative phase, bulk snapshot+CDC catch-up, idempotent transform, checksums/shadow reads, cohort cutover, freeze/repair gaps and decommission after retention; model egress/time/cost.<br>
**Required trade-offs:** Long dual system doubles cost and consistency risk.<br>
**Follow-up ladder:** Clock/cursor cut? Contract change?<br>
**Red flags:** Copy once rồi đổi endpoint.<br>
**Evidence:** Theory `NOT CREATED`; case `DATA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DATA-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.
