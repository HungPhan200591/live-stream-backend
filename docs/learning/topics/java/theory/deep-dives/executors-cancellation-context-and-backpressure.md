# Executor Saturation, Cancellation, Context and Backpressure

> Type: `DEEP_DIVE`<br>
> Domain: `java`<br>
> Target depth: `D3 — fault/load test saturation, cancellation leak và context propagation trên async path`<br>
> Teaching readiness: `TEACHABLE_DRAFT`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Executors, CompletableFuture and Concurrency Control](../core/executors-completablefuture-and-concurrency-control.md)<br>
> Related cases: [`RECONNECT-UC-01`](../../../../use-case-catalog.md#reconnect-uc-01), [`LIVE-UC-01`](../../../../use-case-catalog.md#live-uc-01)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 0. Cách học file này

Đặt một timeline cho request: queue wait, run, remote wait, response deadline và cleanup. Tô phần work còn sống sau caller; đó là cancellation leak. Sau đó dùng capacity thật của DB/remote làm admission bound và đo recovery sau burst.

## 1. Learning objectives

1. Lập concurrency/queue/deadline budget từ arrival rate, service time và downstream limits.
2. Phân tích cancellation/deadline khi work đã tạo side effect hoặc API không interruptible.
3. Giữ đúng trace, security context, MDC và transaction context khi công việc đi qua ranh giới bất đồng bộ.

## 2. Mental model do người dạy cung cấp

Concurrency là số work đang nợ hệ thống; queue là phần chưa bắt đầu, in-flight là phần đang giữ resource. Deadline thuộc toàn request, timeout từng hop chỉ tiêu một phần ngân sách. Caller ngừng chờ không tự hủy queued/running/remote work. Context cũng là dữ liệu phải capture tối thiểu, propagate và clear tại boundary.

```mermaid
flowchart TB
    R["Deadline của request"] --> Q["Thời gian chờ queue"]
    Q --> W["Công việc đang chạy"]
    W --> D["Side effect ở remote"]
    R --> T["Response bị timeout"]
    T --> Z["Zombie work nếu<br/>không cancel/cleanup"]
    D --> Z
    style R fill:#4CAF50,stroke:#fff,stroke-width:2px,color:#fff
    style Q fill:#2196F3,stroke:#fff,stroke-width:2px,color:#fff
    style W fill:#9C27B0,stroke:#fff,stroke-width:2px,color:#fff
    style D fill:#FF9800,stroke:#fff,stroke-width:2px,color:#fff
    style T fill:#607D8B,stroke:#fff,stroke-width:2px,color:#fff
    style Z fill:#F44336,stroke:#fff,stroke-width:2px,color:#fff
```

## 3. Saturation mechanics

Theo Little's Law ở steady state phù hợp, concurrency xấp xỉ throughput nhân latency. Nếu service time tăng hoặc downstream chậm, in-flight tăng; queue chỉ trì hoãn rejection và cộng queue wait vào deadline. Stable design cần admission theo capacity thật, không theo số task caller muốn submit.

Platform-thread pool gồm worker + queue + rejection. Sizing CPU-bound khác blocking. Virtual-thread per task bỏ worker scarcity cho thread object, nhưng admission vẫn phải bound connection, request memory, remote QPS và database transaction. Reactive pipeline cũng không cứu downstream nếu demand/prefetch/buffer không bounded.

Bulkhead chia capacity/blast radius. Quá nhiều bulkhead nhỏ có thể underutilize; một global pool làm workload chậm phá workload khác. Metric cần active/in-flight, queue depth/age, reject, task time, downstream wait và deadline remaining.

## 4. Cancellation/deadline mechanics

Ví dụ request có budget 500 ms, đã mất 120 ms queue/validation thì child không nên nhận timeout 500 ms mới. Nó nhận remaining budget trừ cleanup margin. Nếu timeout sau remote commit nhưng trước response, outcome là ambiguous: API cần idempotency key/status query/reconciliation, không retry mù vì original có thể đã thành công.

Timeout ở caller có thể chỉ dừng chờ. Work đã dequeue/running hoặc remote request có thể tiếp tục và commit side effect. Interruption cooperative: code/library phải check/propagate và restore interrupt status khi không xử lý. Swallow `InterruptedException` phá shutdown/cancellation protocol.

Một deadline end-to-end tốt hơn mỗi hop tự dùng timeout đầy đủ; child nhận remaining budget. Hedging/retry tạo duplicate work nên cần idempotency/cancellation và retry budget. Cancellation sau “point of no return” cần query/reconciliation thay vì giả định rollback.

`CompletableFuture` graph có thể complete exceptionally mà underlying task vẫn chạy. `allOf` failure/timeout policy phải quyết định cancel siblings, collect partial result hay wait cleanup.

## 5. Context mechanics

Một task decorator thường capture trace ID/user ID cần thiết trước submit, set khi chạy và clear trong `finally`. Copy cả authentication token/large request object vào long-lived task vừa tạo security risk vừa giữ memory. Transaction context thread-bound không nên được “copy”; async DB work cần transaction boundary mới rõ ràng.

Thread-local MDC/security/request transaction context không tự theo lambda chạy thread khác. Wrapper/decorator/framework instrumentation cần capture immutable minimum context và clear sau execution để tránh leak/cross-request contamination. Không copy secret/token đầy đủ vào async diagnostic context.

Database transaction thường thread-bound; chạy repository work trong arbitrary async task không “tham gia transaction caller” theo assumption. Boundary phải explicit.

## 6. Failure modes

| Failure | Trigger | Symptom | Root mechanism |
| --- | --- | --- | --- |
| Nợ trong hàng đợi | Tải vào cao hơn tốc độ xử lý kéo dài | p99 còn cao lâu sau khi spike kết thúc | Task cũ phải chờ quá lâu |
| Công việc “zombie” | Caller đã timeout nhưng task vẫn chạy | Tốn tài nguyên hoặc tạo side effect trùng | Cancellation không truyền xuống dưới |
| Retry khuếch đại | Request cũ còn chạy nhưng caller đã retry | Lưu lượng vào nhân lên | Thiếu deadline budget hoặc idempotency |
| Nhiễu từ common pool | Stage mặc định chạy blocking I/O | `CompletableFuture`/parallel stream không liên quan cùng chậm | Shared pool bão hòa |
| Rò context | Không xóa `ThreadLocal` sau task | Log mang nhầm trace hoặc user | Tái sử dụng pooled thread |
| Mất task hoặc treo khi shutdown | Không có owner và thời hạn drain | Deploy bị treo hoặc bỏ task | Lifecycle executor không rõ ràng |

## 7. Experiment implication

1. Dùng stub tạo latency/lỗi ở downstream; tăng tốc độ request tới khi một tài nguyên đầu tiên bão hòa, rồi ghi tuổi task trong queue, số lần reject, p99 và thời gian phục hồi.
2. Cho caller timeout, sau đó kiểm tra công việc downstream hoặc side effect có còn tiếp tục hay không.
3. So sánh bounded pool dùng platform thread, virtual thread kèm semaphore và mô hình async khác dưới cùng số permit downstream.
4. Xác minh trace/security context được truyền đúng rồi được xóa; chèn cancellation và shutdown.
5. Evidence vẫn `NOT RUN`; mọi kết quả dự kiến hiện chỉ là giả thuyết.

## 8. Trade-off matrix

| Option | Capacity control | Cancellation | Context/operability | Complexity |
| --- | --- | --- | --- | --- |
| Async không giới hạn | Không có | Yếu | Bão hòa bị che khuất | Thấp cho tới khi có incident |
| Pool/queue có giới hạn | Giới hạn worker và queue | Interrupt hoặc hợp tác | Metric quen thuộc | Trung bình |
| Virtual thread + semaphore | Permit tài nguyên được khai báo rõ | Hủy theo kiểu hợp tác | Thread dump nhiều nhưng vẫn lần theo được | Trung bình |
| Reactive end-to-end | Operator truyền demand/backpressure | Hủy subscription | Cần kỷ luật instrumentation | Cao |

## 9. Liên hệ case

| Case | Deep implication | Evidence chưa có |
| --- | --- | --- |
| `RECONNECT-UC-01` | Admission/deadline/retry amplification | 30k reconnect lab |
| `LIVE-UC-01` | Concurrency/headroom/downstream budget | Capacity workload |
| `DEPLOY-UC-01` | Drain/shutdown/cancellation | Rolling-deploy rehearsal |

### Vì sao lỗi thường xuất hiện chậm sau khi spike đã hết?

Giả sử executor có 100 worker nhưng queue không giới hạn. Trong một phút, hệ thống nhận 1.000 task/giây trong khi downstream chỉ xử lý được 700 task/giây. Mỗi giây queue tăng thêm 300 task. Khi traffic trở lại 500 task/giây, dashboard request rate trông đã bình thường nhưng hàng chục nghìn task cũ vẫn xếp hàng. Latency p99 tiếp tục tăng, caller timeout rồi retry, làm queue lại phình ra. Đây là chuỗi `quá tải -> nợ queue -> timeout -> retry -> quá tải nặng hơn`, không phải chỉ là “thread chạy chậm”.

Evidence cần nối được bốn mốc: tốc độ arrival, tuổi task lâu nhất trong queue, thời gian chờ permit/connection downstream và số task vẫn chạy sau khi caller đã hủy. Mitigation có thể là queue hữu hạn kèm reject/load shedding, deadline truyền xuống dưới, retry budget và idempotency cho side effect. Tăng số thread mà không tăng capacity downstream chỉ làm nhiều task cùng chờ database hoặc HTTP connection hơn.

## 10. Interview answer outline

Lập budget bằng capacity/Little's Law có điều kiện, nêu queue debt và recovery. Phân biệt timeout, interruption, cancellation và ambiguous side effect. So platform pool, virtual thread + semaphore, reactive bằng cùng downstream permits và metrics; trình bày context capture/clear và shutdown/drain.

## 11. Tóm tắt và learner write-back

- Queue age thường cảnh báo user latency tốt hơn depth đơn lẻ.
- Deadline end-to-end tránh mỗi hop dùng lại toàn budget.
- Timeout có thể để lại zombie work và ambiguous outcome.
- Context phải tối thiểu, explicit và được clear.
- So concurrency model dưới cùng resource limit mới công bằng.

`LEARNER TODO — lập timeline và saturation budget cho RECONNECT-UC-01.`

## 12. Guided self-check

1. **Question:** Queue tăng capacity hay chỉ đổi overload?<br>**Đọc lại nếu bí:** mục 2–3, 6.<br>**Rubric:** service rate không đổi, queue wait/age/memory/recovery debt.<br>**My answer:** `LEARNER TODO`
2. **Question:** Timeout sau side effect xử lý ra sao?<br>**Đọc lại nếu bí:** mục 4 và 6.<br>**Rubric:** ambiguous outcome, idempotency/status query/reconciliation, không retry mù.<br>**My answer:** `LEARNER TODO`
3. **Question:** So các concurrency model bằng metric nào?<br>**Đọc lại nếu bí:** mục 3, 7–8.<br>**Rubric:** same downstream permits, throughput/p99/in-flight/queue age/reject/CPU/memory/recovery.<br>**My answer:** `LEARNER TODO`

## 13. Official references

- [Java SE 21 `ThreadPoolExecutor`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html)
- [Java SE 21 `CompletableFuture`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)
- [Java SE 21 `Future`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Future.html)
- [JEP 444 — Virtual Threads](https://openjdk.org/jeps/444)

## 14. Teach-back checklist

- [ ] Tôi lập concurrency/queue/deadline budget.
- [ ] Tôi không đồng nhất response timeout với work cancellation.
- [ ] Tôi propagate/clear minimum safe context.
- [ ] Tôi thiết kế shutdown/drain/retry/idempotency cùng nhau.
- [ ] Saturation/fault evidence vẫn `NOT RUN`.
