# Database Interview Question Bank — Backup, Restore, PITR and Failover

> Status: `DRAFT`<br>
> Domain owner: `Disaster Recovery`<br>
> Active slice: `NONE`; preview target: `DR-01`<br>
> Related roadmap: [Stage 9](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-9---primaryreplica-partitioning-và-data-lifecycle)<br>
> Related depth rubric: [Data operations](../../../knowledge-depth-rubric.md#319-data-operations-và-lifecycle--p1-target-d2-d3)<br>
> Related theory: [Core theory](../theory/core/backup-restore-pitr-and-failover.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DR-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `DB-DR-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### DB-DR-001 — `FOUNDATION`
**Question:** Backup, replication và archive khác nhau; vì sao replica không phải backup?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Independent recovery copies vs live redundancy.<br>
**Answer outline:** Replica mirrors changes including corruption/delete and shares credentials/failure; backup is retained independent recovery point; archive serves long-term lifecycle, not necessarily transactional restore.<br>
**Required trade-offs:** More copies improve recovery but cost/storage/security.<br>
**Follow-up ladder:** Snapshots? Immutable backup?<br>
**Red flags:** Hot standby thay thế mọi backup.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-DR-002 — `FOUNDATION`
**Question:** RPO và RTO là gì và ảnh hưởng thiết kế thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Acceptable data loss and recovery time.<br>
**Answer outline:** RPO bounds lost data point; RTO bounds service restoration. They drive backup/WAL frequency, topology, automation, people/runbook and cost, defined per business capability.<br>
**Required trade-offs:** Lower RPO/RTO costs more and can reduce normal performance.<br>
**Follow-up ladder:** MTTR? Service tiers?<br>
**Red flags:** RPO zero/RTO zero chỉ cần thêm replica.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-DR-003 — `FOUNDATION`
**Question:** Logical backup, physical/base backup và WAL archive khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Portability/granularity vs exact cluster recovery.<br>
**Answer outline:** Logical dumps export objects/data portable/selective but slower; physical copies cluster files/version-specific for faster large restore; WAL archive plus base enables PITR to timestamp/LSN.<br>
**Required trade-offs:** Flexible logical restore vs speed/fidelity.<br>
**Follow-up ladder:** Incremental backup?<br>
**Red flags:** WAL files một mình đủ restore.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-DR-004 — `FOUNDATION`
**Question:** PITR hoạt động ở mức khái niệm và cần artifact nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Base backup plus continuous WAL and target.<br>
**Answer outline:** Restore compatible base backup, replay complete WAL chain to target time/LSN/transaction then promote; timeline/history and retention/integrity matter.<br>
**Required trade-offs:** Fine recovery reduces loss but WAL storage/operations grow.<br>
**Follow-up ladder:** Restore point? Timeline?<br>
**Red flags:** Chọn timestamp chính xác luôn loại bỏ bad transaction.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-DR-005 — `SENIOR`
**Question:** Một restore rehearsal đáng tin cần procedure/evidence gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Isolated environment, timing and invariant verification.<br>
**Answer outline:** Restore exact artifacts into isolated network, record start/end and missing WAL, run schema/count/checksum/domain invariant and app smoke tests, rotate credentials and document actual RPO/RTO.<br>
**Required trade-offs:** Full rehearsal consumes infra/time but backup-success metric không đủ.<br>
**Follow-up ladder:** Encrypted keys? DNS isolation?<br>
**Red flags:** Database starts là restore pass.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-DR-006 — `SENIOR`
**Question:** Accidental delete được PITR trong khi production vẫn ghi: recovery path nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Target-time discovery and selective merge/cutover.<br>
**Answer outline:** Contain delete path, identify target via audit/WAL, PITR separate cluster before event, extract/reconcile affected rows or controlled full cutover; handle dependent events/cache/search and audit repair.<br>
**Required trade-offs:** Selective restore minimizes downtime but consistency merge hard.<br>
**Follow-up ladder:** Foreign keys? Sequence values?<br>
**Red flags:** Restore backup đè thẳng production nhanh nhất.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-DR-007 — `SENIOR`
**Question:** Backup encryption, access và retention bảo vệ/rủi ro gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Confidentiality, key recoverability and deletion policy.<br>
**Answer outline:** Encrypt transit/at rest with separately protected/rotated keys, least access/audit, immutable copies, retention/legal/privacy policy and secure deletion; test key availability during disaster.<br>
**Required trade-offs:** Isolation reduces breach but lost key makes backup useless.<br>
**Follow-up ladder:** Ransomware account separation?<br>
**Red flags:** Same admin credential cho DB và backup tiện hơn.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-DR-008 — `SENIOR`
**Question:** Failover/failback runbook cần kiểm tra application dependencies nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** DNS/pools, broker/cache, jobs and old primary fencing.<br>
**Answer outline:** Declare authority, fence old primary, promote/check data, update discovery/credentials, recycle pools, resume jobs/consumers, verify writes/invariants/lag; failback is planned rebuild, not reverse switch.<br>
**Required trade-offs:** Automation speeds RTO but unsafe assumptions amplify corruption.<br>
**Follow-up ladder:** Scheduled jobs duplicate?<br>
**Red flags:** DB endpoint đổi là toàn hệ thống phục hồi.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-DR-009 — `ARCHITECT`
**Question:** Thiết kế DR theo failure domains và cost ra sao?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Region/zone/account separation and business tiers.<br>
**Answer outline:** Map threats, tier data/services by RPO/RTO, choose replica + immutable cross-domain backups/WAL, dependency restore order, regular drills and evidence; include people/key/control-plane failures.<br>
**Required trade-offs:** Geographic isolation/copies raise cost/compliance complexity.<br>
**Follow-up ladder:** Cold/warm/hot standby?<br>
**Red flags:** Multi-region automatically means DR complete.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-DR-010 — `EXPERT`
**Question:** Backup chain thiếu/corrupt trong incident: triage và communicate thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Evidence preservation, alternative recovery and honest loss bound.<br>
**Answer outline:** Stop overwrite/retention, inventory bases/WAL/checksums/replicas, find latest recoverable point, test isolated partial recovery, quantify data gap and reconcile external sources; communicate uncertainty and preserve forensics.<br>
**Required trade-offs:** Fast partial service vs risk of inconsistent state.<br>
**Follow-up ladder:** Broker replay as source? Manual reconstruction?<br>
**Red flags:** Improvise on production without testing because RTO urgent.<br>
**Evidence:** Theory [Core](../theory/core/backup-restore-pitr-and-failover.md); case `DR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DR-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.
