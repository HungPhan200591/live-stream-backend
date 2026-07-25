# Platform Interview Question Bank — gRPC, GraphQL, Native Image and Platform-Specific Stack

> Status: `DRAFT`<br>
> Domain owner: `Conditional Platform Extensions`<br>
> Active slice: `NONE`; preview target: `CONDITIONAL_P3`<br>
> Related roadmap: [P3 activation rule](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#62-backlog-catalog-map-theo-primary-stage)<br>
> Related depth rubric: [P3 platform stack](../../../knowledge-depth-rubric.md#324-grpc-graphql-native-image-và-platform-specific-stack--p3-target-d1)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/platform/theory/core/grpc-graphql-native-image-and-platform-specific-stack.md`<br>
> Updated: `2026-07-26`

Conditional preview; không có backlog owner và không active/implement stack. Chỉ kích hoạt khi target role/constraint cung cấp evidence. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `PLAT-EXT-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### PLAT-EXT-001 — `FOUNDATION`
**Question:** gRPC/Protobuf khác REST/JSON ở contract và transport nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** IDL/binary RPC/HTTP2 vs resource-oriented text ecosystem.<br>
**Answer outline:** gRPC uses typed IDL/generated stubs, binary frames and HTTP/2 streaming; REST/JSON is ubiquitous/debug/cache/browser-friendly with HTTP semantics. Choose by clients/network/contracts.<br>
**Required trade-offs:** Efficiency/type safety vs compatibility/tooling/human readability.<br>
**Follow-up ladder:** gRPC-Web?<br>
**Red flags:** Binary protocol tự động nhanh hơn trong mọi workload.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-002 — `FOUNDATION`
**Question:** Unary, server/client và bidirectional streaming gRPC khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Message direction/lifecycle and flow control.<br>
**Answer outline:** Unary one request/response; streaming keeps RPC for sequences in one/both directions with deadlines/cancel/backpressure. Streaming requires bounded buffers and retry/resume semantics.<br>
**Required trade-offs:** Fewer connections/low latency vs lifecycle/debug/load balancer complexity.<br>
**Follow-up ladder:** Ordering? Half close?<br>
**Red flags:** Retry whole bidi stream không duplicate.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-003 — `FOUNDATION`
**Question:** GraphQL schema, query, mutation và resolver là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Client-selected graph and field execution.<br>
**Answer outline:** Schema defines types/fields; query reads, mutation changes; resolvers fetch fields. One endpoint doesn't remove auth/business boundaries and can create N+1/cost abuse.<br>
**Required trade-offs:** Client flexibility vs server complexity/cache/cost control.<br>
**Follow-up ladder:** Subscription? DataLoader?<br>
**Red flags:** GraphQL tự giải quyết N+1.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-004 — `FOUNDATION`
**Question:** JVM native image khác JIT JVM runtime ở trade-off nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** AOT closed world, startup/memory vs peak/dynamic features.<br>
**Answer outline:** Native image compiles ahead with reachability metadata, fast startup/lower footprint; JIT warms and optimizes profile, supports reflection/dynamic loading naturally. Measure workload/version compatibility.<br>
**Required trade-offs:** Startup/density vs build time/peak throughput/debug ecosystem.<br>
**Follow-up ladder:** CDS/AOT cache?<br>
**Red flags:** Native image luôn dùng ít CPU và chạy nhanh hơn.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-005 — `SENIOR`
**Question:** gRPC deadline/cancellation/status và retry được thiết kế thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** End-to-end budget and idempotency.<br>
**Answer outline:** Propagate deadline, cancel downstream work cooperatively, map typed status/details without leaking internals; retry only configured transient/idempotent calls with budget, handle streaming resume separately.<br>
**Required trade-offs:** Aggressive deadline controls tail but false failures/duplicate risk.<br>
**Follow-up ladder:** Metadata/trailers?<br>
**Red flags:** UNAVAILABLE luôn retry vô hạn.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-006 — `SENIOR`
**Question:** GraphQL authorization và query-cost/resource exhaustion được chặn thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Field/object auth, depth/complexity and batching.<br>
**Answer outline:** Authenticate request, authorize per field/object/use case, input DTO allowlist, depth/complexity/alias/batch limits, persisted queries/rate limit and DataLoader; monitor resolver latency/cardinality.<br>
**Required trade-offs:** Flexible arbitrary queries vs predictable capacity/security.<br>
**Follow-up ladder:** Introspection prod?<br>
**Red flags:** Gateway auth một lần bảo vệ mọi resolver.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-007 — `SENIOR`
**Question:** Evolve Protobuf/GraphQL schema backward compatible thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Field numbers/nullability/deprecation and mixed clients.<br>
**Answer outline:** Protobuf never reuse field numbers/names carelessly, add optional compatible fields; GraphQL add fields, deprecate then measure usage before remove, avoid semantic changes; contract tests and rollout.<br>
**Required trade-offs:** Long compatibility window retains schema debt.<br>
**Follow-up ladder:** Enum evolution? Required input?<br>
**Red flags:** Generated code compile nghĩa wire compatible.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-008 — `SENIOR`
**Question:** Native-image compatibility issue với reflection/proxy/resources được chẩn đoán thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Closed-world reachability and framework generated metadata.<br>
**Answer outline:** Reproduce exact build/runtime, inspect missing class/resource/proxy/serialization metadata, prefer framework AOT hints/generated config over broad include; test security/serialization and compare JVM mode.<br>
**Required trade-offs:** Broad metadata fixes quickly but bloats image/hides unsupported dynamic behavior.<br>
**Follow-up ladder:** JNI? Agents?<br>
**Red flags:** Thêm all reflection config là production fix tốt.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-009 — `ARCHITECT`
**Question:** Khi nào các P3 stack này đủ giá trị để vào roadmap chính?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Target role/workload evidence and total ecosystem cost.<br>
**Answer outline:** Require measured need: internal high-QPS typed RPC/streaming, client-driven graph, or startup/density constraint; compare simpler REST/JVM alternatives, team skills, observability/security/deploy and exit path. Otherwise time-box learn only.<br>
**Required trade-offs:** Specialization can win constraints but fragments platform.<br>
**Follow-up ladder:** Proof-of-concept gate?<br>
**Red flags:** Thêm stack để thể hiện seniority.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-010 — `EXPERT`
**Question:** Một platform dùng REST, gRPC, GraphQL và native image đồng thời: giảm protocol/runtime sprawl thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Capability ownership, golden paths and migration evidence.<br>
**Answer outline:** Inventory consumers/use cases/SLO/cost, designate defaults and narrow exceptions, shared auth/telemetry/contracts, retire redundant gateways/protocols via compatibility plan; benchmark native only selected workloads.<br>
**Required trade-offs:** Consolidation lowers ops/cognitive cost but migration/client impact.<br>
**Follow-up ladder:** Protocol translation? Org boundaries?<br>
**Red flags:** Chuẩn hóa cưỡng bức một protocol không xét consumers.<br>
**Evidence:** Theory `NOT CREATED`; case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi target role chứng minh cần P3, tạo owner item trước rồi mới link theory/case/evidence; không đổi/reuse stable IDs.
