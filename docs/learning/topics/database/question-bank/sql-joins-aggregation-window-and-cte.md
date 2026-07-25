# Database Interview Question Bank — SQL Joins, Aggregation, Window Functions and CTE

> Status: `DRAFT`  
> Domain owner: `PostgreSQL/SQL`  
> Active slice: `NONE`; preview target: `SQL-01`  
> Related roadmap: [Stage 3](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)  
> Related depth rubric: [PostgreSQL/SQL](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/database/theory/core/sql-joins-aggregation-window-and-cte.md`  
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
**Question:** `INNER JOIN` và `LEFT JOIN` khác nhau; filter ở `ON` và `WHERE` ảnh hưởng thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Preserved rows và null-rejecting predicate.  
**Answer outline:** INNER chỉ matched; LEFT giữ toàn bộ left và null phía right. Predicate right table ở WHERE có thể biến LEFT thành hiệu ứng inner; ở ON giới hạn match nhưng vẫn giữ left rows.  
**Required trade-offs:** Query rõ semantics quan trọng hơn dùng join “mặc định”.  
**Follow-up ladder:** Anti-join? FULL JOIN?  
**Red flags:** ON và WHERE luôn tương đương.  
**Evidence:** Theory `NOT CREATED`; case `SQL-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-002 — `FOUNDATION`
**Question:** `WHERE` và `HAVING` khác nhau; aggregation query được xử lý logic theo thứ tự nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Pre-group filter và post-group filter.  
**Answer outline:** WHERE lọc rows trước GROUP BY; HAVING lọc groups sau aggregate; SELECT/ORDER sau đó theo logical processing. Đẩy predicate hợp lệ xuống WHERE giảm dữ liệu aggregate.  
**Required trade-offs:** Rewrite tối ưu phải giữ null/group semantics.  
**Follow-up ladder:** Alias dùng trong HAVING? FILTER clause?  
**Red flags:** HAVING chỉ là WHERE có SUM.  
**Evidence:** Theory `NOT CREATED`; case `SQL-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-003 — `FOUNDATION`
**Question:** Window function khác `GROUP BY` thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Row preservation, partition và order.  
**Answer outline:** GROUP BY collapse rows thành group; window tính qua partition/frame nhưng giữ từng row, dùng cho ranking, running total, lag/lead. Window order không tự bảo đảm final output order.  
**Required trade-offs:** Window diễn đạt tốt nhưng sort/memory cost có thể cao.  
**Follow-up ladder:** `row_number` vs `rank`? Frame default?  
**Red flags:** Window function luôn nhanh hơn subquery.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-004 — `FOUNDATION`
**Question:** CTE dùng để làm gì; recursive CTE phù hợp bài toán nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Query decomposition và recursion anchor/step.  
**Answer outline:** CTE đặt tên intermediate relation để tăng clarity/reuse; recursive CTE có anchor và recursive term cho hierarchy/graph traversal có guard. Materialization/inlining phụ thuộc version/query.  
**Required trade-offs:** Readability vs optimizer fence/materialization cost.  
**Follow-up ladder:** Cycle detection? `WITH MATERIALIZED`?  
**Red flags:** CTE luôn được cache và nhanh.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-005 — `SENIOR`
**Question:** Viết query lấy top N stream theo category và giải thích tie handling.  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `CODING_SCENARIO`  
**Interviewer evaluates:** Partitioned ranking và deterministic order.  
**Answer outline:** Dùng `row_number()`/`rank()` over `(partition by category order by metric desc, id)` rồi filter rank; chọn row_number cho đúng N, rank/dense_rank nếu giữ ties theo contract. Index/pre-aggregation tùy volume.  
**Required trade-offs:** Real-time aggregate chính xác vs precomputed read model.  
**Follow-up ladder:** Latest row per group? Keyset page ranked data?  
**Red flags:** GROUP BY category rồi LIMIT N toàn cục.  
**Evidence:** Theory `NOT CREATED`; query lab `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-006 — `SENIOR`
**Question:** Đọc `EXPLAIN (ANALYZE, BUFFERS)` để tìm bottleneck như thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `DIAGNOSTIC`  
**Interviewer evaluates:** Estimated/actual rows, loops, I/O, sorts và plan shape.  
**Answer outline:** Bắt đầu total time rồi node tốn actual time×loops/buffers; so estimate vs actual để thấy statistics/correlation; xem scan/join/sort spill; đo query thật với dữ liệu đại diện, không chỉ săn seq scan.  
**Required trade-offs:** Index giúp read nhưng tăng write/storage/vacuum.  
**Follow-up ladder:** Nested loop bad khi nào? Generic plan?  
**Red flags:** Seq scan luôn là lỗi.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-007 — `SENIOR`
**Question:** Fan-out join làm sai COUNT/SUM thế nào và sửa ra sao?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Cardinality multiplication.  
**Answer outline:** Join một parent với hai one-to-many tạo tích tổ hợp, nhân metrics; pre-aggregate từng child, semi-join/EXISTS hoặc xác định correct grain trước join. `DISTINCT` không phải thuốc chung.  
**Required trade-offs:** Pre-aggregation thêm query complexity nhưng giữ semantics và giảm rows.  
**Follow-up ladder:** Count distinct cost? Lateral join?  
**Red flags:** Thêm DISTINCT vào mọi aggregate.  
**Evidence:** Theory `NOT CREATED`; query lab `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-008 — `SENIOR`
**Question:** Dynamic filtering/sorting từ API nên xây thế nào để đúng và chống SQL injection?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Parameter binding, allowlist và plan/index.  
**Answer outline:** Bind values; allowlist map external sort/filter fields sang expressions cố định; validate direction/operator/page size; không concatenate identifiers từ user. Theo dõi query shapes và cap expensive combinations.  
**Required trade-offs:** Flexibility cao làm plan/index/cost khó kiểm soát.  
**Follow-up ladder:** Optional predicates? Prepared statement?  
**Red flags:** Escape chuỗi thủ công là đủ.  
**Evidence:** Theory `NOT CREATED`; project query `NOT SELECTED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-009 — `ARCHITECT`
**Question:** Chọn normalized OLTP, materialized view hay analytics store cho reporting thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Freshness, write/read load và ownership.  
**Answer outline:** Query nhẹ/fresh dùng OLTP; repeated expensive aggregate dùng indexed/precomputed view với refresh SLO; analytical scale/ad-hoc tách CDC warehouse. Có reconciliation, lineage và cost model.  
**Required trade-offs:** Freshness/consistency vs isolation/throughput/cost.  
**Follow-up ladder:** Incremental refresh? Backfill?  
**Red flags:** Đẩy mọi reporting sang replica là kiến trúc hoàn chỉnh.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SQL-QUERY-010 — `EXPERT`
**Question:** Một query plan nhanh ở staging nhưng chậm production: điều tra plan instability thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Data distribution, statistics, parameters và memory.  
**Answer outline:** So plan/settings/schema/stats/data skew/bloat/cache/parameters; capture actual buffers/temp/locks; xem stale stats, correlation, generic vs custom plan, work_mem và concurrent load; sửa bằng stats/index/query trước khi hint-like workaround.  
**Required trade-offs:** Plan pinning ổn định ngắn hạn nhưng dễ lỗi khi data đổi.  
**Follow-up ladder:** Extended statistics? Parameter sniffing analogue?  
**Red flags:** Copy production data là cách duy nhất để debug.  
**Evidence:** Incident lab `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SQL-01` active, chạy query lab với PostgreSQL và lưu plan/evidence thật; không đổi/reuse stable IDs.
