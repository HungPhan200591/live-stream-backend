# Platform Interview Question Bank — gRPC, GraphQL, Native Image and Platform-Specific Stack

> Status: `DRAFT`<br>
> Domain owner: `Conditional Platform Extensions`<br>
> Active slice: `NONE`; preview target: `CONDITIONAL_P3`<br>
> Related roadmap: [P3 activation rule](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#62-backlog-catalog-map-theo-primary-stage)<br>
> Related depth rubric: [P3 platform stack](../../../knowledge-depth-rubric.md#324-grpc-graphql-native-image-và-platform-specific-stack--p3-target-d1)<br>
> Related theory: [Core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) · [Deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md)<br>
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
**Answer outline:** gRPC dùng IDL có kiểu, stub sinh tự động, binary frame và HTTP/2 streaming; REST/JSON phổ biến, dễ debug/cache/browser và tận dụng HTTP semantics. Chọn theo client, network và contract.<br>
**Required trade-offs:** Efficiency/type safety vs compatibility/tooling/human readability.<br>
**Follow-up ladder:** gRPC-Web?<br>
**Red flags:** Binary protocol tự động nhanh hơn trong mọi workload.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-002 — `FOUNDATION`
**Question:** Unary, server/client và bidirectional streaming gRPC khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Message direction/lifecycle and flow control.<br>
**Answer outline:** Unary có một request/response; streaming giữ RPC cho chuỗi dữ liệu một hoặc hai chiều với deadline, cancellation và backpressure. Streaming cần buffer hữu hạn và semantics retry/resume.<br>
**Required trade-offs:** Fewer connections/low latency vs lifecycle/debug/load balancer complexity.<br>
**Follow-up ladder:** Ordering? Half close?<br>
**Red flags:** Retry whole bidi stream không duplicate.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-003 — `FOUNDATION`
**Question:** GraphQL schema, query, mutation và resolver là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Client-selected graph and field execution.<br>
**Answer outline:** Schema định nghĩa type/field; query dùng đọc, mutation dùng thay đổi; resolver lấy dữ liệu cho field. Một endpoint không loại bỏ auth/business boundary và có thể tạo N+1 hoặc cost abuse.<br>
**Required trade-offs:** Client flexibility vs server complexity/cache/cost control.<br>
**Follow-up ladder:** Subscription? DataLoader?<br>
**Red flags:** GraphQL tự giải quyết N+1.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-004 — `FOUNDATION`
**Question:** JVM native image khác JIT JVM runtime ở trade-off nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** AOT closed world, startup/memory vs peak/dynamic features.<br>
**Answer outline:** Native image compile ahead-of-time bằng reachability metadata, startup nhanh và footprint thấp; JIT warm-up rồi tối ưu theo profile, hỗ trợ reflection/dynamic loading tự nhiên hơn. Phải đo workload và compatibility theo version.<br>
**Required trade-offs:** Startup/density vs build time/peak throughput/debug ecosystem.<br>
**Follow-up ladder:** CDS/AOT cache?<br>
**Red flags:** Native image luôn dùng ít CPU và chạy nhanh hơn.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-005 — `SENIOR`
**Question:** gRPC deadline/cancellation/status và retry được thiết kế thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Deadline budget end-to-end và idempotency.<br>
**Answer outline:** Truyền deadline, hủy downstream theo kiểu hợp tác, map status/detail có kiểu mà không lộ internals; chỉ retry call transient/idempotent đã cấu hình trong budget, xử lý streaming resume riêng.<br>
**Required trade-offs:** Aggressive deadline controls tail but false failures/duplicate risk.<br>
**Follow-up ladder:** Metadata/trailers?<br>
**Red flags:** UNAVAILABLE luôn retry vô hạn.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-006 — `SENIOR`
**Question:** GraphQL authorization và query-cost/resource exhaustion được chặn thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Field/object auth, depth/complexity and batching.<br>
**Answer outline:** Authenticate request, authorize theo field/object/use case; allowlist input DTO; giới hạn depth/complexity/alias/batch; dùng persisted query, rate limit và DataLoader; theo dõi latency/cardinality của resolver.<br>
**Required trade-offs:** Flexible arbitrary queries vs predictable capacity/security.<br>
**Follow-up ladder:** Introspection prod?<br>
**Red flags:** Gateway auth một lần bảo vệ mọi resolver.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-007 — `SENIOR`
**Question:** Evolve Protobuf/GraphQL schema backward compatible thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Field numbers/nullability/deprecation and mixed clients.<br>
**Answer outline:** Với Protobuf, không tái sử dụng field number/name tùy tiện; thêm field optional tương thích. Với GraphQL, thêm field, deprecate rồi đo usage trước khi xóa; tránh đổi semantics. Dùng contract test và rollout theo version.<br>
**Required trade-offs:** Long compatibility window retains schema debt.<br>
**Follow-up ladder:** Enum evolution? Required input?<br>
**Red flags:** Generated code compile nghĩa wire compatible.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-008 — `SENIOR`
**Question:** Native-image compatibility issue với reflection/proxy/resources được chẩn đoán thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Closed-world reachability and framework generated metadata.<br>
**Answer outline:** Tái hiện đúng build/runtime; kiểm class, resource, proxy hoặc serialization metadata bị thiếu; ưu tiên AOT hint/config do framework sinh thay vì include quá rộng; test security/serialization và so với JVM mode.<br>
**Required trade-offs:** Broad metadata fixes quickly but bloats image/hides unsupported dynamic behavior.<br>
**Follow-up ladder:** JNI? Agents?<br>
**Red flags:** Thêm all reflection config là production fix tốt.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-009 — `ARCHITECT`
**Question:** Khi nào các P3 stack này đủ giá trị để vào roadmap chính?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Target role/workload evidence and total ecosystem cost.<br>
**Answer outline:** Yêu cầu nhu cầu đã đo: typed RPC/streaming nội bộ QPS cao, graph do client chọn, hoặc constraint startup/density. So với REST/JVM đơn giản hơn, kỹ năng team, observability/security/deploy và exit path; nếu chưa đủ thì chỉ time-box việc học thử.<br>
**Required trade-offs:** Specialization can win constraints but fragments platform.<br>
**Follow-up ladder:** Proof-of-concept gate?<br>
**Red flags:** Thêm stack để thể hiện seniority.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### PLAT-EXT-010 — `EXPERT`
**Question:** Một platform dùng REST, gRPC, GraphQL và native image đồng thời: giảm protocol/runtime sprawl thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Capability ownership, golden paths and migration evidence.<br>
**Answer outline:** Kiểm kê consumer/use case/SLO/cost; chọn default và exception hẹp; chuẩn hóa auth/telemetry/contract; loại gateway/protocol thừa qua kế hoạch compatibility; chỉ benchmark native cho workload đã chọn.<br>
**Required trade-offs:** Consolidation lowers ops/cognitive cost but migration/client impact.<br>
**Follow-up ladder:** Protocol translation? Org boundaries?<br>
**Red flags:** Chuẩn hóa cưỡng bức một protocol không xét consumers.<br>
**Evidence:** Theory [core](../theory/core/grpc-graphql-native-image-and-platform-specific-stack.md) + [deep-dive](../theory/deep-dives/protocol-sprawl-stream-recovery-and-native-image-compatibility.md); case `NOT APPLICABLE`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi target role chứng minh cần P3, tạo owner item trước rồi mới link theory/case/evidence; không đổi/reuse stable IDs.
