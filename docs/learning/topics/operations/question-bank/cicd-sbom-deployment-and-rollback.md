# Operations Interview Question Bank — CI/CD, SBOM, Deployment and Rollback

> Status: `DRAFT`<br>
> Domain owner: `Delivery Engineering`<br>
> Active slice: `NONE`; preview target: `OPS-01`<br>
> Related roadmap: [Stage 8](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-8---observability-testing-runtime-và-delivery-engineering)<br>
> Related depth rubric: [Build/CI/CD](../../../knowledge-depth-rubric.md#318-git-linux-container-build-và-cicd--p1-target-d2-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/operations/theory/core/cicd-sbom-deployment-and-rollback.md`<br>
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
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-002 — `FOUNDATION`
**Question:** Artifact immutability và build reproducibility quan trọng vì sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** What was tested equals what runs.<br>
**Answer outline:** Pin toolchain/dependencies/base digests, generate checksum/provenance, build once and promote; environment config external. Rebuild for production can drift from tested artifact.<br>
**Required trade-offs:** Strict pinning needs update automation/security response.<br>
**Follow-up ladder:** Snapshot dependency? Maven wrapper?<br>
**Red flags:** Same Git commit guarantees identical binary.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-003 — `FOUNDATION`
**Question:** Rolling, blue-green và canary deployment khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Traffic coexistence, rollback speed và capacity.<br>
**Answer outline:** Rolling replaces instances gradually; blue-green keeps two full environments for switch; canary exposes small traffic/cohorts and evaluates metrics. All require mixed-version compatibility.<br>
**Required trade-offs:** Capacity/cost vs blast-radius/rollback speed.<br>
**Follow-up ladder:** Feature flags? Stateful sessions?<br>
**Red flags:** Canary là deploy một pod rồi chờ.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-004 — `FOUNDATION`
**Question:** SBOM, provenance và artifact signing trả lời câu hỏi gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Contents, origin/build process và integrity.<br>
**Answer outline:** SBOM lists components/versions; provenance attests source/build; signatures verify artifact identity/integrity. They support triage, not prove vulnerability absence or runtime safety.<br>
**Required trade-offs:** More metadata and key management increase pipeline complexity.<br>
**Follow-up ladder:** SLSA? Keyless signing?<br>
**Red flags:** Signed vulnerable artifact becomes secure.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-005 — `SENIOR`
**Question:** Thiết kế CI quality gates theo change type và risk thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Compile/test/security/schema/contract gates và fast feedback.<br>
**Answer outline:** Fast deterministic compile/unit first, targeted integration/contract/migration/security scans based on paths/risk, then broader nightly/load; block critical findings, time-bound exceptions with owner.<br>
**Required trade-offs:** Strict gates reduce incidents but slow feedback; parallel/cache carefully.<br>
**Follow-up ladder:** Flaky test policy? Monorepo?<br>
**Red flags:** Run full suite serially cho mọi commit là an toàn nhất.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-006 — `SENIOR`
**Question:** Rollback application khi schema/event đã evolve cần chuẩn bị gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Backward compatibility and roll-forward.<br>
**Answer outline:** Expand-contract lets old/new coexist; event readers tolerant/versioned; rollback code only if it can read new writes, otherwise roll-forward fix/feature disable. Rehearse and define data repair.<br>
**Required trade-offs:** Fast binary rollback vs irreversible data semantics.<br>
**Follow-up ladder:** Down migration? Dual write?<br>
**Red flags:** `kubectl rollout undo` luôn khôi phục hệ thống.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-007 — `SENIOR`
**Question:** Canary analysis chọn metrics và decision window thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** User SLI, saturation, business invariant and cohort.<br>
**Answer outline:** Compare error/latency/saturation plus domain failures against baseline by version/cohort, enough traffic/window; automate halt/rollback with guardrails and account for low-volume endpoints.<br>
**Required trade-offs:** Long window detects rare issue but slows release.<br>
**Follow-up ladder:** Sequential testing? Dark traffic?<br>
**Red flags:** CPU bình thường nghĩa canary healthy.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-008 — `SENIOR`
**Question:** Secret và untrusted pull request trong CI tạo threat gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Fork code execution, token scope và dependency poisoning.<br>
**Answer outline:** Do not expose prod secrets to untrusted jobs; least-privileged short-lived tokens, isolated runners, protected environments/approvals, pinned actions/plugins and egress/artifact controls.<br>
**Required trade-offs:** Isolation costs compute/maintenance but protects supply chain.<br>
**Follow-up ladder:** Cache poisoning? OIDC workload identity?<br>
**Red flags:** Masking log string làm secret an toàn.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-009 — `ARCHITECT`
**Question:** Thiết kế deployment governance nhiều service/team mà không tạo central bottleneck thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Golden paths, policy-as-code và service ownership.<br>
**Answer outline:** Platform supplies templates/provenance/security minimum and progressive delivery; service owns SLO/contract/rollback/runbook; policy exceptions audited and metrics track lead time/change failure.<br>
**Required trade-offs:** Standardization vs autonomy/special workload.<br>
**Follow-up ladder:** Environment promotion? Database owner?<br>
**Red flags:** Một central team bấm deploy cho mọi service.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-DELIVERY-010 — `EXPERT`
**Question:** Bad deploy gây partial data corruption trước khi canary rollback: điều hành recovery thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Containment, version/data scope and auditable repair.<br>
**Answer outline:** Stop traffic/feature, identify affected version/time/commands, preserve evidence, rollback only compatible app, run idempotent reconciliation/compensation, verify invariant and communicate; add detection/gate/action owner.<br>
**Required trade-offs:** Fast availability recovery vs correctness investigation.<br>
**Follow-up ladder:** Replay events? Customer remediation?<br>
**Red flags:** Rollback binary tự sửa dữ liệu đã ghi.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `OPS-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

