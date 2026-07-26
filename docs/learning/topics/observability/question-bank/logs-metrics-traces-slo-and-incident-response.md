# Observability Interview Question Bank — Logs, Metrics, Traces, SLO and Incident Response

> Status: `DRAFT`<br>
> Domain owner: `Observability/Reliability`<br>
> Active slice: `NONE`; preview target: `OBS-01`<br>
> Related roadmap: [Stage 8](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-8---observability-testing-runtime-và-delivery-engineering)<br>
> Related depth rubric: [Observability](../../../knowledge-depth-rubric.md#311-observability-reliability-và-incident-response--p0-target-d3)<br>
> Related theory: [Core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) · [Deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `OBS-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `OBS-PROD-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### OBS-PROD-001 — `FOUNDATION`
**Question:** Logs, metrics và traces trả lời các loại câu hỏi nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Event detail, aggregate trends và request causality.<br>
**Answer outline:** Logs cho discrete context; metrics cho bounded aggregate/alert; traces nối spans qua path. Cần correlation và exemplars, không bắt một signal làm mọi việc.<br>
**Required trade-offs:** Telemetry đầy đủ tăng cost/overhead/privacy.<br>
**Follow-up ladder:** Profiles/JFR thuộc đâu?<br>
**Red flags:** Có distributed trace thì không cần logs.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OBS-PROD-002 — `FOUNDATION`
**Question:** Counter, gauge, histogram và timer dùng khi nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Monotonic events, current state và distributions.<br>
**Answer outline:** Counter cho requests/errors; gauge current queue/pool; histogram/timer cho latency/size distributions. Rate tính từ counter; bucket phù hợp SLO.<br>
**Required trade-offs:** Bucket/cardinality chi tiết tăng storage/cost.<br>
**Follow-up ladder:** Summary vs histogram?<br>
**Red flags:** Average timer đủ đại diện latency.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OBS-PROD-003 — `FOUNDATION`
**Question:** SLI, SLO, SLA và error budget khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Measured reliability target vs contract.<br>
**Answer outline:** SLI là measure user outcome; SLO target/window; SLA contractual consequence; error budget là allowed bad events dùng cân bằng release/reliability.<br>
**Required trade-offs:** SLO chặt tăng cost/slow delivery.<br>
**Follow-up ladder:** Availability denominator? Burn rate?<br>
**Red flags:** SLO 100% là mục tiêu tốt.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OBS-PROD-004 — `FOUNDATION`
**Question:** Correlation ID và trace context propagation qua HTTP/async/broker ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Context injection/extraction và lifecycle.<br>
**Answer outline:** Accept/generate safe correlation, W3C trace headers, capture/restore context khi async, inject message headers, clear ThreadLocal/MDC; không trust arbitrary ID for security.<br>
**Required trade-offs:** Propagation tăng debuggability nhưng header/cardinality/PII cần kiểm soát.<br>
**Follow-up ladder:** Baggage? Virtual threads?<br>
**Red flags:** MDC tự theo mọi thread và broker.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OBS-PROD-005 — `SENIOR`
**Question:** Metric cardinality explosion phát sinh và phòng ngừa thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Unbounded labels và telemetry backend saturation.<br>
**Answer outline:** Không label user/request/token/raw URL; normalize route/error class, cap tenant dimensions, sample logs/traces, monitor series count/cost; high-cardinality detail chuyển trace/log protected.<br>
**Required trade-offs:** Aggregation mất drill-down; exemplars bridge signals.<br>
**Follow-up ladder:** Status code label? Dynamic exception?<br>
**Red flags:** Thêm mọi field làm label để debug tốt hơn.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OBS-PROD-006 — `SENIOR`
**Question:** Thiết kế actionable alert cho latency/error/consumer lag thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** User impact, burn rate và runbook.<br>
**Answer outline:** Alert theo tốc độ burn SLO hoặc saturation kéo dài; dùng nhiều cửa sổ để giảm noise. Alert phải có service, symptom, dashboard, runbook và owner; chỉ page khi khẩn cấp và hành động được, còn trend thì tạo ticket.<br>
**Required trade-offs:** Nhạy nhanh vs false positives/on-call fatigue.<br>
**Follow-up ladder:** Queue depth vs age? Synthetic check?<br>
**Red flags:** Alert mỗi exception.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OBS-PROD-007 — `SENIOR`
**Question:** Trace sampling giữ incident evidence mà kiểm soát cost thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Head/tail sampling và error/latency bias.<br>
**Answer outline:** Baseline probabilistic head sampling; tail-based retain errors/high latency/rare routes within resource limits; propagate sampling decision, redact attributes and measure dropped spans.<br>
**Required trade-offs:** Tail sampling richer nhưng needs collector buffering/cost.<br>
**Follow-up ladder:** Parent-based sampling? PII?<br>
**Red flags:** Chỉ sample success, log toàn lỗi là đủ trace.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OBS-PROD-008 — `SENIOR`
**Question:** Dẫn incident từ symptom tới mitigation bằng timeline ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Triage, hypothesis, evidence và communication.<br>
**Answer outline:** Công bố severity và commander; stabilize/contain; đối chiếu deploy, SLI, saturation và dependency; kiểm giả thuyết, cập nhật theo nhịp, recover rồi verify. Giữ timeline và làm blameless postmortem với action có owner.<br>
**Required trade-offs:** Fast mitigation may defer root cause; rollback has data compatibility risk.<br>
**Follow-up ladder:** When stop investigation? Status page?<br>
**Red flags:** Tìm thủ phạm trước khi mitigate.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OBS-PROD-009 — `ARCHITECT`
**Question:** Thiết kế observability platform và cost allocation cho nhiều team thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Standards, ownership, retention và multi-tenancy.<br>
**Answer outline:** Chuẩn hóa semantic convention, SDK và collector; giới hạn label, redact dữ liệu; phân tầng retention/sampling; mỗi service sở hữu dashboard/SLO; đặt ingestion budget và self-service query có access control.<br>
**Required trade-offs:** Central consistency vs team flexibility/vendor cost.<br>
**Follow-up ladder:** Build vs buy? Chargeback?<br>
**Red flags:** Collect everything forever rồi query khi cần.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OBS-PROD-010 — `EXPERT`
**Question:** Telemetry pipeline lỗi đúng lúc incident: quan sát và phục hồi hệ thống thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Observability as dependency and fallback evidence.<br>
**Answer outline:** Có health out-of-band cho collector; giữ log local có bound, JFR và metric runtime/database/broker; sampling hoặc load-shed telemetry, đường dự phòng và gap đã biết. Không để exporter chặn request; dựng lại timeline từ evidence bền vững của hệ thống.<br>
**Required trade-offs:** Fail-open telemetry loses detail; buffering risks disk/memory.<br>
**Follow-up ladder:** Clock skew timeline? Collector backpressure?<br>
**Red flags:** Ứng dụng phải fail nếu trace backend down.<br>
**Evidence:** Theory [core](../theory/core/logs-metrics-traces-slo-and-incident-response.md) + [deep-dive](../theory/deep-dives/telemetry-pipeline-failure-tail-sampling-and-incident-reconstruction.md); case `OBS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `OBS-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.
