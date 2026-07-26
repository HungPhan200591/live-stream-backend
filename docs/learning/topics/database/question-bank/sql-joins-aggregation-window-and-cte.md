# Database Interview Question Bank — SQL Joins, Aggregation, Window Functions and CTE

> Status: `DRAFT`<br>
> Domain owner: `PostgreSQL/SQL`<br>
> Active slice: `NONE`; preview target: `SQL-01`<br>
> Related roadmap: [Stage 3](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)<br>
> Related depth rubric: [PostgreSQL/SQL](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/sql-joins-aggregation-window-and-cte.md) · [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `SQL-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `SQL-QUERY-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### SQL-QUERY-001 — `FOUNDATION`
**Question:** `INNER JOIN` và `LEFT JOIN` khác nhau; filter ở `ON` và `WHERE` ảnh hưởng thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Preserved rows và null-rejecting predicate.<br>
**Answer outline:** INNER chỉ matched; LEFT giữ toàn bộ left và null phía right. Predicate right table ở WHERE có thể biến LEFT thành hiệu ứng inner; ở ON giới hạn match nhưng vẫn giữ left rows.<br>
**Required trade-offs:** Query rõ semantics quan trọng hơn dùng join “mặc định”.<br>
**Follow-up ladder:** Anti-join? FULL JOIN?<br>
**Red flags:** ON và WHERE luôn tương đương.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); case `SQL-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-002 — `FOUNDATION`
**Question:** `WHERE` và `HAVING` khác nhau; aggregation query được xử lý logic theo thứ tự nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Pre-group filter và post-group filter.<br>
**Answer outline:** WHERE lọc rows trước GROUP BY; HAVING lọc groups sau aggregate; SELECT/ORDER sau đó theo logical processing. Đẩy predicate hợp lệ xuống WHERE giảm dữ liệu aggregate.<br>
**Required trade-offs:** Rewrite tối ưu phải giữ null/group semantics.<br>
**Follow-up ladder:** Alias dùng trong HAVING? FILTER clause?<br>
**Red flags:** HAVING chỉ là WHERE có SUM.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); case `SQL-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-003 — `FOUNDATION`
**Question:** Window function khác `GROUP BY` thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Row preservation, partition và order.<br>
**Answer outline:** GROUP BY collapse rows thành group; window tính qua partition/frame nhưng giữ từng row, dùng cho ranking, running total, lag/lead. Window order không tự bảo đảm final output order.<br>
**Required trade-offs:** Window diễn đạt tốt nhưng sort/memory cost có thể cao.<br>
**Follow-up ladder:** `row_number` vs `rank`? Frame default?<br>
**Red flags:** Window function luôn nhanh hơn subquery.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-004 — `FOUNDATION`
**Question:** CTE dùng để làm gì; recursive CTE phù hợp bài toán nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Query decomposition và recursion anchor/step.<br>
**Answer outline:** CTE đặt tên intermediate relation để tăng clarity/reuse; recursive CTE có anchor và recursive term cho hierarchy/graph traversal có guard. Materialization/inlining phụ thuộc version/query.<br>
**Required trade-offs:** Readability vs optimizer fence/materialization cost.<br>
**Follow-up ladder:** Cycle detection? `WITH MATERIALIZED`?<br>
**Red flags:** CTE luôn được cache và nhanh.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-005 — `SENIOR`
**Question:** Viết query lấy top N stream theo category và giải thích tie handling.<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `CODING_SCENARIO`<br>
**Interviewer evaluates:** Partitioned ranking và deterministic order.<br>
**Answer outline:** Dùng `row_number()`/`rank()` over `(partition by category order by metric desc, id)` rồi filter rank; chọn row_number cho đúng N, rank/dense_rank nếu giữ ties theo contract. Index/pre-aggregation tùy volume.<br>
**Required trade-offs:** Real-time aggregate chính xác vs precomputed read model.<br>
**Follow-up ladder:** Latest row per group? Keyset page ranked data?<br>
**Red flags:** GROUP BY category rồi LIMIT N toàn cục.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); query lab `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-006 — `SENIOR`
**Question:** Đọc `EXPLAIN (ANALYZE, BUFFERS)` để tìm bottleneck như thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `DIAGNOSTIC`<br>
**Interviewer evaluates:** Estimated/actual rows, loops, I/O, sorts và plan shape.<br>
**Answer outline:** Bắt đầu total time rồi node tốn actual time×loops/buffers; so estimate vs actual để thấy statistics/correlation; xem scan/join/sort spill; đo query thật với dữ liệu đại diện, không chỉ săn seq scan.<br>
**Required trade-offs:** Index giúp read nhưng tăng write/storage/vacuum.<br>
**Follow-up ladder:** Nested loop bad khi nào? Generic plan?<br>
**Red flags:** Seq scan luôn là lỗi.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-007 — `SENIOR`
**Question:** Fan-out join làm sai COUNT/SUM thế nào và sửa ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Cardinality multiplication.<br>
**Answer outline:** Join một parent với hai one-to-many tạo tích tổ hợp, nhân metrics; pre-aggregate từng child, semi-join/EXISTS hoặc xác định correct grain trước join. `DISTINCT` không phải thuốc chung.<br>
**Required trade-offs:** Pre-aggregation thêm query complexity nhưng giữ semantics và giảm rows.<br>
**Follow-up ladder:** Count distinct cost? Lateral join?<br>
**Red flags:** Thêm DISTINCT vào mọi aggregate.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); query lab `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-008 — `SENIOR`
**Question:** Dynamic filtering/sorting từ API nên xây thế nào để đúng và chống SQL injection?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Parameter binding, allowlist và plan/index.<br>
**Answer outline:** Bind values; allowlist map external sort/filter fields sang expressions cố định; validate direction/operator/page size; không concatenate identifiers từ user. Theo dõi query shapes và cap expensive combinations.<br>
**Required trade-offs:** Flexibility cao làm plan/index/cost khó kiểm soát.<br>
**Follow-up ladder:** Optional predicates? Prepared statement?<br>
**Red flags:** Escape chuỗi thủ công là đủ.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); project query `NOT SELECTED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-009 — `ARCHITECT`
**Question:** Chọn normalized OLTP, materialized view hay analytics store cho reporting thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Freshness, write/read load và ownership.<br>
**Answer outline:** Query nhẹ/fresh dùng OLTP; repeated expensive aggregate dùng indexed/precomputed view với refresh SLO; analytical scale/ad-hoc tách CDC warehouse. Có reconciliation, lineage và cost model.<br>
**Required trade-offs:** Freshness/consistency vs isolation/throughput/cost.<br>
**Follow-up ladder:** Incremental refresh? Backfill?<br>
**Red flags:** Đẩy mọi reporting sang replica là kiến trúc hoàn chỉnh.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-010 — `EXPERT`
**Question:** Một query plan nhanh ở staging nhưng chậm production: điều tra plan instability thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Data distribution, statistics, parameters và memory.<br>
**Answer outline:** So plan/settings/schema/stats/data skew/bloat/cache/parameters; capture actual buffers/temp/locks; xem stale stats, correlation, generic vs custom plan, work_mem và concurrent load; sửa bằng stats/index/query trước khi hint-like workaround.<br>
**Required trade-offs:** Plan pinning ổn định ngắn hạn nhưng dễ lỗi khi data đổi.<br>
**Follow-up ladder:** Extended statistics? Parameter sniffing analogue?<br>
**Red flags:** Copy production data là cách duy nhất để debug.<br>
**Evidence:** Theory [Core](../theory/core/sql-joins-aggregation-window-and-cte.md) + [Deep-dive](../theory/deep-dives/sql-grain-window-frames-and-cte-plan-boundaries.md); Incident lab `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SQL-01` active, chạy query lab với PostgreSQL và lưu plan/evidence thật; không đổi/reuse stable IDs.
