# Operations Interview Question Bank — CI/CD, SBOM, Deployment and Rollback

> Status: `DRAFT`<br>
> Domain owner: `Delivery Engineering`<br>
> Active slice: `NONE`; preview target: `OPS-01`<br>
> Related roadmap: [Stage 8](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-8---observability-testing-runtime-và-delivery-engineering)<br>
> Related depth rubric: [Build/CI/CD](../../../knowledge-depth-rubric.md#318-git-linux-container-build-và-cicd--p1-target-d2-d3)<br>
> Related theory: [Core](../theory/core/cicd-sbom-deployment-and-rollback.md) · [Deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `OPS-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `OPS-DELIVERY-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### OPS-DELIVERY-001 — `FOUNDATION`
**Question:** Continuous integration, delivery và deployment khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Merge validation, releasable artifact và automatic production.<br>
**Answer outline:** CI integrates/tests often; continuous delivery keeps artifact deployable with manual production decision; deployment automatically releases passing changes. Same immutable artifact promoted across environments.<br>
**Required trade-offs:** Automation speed vs control/compliance gates.<br>
**Follow-up ladder:** Trunk-based? Release train?<br>
**Red flags:** Có Jenkins pipeline là continuous deployment.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-002 — `FOUNDATION`
**Question:** Artifact immutability và build reproducibility quan trọng vì sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** What was tested equals what runs.<br>
**Answer outline:** Pin toolchain, dependency và base-image digest; tạo checksum/provenance; build một lần rồi promote cùng artifact; config môi trường để bên ngoài. Rebuild riêng cho production có thể lệch artifact đã test.<br>
**Required trade-offs:** Strict pinning needs update automation/security response.<br>
**Follow-up ladder:** Snapshot dependency? Maven wrapper?<br>
**Red flags:** Same Git commit guarantees identical binary.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-003 — `FOUNDATION`
**Question:** Rolling, blue-green và canary deployment khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Traffic coexistence, rollback speed và capacity.<br>
**Answer outline:** Rolling thay instance dần; blue-green giữ hai môi trường đầy đủ để chuyển traffic; canary chỉ mở cho một phần traffic/cohort rồi đánh giá metric. Cả ba đều cần mixed-version compatibility.<br>
**Required trade-offs:** Capacity/cost vs blast-radius/rollback speed.<br>
**Follow-up ladder:** Feature flags? Stateful sessions?<br>
**Red flags:** Canary là deploy một pod rồi chờ.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-004 — `FOUNDATION`
**Question:** SBOM, provenance và artifact signing trả lời câu hỏi gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Contents, origin/build process và integrity.<br>
**Answer outline:** SBOM liệt kê component/version; provenance chứng thực nguồn và quy trình build; chữ ký xác minh identity/integrity của artifact. Chúng hỗ trợ triage, không chứng minh hệ thống không có vulnerability hoặc an toàn ở runtime.<br>
**Required trade-offs:** More metadata and key management increase pipeline complexity.<br>
**Follow-up ladder:** SLSA? Keyless signing?<br>
**Red flags:** Signed vulnerable artifact becomes secure.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-005 — `SENIOR`
**Question:** Thiết kế CI quality gates theo change type và risk thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Compile/test/security/schema/contract gates và fast feedback.<br>
**Answer outline:** Chạy compile/unit tất định và nhanh trước; chọn integration/contract/migration/security scan theo path và risk; chạy suite rộng hoặc load theo lịch. Chặn finding critical; exception phải có owner và thời hạn.<br>
**Required trade-offs:** Strict gates reduce incidents but slow feedback; parallel/cache carefully.<br>
**Follow-up ladder:** Flaky test policy? Monorepo?<br>
**Red flags:** Run full suite serially cho mọi commit là an toàn nhất.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-006 — `SENIOR`
**Question:** Rollback application khi schema/event đã evolve cần chuẩn bị gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Backward compatibility and roll-forward.<br>
**Answer outline:** Expand-contract cho bản cũ/mới cùng tồn tại; event reader tolerant hoặc có version. Chỉ rollback code nếu bản cũ đọc được dữ liệu mới; nếu không phải roll-forward hoặc tắt feature. Diễn tập và định nghĩa data repair.<br>
**Required trade-offs:** Fast binary rollback vs irreversible data semantics.<br>
**Follow-up ladder:** Down migration? Dual write?<br>
**Red flags:** `kubectl rollout undo` luôn khôi phục hệ thống.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-007 — `SENIOR`
**Question:** Canary analysis chọn metrics và decision window thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** User SLI, saturation, business invariant and cohort.<br>
**Answer outline:** So error/latency/saturation và domain failure với baseline theo version/cohort, trên đủ traffic và cửa sổ thời gian; tự động halt/rollback có guardrail, đồng thời xử lý endpoint ít traffic.<br>
**Required trade-offs:** Long window detects rare issue but slows release.<br>
**Follow-up ladder:** Sequential testing? Dark traffic?<br>
**Red flags:** CPU bình thường nghĩa canary healthy.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-008 — `SENIOR`
**Question:** Secret và untrusted pull request trong CI tạo threat gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Fork code execution, token scope và dependency poisoning.<br>
**Answer outline:** Không đưa production secret cho job không tin cậy; dùng token ngắn hạn least-privilege, runner cô lập, environment/approval được bảo vệ, action/plugin đã pin và control egress/artifact.<br>
**Required trade-offs:** Isolation costs compute/maintenance but protects supply chain.<br>
**Follow-up ladder:** Cache poisoning? OIDC workload identity?<br>
**Red flags:** Masking log string làm secret an toàn.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-009 — `ARCHITECT`
**Question:** Thiết kế deployment governance nhiều service/team mà không tạo central bottleneck thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Golden paths, policy-as-code và service ownership.<br>
**Answer outline:** Platform cung cấp template, provenance, security minimum và progressive delivery; service sở hữu SLO, contract, rollback và runbook; policy exception được audit, metric theo dõi lead time và change failure.<br>
**Required trade-offs:** Standardization vs autonomy/special workload.<br>
**Follow-up ladder:** Environment promotion? Database owner?<br>
**Red flags:** Một central team bấm deploy cho mọi service.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-010 — `EXPERT`
**Question:** Bad deploy gây partial data corruption trước khi canary rollback: điều hành recovery thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Containment, version/data scope and auditable repair.<br>
**Answer outline:** Dừng traffic/feature, xác định version/thời gian/command bị ảnh hưởng, giữ evidence; chỉ rollback app tương thích; chạy reconciliation/compensation idempotent, verify invariant và communication; bổ sung detection, gate và action owner.<br>
**Required trade-offs:** Fast availability recovery vs correctness investigation.<br>
**Follow-up ladder:** Replay events? Customer remediation?<br>
**Red flags:** Rollback binary tự sửa dữ liệu đã ghi.<br>
**Evidence:** Theory [core](../theory/core/cicd-sbom-deployment-and-rollback.md) + [deep-dive](../theory/deep-dives/mixed-version-rollout-supply-chain-and-data-corruption-recovery.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `OPS-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.
