# Cloud Interview Question Bank — Kubernetes, Managed Services and IaC

> Status: `DRAFT`<br>
> Domain owner: `Cloud/Kubernetes`<br>
> Active slice: `NONE`; preview target: `CLOUD-01`<br>
> Related roadmap: [Stage 11 extensions](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-11---solution-architecture-capstones)<br>
> Related depth rubric: [Cloud/Kubernetes/IaC](../../../knowledge-depth-rubric.md#321-cloud-kubernetes-và-iac--p2-target-d1-d2)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/cloud/theory/core/kubernetes-managed-services-and-iac.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `CLOUD-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `CLOUD-K8S-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### CLOUD-K8S-001 — `FOUNDATION`
**Question:** Pod, Deployment, Service và Ingress/Gateway có vai trò gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Workload unit, reconciliation, discovery and edge routing.<br>
**Answer outline:** Pod hosts containers; Deployment manages replica rollout; Service stable discovery/LB to pods; Ingress/Gateway routes external HTTP/TLS by controller. These don't define app correctness.<br>
**Required trade-offs:** Abstractions automate ops but add control-plane/network complexity.<br>
**Follow-up ladder:** StatefulSet? EndpointSlice?<br>
**Red flags:** Pod là durable VM có stable IP.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CLOUD-K8S-002 — `FOUNDATION`
**Question:** ConfigMap, Secret và workload identity khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Non-secret config, sensitive values and credential-less auth.<br>
**Answer outline:** ConfigMap for config; Secret API is sensitive distribution but needs encryption/RBAC/rotation; workload identity issues short-lived cloud/service credentials without static keys.<br>
**Required trade-offs:** Managed identity reduces secret sprawl but couples platform/IAM.<br>
**Follow-up ladder:** CSI secret store?<br>
**Red flags:** Base64 Kubernetes Secret là encrypted.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CLOUD-K8S-003 — `FOUNDATION`
**Question:** HPA, VPA và cluster autoscaler scale các tầng nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Replicas, resource sizing and nodes.<br>
**Answer outline:** HPA changes pod replicas from metrics; VPA recommends/sets requests often with restart; cluster autoscaler adds/removes nodes for pending capacity. App/bottleneck/state must support scale.<br>
**Required trade-offs:** Autoscaling saves cost but lags bursts and can amplify dependencies.<br>
**Follow-up ladder:** KEDA? Scale to zero?<br>
**Red flags:** HPA CPU giải quyết mọi overload.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CLOUD-K8S-004 — `FOUNDATION`
**Question:** IaC state, plan và drift là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Desired infrastructure, tracked identity and out-of-band change.<br>
**Answer outline:** IaC code/provider computes plan against state/real resources; drift is manual/external divergence. Remote locked/encrypted state, review plans and controlled imports.<br>
**Required trade-offs:** Automation/repeatability vs state/provider/version risk.<br>
**Follow-up ladder:** Terraform module? Policy as code?<br>
**Red flags:** Git file alone là source of truth dù state mất.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CLOUD-K8S-005 — `SENIOR`
**Question:** Chọn managed database/broker/cache hay self-managed theo tiêu chí nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Team skill, SLO, control, compliance and TCO.<br>
**Answer outline:** Compare HA/backup/patching/scaling/support, performance features/limits, data residency, lock-in/egress, staffing and incident ownership. Benchmark critical semantics.<br>
**Required trade-offs:** Managed reduces toil but not schema/query/consumer responsibility.<br>
**Follow-up ladder:** Shared responsibility? Exit plan?<br>
**Red flags:** Managed service không cần runbook.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CLOUD-K8S-006 — `SENIOR`
**Question:** Thiết kế requests/limits/probes/PDB/rollout cho Spring Boot workload thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Scheduling, JVM behavior and availability during disruption.<br>
**Answer outline:** Measure startup/resource/load, set realistic requests/headroom/limits, separate probes, graceful drain, maxUnavailable/surge and PDB with node/zone capacity; test throttling/eviction.<br>
**Required trade-offs:** Strong PDB protects availability but can block maintenance.<br>
**Follow-up ladder:** Topology spread? PreStop?<br>
**Red flags:** replicas=3 tự bảo đảm HA.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CLOUD-K8S-007 — `SENIOR`
**Question:** IaC pipeline bảo vệ destructive change và secret thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Plan review, policy, identity and staged apply.<br>
**Answer outline:** Short-lived CI identity, protected state, pinned providers/modules, lint/security/policy checks, human approval for prod/destruction, staged environments and backup/rollback/restore plan; redact plan secrets.<br>
**Required trade-offs:** Controls slow changes but prevent broad blast radius.<br>
**Follow-up ladder:** Sentinel/OPA? `prevent_destroy`?<br>
**Red flags:** Auto-approve main branch mọi resource.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CLOUD-K8S-008 — `SENIOR`
**Question:** Multi-zone/region cloud design cần test failure/control-plane dependency gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Traffic/data failover and regional service limits.<br>
**Answer outline:** Map zonal/regional services, data replication/RPO, DNS/GSLB, quotas/egress and identity/control-plane; inject zone loss and rehearse region recovery rather than trust SLA multiplication.<br>
**Required trade-offs:** More regions cost/complex consistency/operations.<br>
**Follow-up ladder:** Cell architecture?<br>
**Red flags:** Deploy ở hai region là active-active hoàn chỉnh.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CLOUD-K8S-009 — `ARCHITECT`
**Question:** Quyết định Kubernetes hay simpler PaaS/VM cho team nhỏ thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Workload variety, scale, platform capability and opportunity cost.<br>
**Answer outline:** Use PaaS/managed container when few standard services; Kubernetes when scheduling/extensibility/multi-team platform needs justify dedicated expertise. Include security/upgrades/on-call/TCO and exit path.<br>
**Required trade-offs:** K8s flexibility/portability vs platform tax.<br>
**Follow-up ladder:** Serverless? Compliance?<br>
**Red flags:** Senior architecture luôn cần Kubernetes.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CLOUD-K8S-010 — `EXPERT`
**Question:** Cluster autoscaling và retry storm làm database collapse: điều tra/điều khiển thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Delayed feedback loops across app/platform/dependency.<br>
**Answer outline:** Correlate HPA/pod starts/readiness, retry QPS, pool connections and DB saturation; cap per-pod/global concurrency, retry budget, scale rate, warmup and DB admission; shed traffic and recover gradually.<br>
**Required trade-offs:** Slower scaling/rejection protects DB but lowers immediate success.<br>
**Follow-up ladder:** Scale-from-zero storm?<br>
**Red flags:** Tăng max nodes và DB connections đồng thời.<br>
**Evidence:** Theory `NOT CREATED`; case `CLOUD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `CLOUD-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.
