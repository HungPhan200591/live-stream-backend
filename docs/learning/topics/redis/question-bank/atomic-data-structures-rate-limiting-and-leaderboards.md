# Ngân hàng câu hỏi phỏng vấn Redis — cấu trúc dữ liệu nguyên tử, rate limiting và leaderboard

> Status: `DRAFT`<br>
> Domain owner: `Redis`<br>
> Active slice: `NONE`; preview target: `RED-01`<br>
> Related roadmap: [Stage 4](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-4---redis-as-a-distributed-data-structure)<br>
> Related depth rubric: [Redis](../../../knowledge-depth-rubric.md#315-redis--p1-target-d3)<br>
> Related theory: [Core theory](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) · [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md)<br>
> Updated: `2026-07-26`

Bản xem trước; không kích hoạt hoặc triển khai `RED-01`. Khả năng xuất hiện chỉ là ước lượng. Mọi câu vẫn `UNANSWERED`, kiểm thử `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `RED-DS-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### RED-DS-001 — `FOUNDATION`
**Question:** String, Hash, Set, Sorted Set, List và Stream phù hợp access pattern nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Hiểu ngữ nghĩa từng cấu trúc dữ liệu thay vì coi Redis là một map chung.<br>
**Answer outline:** String cho scalar, blob và counter; Hash cho các field; Set bảo đảm duy nhất; ZSET sắp theo score; List làm queue đơn giản; Stream là log append có consumer group. Chọn theo operation, cardinality và TTL.<br>
**Required trade-offs:** Cấu trúc chuyên biệt nhanh nhưng có memory model và cách migration khác nhau.<br>
**Follow-up ladder:** Bitmap/HLL? Per-field TTL?<br>
**Red flags:** Cho rằng mọi value đều nên serialize thành JSON String.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-002 — `FOUNDATION`
**Question:** Lệnh Redis atomic ở phạm vi nào; pipeline và transaction khác Lua thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Tính nguyên tử của một command, batching và ghép thao tác phía server.<br>
**Answer outline:** Một command là nguyên tử trên event loop; pipeline chỉ giảm RTT, không nguyên tử. `MULTI/EXEC` nhóm command nhưng không rollback như DB. Lua/function chạy nguyên tử nhưng chặn server nếu quá lâu.<br>
**Required trade-offs:** Tính nguyên tử phía server giảm race nhưng script dài làm blast radius của latency lớn hơn.<br>
**Follow-up ladder:** WATCH optimistic transaction?<br>
**Red flags:** MULTI rollback từng command khi lỗi.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-003 — `FOUNDATION`
**Question:** Token bucket, fixed window và sliding window rate limit khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Burst, tính công bằng, memory và cập nhật nguyên tử.<br>
**Answer outline:** Fixed window đơn giản nhưng boundary burst; sliding log chính xác tốn memory; sliding counter xấp xỉ; token bucket cho burst có refill rate. Key scope và atomic script bắt buộc.<br>
**Required trade-offs:** Tính công bằng và độ chính xác đổi lấy chi phí.<br>
**Follow-up ladder:** Leaky bucket? 429 headers?<br>
**Red flags:** Cho rằng `INCR` rồi `EXPIRE` bằng hai lệnh luôn không có race.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-004 — `FOUNDATION`
**Question:** Sorted Set xây leaderboard và range/rank như thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Thứ tự score, cách xử lý tie và ngữ nghĩa update.<br>
**Answer outline:** `ZADD` ghi member–score, `ZREVRANGE`/`ZRANK` đọc hạng; member là duy nhất và ghi lại sẽ đổi score. Thứ tự khi tie cần contract và tie-breaker ngoài score hoặc cách mã hóa ghép được kiểm soát.<br>
**Required trade-offs:** Xếp hạng real-time nhanh nhưng nguồn sự thật, rebuild và audit phải rõ.<br>
**Follow-up ladder:** Top N per period? Floating score?<br>
**Red flags:** ZSET là durable ledger.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-005 — `SENIOR`
**Question:** Viết atomic Lua rate limiter cần bảo vệ các edge case nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Thời gian trong key, TTL, giới hạn số và cluster slot.<br>
**Answer outline:** Validate input, dùng thời gian server Redis khi cần, update cùng expiry nguyên tử và trả remaining/reset. Giới hạn key/cardinality, timeout script và dùng hash tag cho thao tác nhiều key trên Cluster.<br>
**Required trade-offs:** Lua chính xác nhưng script dài chặn shard và khó debug.<br>
**Follow-up ladder:** EVALSHA/functions? Clock skew?<br>
**Red flags:** Lua có thể gọi network/DB bên trong.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-006 — `SENIOR`
**Question:** Đếm unique/current viewers bằng HLL và ZSET heartbeat khác nhau thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Phân biệt cardinality xấp xỉ với membership có cửa sổ thời gian.<br>
**Answer outline:** HLL dùng ít memory để ước lượng số phần tử duy nhất nhưng không liệt kê member. ZSET với `score=lastSeen` biểu diễn cửa sổ active, cần cleanup và capacity cho hot key. Số đếm nghiệp vụ durable vẫn thuộc owner phù hợp.<br>
**Required trade-offs:** Độ chính xác và khả năng liệt kê đổi lấy memory và write rate.<br>
**Follow-up ladder:** PFCOUNT error? Shard/merge?<br>
**Red flags:** HLL trả danh sách user unique.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-007 — `SENIOR`
**Question:** Hot key trên leaderboard/viewer set gây vấn đề gì và giảm tải ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** CPU/network trên một shard, large key và fan-out.<br>
**Answer outline:** Đo operation, byte và latency theo key; chỉ local-cache/read-replica cho dữ liệu đọc an toàn; shard/bucket rồi chấp nhận chi phí merge; batch update, giới hạn kết quả và tránh delete lớn gây block.<br>
**Required trade-offs:** Sharding tăng throughput nhưng mất cách tính tổng/hạng nguyên tử đơn giản.<br>
**Follow-up ladder:** UNLINK/SCAN? Celebrity key?<br>
**Red flags:** Thêm node tự shard một key.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-008 — `SENIOR`
**Question:** Version/serialization migration cho Redis structures khi rolling deploy thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Tương thích reader cũ/mới và khả năng rebuild.<br>
**Answer outline:** Đặt version cho namespace/payload, dual-read có deadline hoặc rebuild từ PostgreSQL/event log, cho key cũ hết TTL và đo fallback. Không đổi encoding tại chỗ nếu chưa có kế hoạch.<br>
**Required trade-offs:** Cold rebuild/load vs dual-format complexity.<br>
**Follow-up ladder:** Cluster key tags? Serializer allowlist?<br>
**Red flags:** FLUSHALL rồi deploy là migration.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-009 — `ARCHITECT`
**Question:** Thiết kế Redis cluster topology và data ownership theo failure domain thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Shard/replica, consistency và chế độ hạ cấp.<br>
**Answer outline:** Phân loại cache, coordination tạm thời và trạng thái rate limit; xem phân bố/cardinality key, ngữ nghĩa replica/failover, persistence/RPO khi cần. Mỗi feature có nguồn, rebuild và outage policy riêng.<br>
**Required trade-offs:** Availability/latency đổi lấy consistency và chi phí vận hành.<br>
**Follow-up ladder:** Cluster vs Sentinel? Multi-region?<br>
**Red flags:** Một Redis chung cho cache, locks và security state không isolation.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-010 — `EXPERT`
**Question:** Failover làm lock/rate-limit state mất hoặc duplicate; bảo vệ invariant thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Cửa sổ replication bất đồng bộ, fencing và an toàn nghiệp vụ.<br>
**Answer outline:** Primary ACK trước replica có thể mất write gần nhất khi promote. Rate limit cần policy fail-open/closed; lock critical cần invariant DB hoặc fencing token từ owner monotonic durable. Tiêm failover và đo cửa sổ mất state.<br>
**Required trade-offs:** Safety mạnh hơn làm giảm availability và tăng latency.<br>
**Follow-up ladder:** WAIT command? Split brain?<br>
**Red flags:** Cho rằng Redis replication tự tạo linearizability.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md) + [Deep-dive](../theory/deep-dives/lua-atomicity-rate-limiter-and-leaderboard-hotkeys.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RED-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.
