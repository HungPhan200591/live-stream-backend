# Database Interview Question Bank — Flyway Baseline and Clean Bootstrap

> Status: `DRAFT`<br>
> Domain owner: `database / schema lifecycle / build reliability`<br>
> Active slice: `NONE`; preview target `MIG-01 — Flyway baseline and clean-database bootstrap`<br>
> Scope boundary: online/zero-downtime expand-contract migration remains owned by `DB-04`<br>
> Related roadmap: [Stage 0](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory), [Stage 3 boundary](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)<br>
> Related depth rubric: [PostgreSQL, SQL and data modeling](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3), [Testing and quality strategy](../../../knowledge-depth-rubric.md#310-testing-và-quality-strategy--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/versioned-schema-migration.md)<br>
> Version snapshot checked: `2026-07-25`; re-check Flyway/Spring Boot behavior when `MIG-01` becomes active<br>
> Updated: `2026-07-26`

Question bank này được chuẩn bị trước cho `MIG-01`. Nó chỉ tạo question ladder/rubric, không cài Flyway, không tạo migration, không baseline database và không chứng minh clean bootstrap. Người học phải trả lời trước khi mở `Answer outline`; mọi test, experiment và interview note giữ `NOT RUN`/`NOT CREATED`.

## Official sources for the snapshot

- [Spring Boot — Database Initialization](https://docs.spring.io/spring-boot/how-to/data-initialization.html)
- [Flyway — Versioned Migrations](https://documentation.red-gate.com/flyway/flyway-concepts/migrations/versioned-migrations)
- [Flyway — Schema History Table and Validation](https://documentation.red-gate.com/flyway/flyway-concepts/migrations/flyway-schema-history-table)
- [Flyway — Clean Command](https://documentation.red-gate.com/flyway/reference/commands/clean)
- [Flyway — Clean Disabled Setting](https://documentation.red-gate.com/fd/flyway-clean-disabled-setting-277578981.html)

## Level rubric

| Level | Trọng tâm |
| --- | --- |
| `FOUNDATION` | Schema ownership, migration vocabulary, history/checksum và clean bootstrap |
| `SENIOR` | Safe baseline, drift/failure diagnosis, initialization ordering và hermetic test |
| `ARCHITECT` | Governance, execution topology, permissions, recovery và environment parity |
| `EXPERT` | Out-of-band drift, partial/non-transactional failure và bounded reconciliation |

Level được gắn trên từng câu hỏi; không tách folder theo level.

## Coverage

| Topic | Foundation | Senior | Architect | Expert | Theory source |
| --- | ---: | ---: | ---: | ---: | --- |
| Schema ownership, history và migration types | 2 | 1 | 1 | 0 | `PLANNED` |
| Baseline existing schema và drift control | 1 | 2 | 0 | 1 | `PLANNED` |
| Clean bootstrap, Spring ordering và test data | 1 | 2 | 1 | 0 | `PLANNED` |
| Execution, recovery và governance | 0 | 1 | 2 | 1 | `PLANNED` |
| **Tổng** | **4** | **6** | **4** | **2** | 16 questions |

## Questions

### DB-FLYWAY-001 — `FOUNDATION`

**Question:**

Vì sao production schema cần một source of truth versioned? Phân biệt Flyway migration với Hibernate `ddl-auto`, `schema.sql` và thao tác DDL thủ công.

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có hiểu ownership/audit/reproducibility thay vì chỉ biết thêm dependency Flyway.

**Answer outline:**

1. Migration versioned là lịch sử thay đổi reviewable, chạy theo thứ tự và có thể tái lập schema từ repository.
2. `ddl-auto` suy schema từ ORM model và không thay thế audited data/schema evolution; `schema.sql` là initializer khác; DDL thủ công tạo state ngoài version control.
3. Chọn một cơ chế sở hữu schema production; ORM nên validate mapping/schema theo policy thay vì âm thầm mutate.

**Required trade-offs:**

- Versioned migration tăng discipline/review cost nhưng mua auditability, repeatability và controlled rollout.

**Follow-up ladder:**

- Foundation: `ddl-auto=validate` chứng minh gì?
- Senior: Hai schema owner cùng chạy gây failure nào?
- Architect: Ai approve migration có destructive DDL?
- Expert: Schema-as-code khác desired-state reconciliation ở điểm nào?

**Red flags:**

- “Flyway tự sinh schema từ entity”; bật `update` cùng Flyway ở production.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-002 — `FOUNDATION`

**Question:**

`flyway_schema_history` lưu vai trò gì? Version, rank, checksum và trạng thái giúp Flyway quyết định migration nào pending hoặc drift như thế nào?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có mental model về database history, không coi filename là toàn bộ state.

**Answer outline:**

1. History table ghi migration đã áp dụng, thứ tự/thời điểm, checksum và success/state để đối chiếu với resolved migrations.
2. Versioned migration có unique version và thường chạy đúng một lần theo version order.
3. Checksum mismatch hoặc migration đã áp dụng nhưng mất khỏi repository là drift cần điều tra, không được bỏ qua tự động.

**Required trade-offs:**

- History table là audit/control metadata nhưng không tự chứng minh schema/data đúng business invariant.

**Follow-up ladder:**

- Foundation: Installed rank khác migration version thế nào?
- Senior: Vì sao backup chỉ schema mà mất history table là nguy hiểm?
- Architect: History table được bảo vệ/monitor như production metadata nào?
- Expert: Multiple locations/branches tạo state `Future` hoặc `Out of Order` ra sao?

**Red flags:**

- Sửa/xóa row history để “cho chạy qua” mà không reconcile actual schema.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-003 — `FOUNDATION`

**Question:**

Phân biệt versioned migration, repeatable migration, `baseline` operation và baseline migration. Khi nào dùng mỗi loại và invariant nào phải giữ?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có phân biệt các khái niệm dễ bị gọi chung là “baseline script”.

**Answer outline:**

1. Versioned migration ghi một bước evolution có unique version, áp dụng một lần; không sửa sau khi đã đi downstream.
2. Repeatable migration chạy lại khi checksum thay đổi, phù hợp object định nghĩa có thể recreate như view/procedure theo dependency policy.
3. `baseline` đánh dấu database hiện hữu ở một version để Flyway nhận ownership từ điểm đó; baseline migration mô tả cumulative initial state cho environment mới theo feature/version đang dùng.
4. Baseline không được giả vờ rằng unknown existing schema đã được kiểm chứng tương đương canonical state.

**Required trade-offs:**

- Cumulative baseline tăng tốc provisioning nhưng cần chứng minh tương đương với full history và không thay audit trail đã deploy.

**Follow-up ladder:**

- Foundation: Vì sao DML data correction thường là versioned?
- Senior: Repeatable view phụ thuộc versioned table change xử lý order thế nào?
- Architect: Khi nào squash history thành baseline mới?
- Expert: Chứng minh semantic equivalence giữa baseline và replay history ra sao?

**Red flags:**

- Dùng repeatable cho destructive data migration; đồng nhất `baseline` command với tạo schema sạch.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-004 — `FOUNDATION`

**Question:**

“Clean-database bootstrap” phải chứng minh điều gì? Vì sao application startup thành công trên database developer đã tồn tại không phải bằng chứng tương đương?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có hiểu from-zero reproducibility và hidden dependency vào state lịch sử.

**Answer outline:**

1. Tạo PostgreSQL/database trống với exact supported version, chạy toàn bộ canonical migrations và startup application không cần DDL thủ công.
2. Verify schema objects, constraints, reference data tối thiểu, Flyway state và ORM validation/critical smoke behavior.
3. Database developer có thể chứa bảng/cột/extension/hotfix ngoài migrations nên startup pass chỉ chứng minh state đó dùng được.

**Required trade-offs:**

- Bootstrap từ sạch tăng CI time nhưng phát hiện missing migration và initialization drift sớm.

**Follow-up ladder:**

- Foundation: Database empty và schema empty có giống nhau không?
- Senior: Testcontainers giúp gì và chưa giúp gì?
- Architect: Bootstrap gate chạy ở PR, main hay release?
- Expert: Extension/role do platform provision làm reproducibility boundary thay đổi thế nào?

**Red flags:**

- Copy database dump không provenance rồi gọi là clean bootstrap; để Hibernate bù migration thiếu.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-005 — `SENIOR`

**Question:**

Bạn đưa một production database không rỗng, chưa có Flyway history vào quản lý bằng Flyway thế nào mà không chạy lại DDL lên object đang tồn tại?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có audit actual state và khóa baseline boundary bằng evidence.

**Answer outline:**

1. Inventory schema/data/extension/role và so với canonical model; xử lý drift trước khi nhận ownership.
2. Chọn baseline version rõ nghĩa, tạo/review initial migration cho clean environments và rehearsal trên clone/backup.
3. Chạy explicit baseline trên đúng target sau approval; verify history/state rồi chỉ cho phép migrations sau baseline.
4. Chụp command/config/database identity và rollback/recovery plan; không bật automation rộng trước khi pilot.

**Required trade-offs:**

- Baseline chấp nhận lịch sử trước đó không nằm trong Flyway; cần snapshot/audit để không biến unknown drift thành canonical.

**Follow-up ladder:**

- Foundation: Baseline version quyết định migration nào bị bỏ qua?
- Senior: Làm sao phát hiện production có hotfix khác staging?
- Architect: Cần approval nào trước baseline?
- Expert: Nhiều tenant ở schema versions khác nhau được onboard ra sao?

**Red flags:**

- Bật `baselineOnMigrate` trên mọi environment rồi chờ nó tự hiểu schema.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-006 — `SENIOR`

**Question:**

`baselineOnMigrate` giảm thao tác onboarding nhưng tạo safety risk nào? Nếu dùng, bạn đặt guardrail gì để tránh baseline nhầm database?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có nhìn automation convenience như một destructive/misrouting risk.

**Answer outline:**

1. Tự baseline schema non-empty chưa có history có thể che wrong target, unknown drift hoặc missing configuration.
2. Mặc định ưu tiên explicit one-time baseline; nếu automation bắt buộc thì scope environment, exact URL/database/schema, identity assertion và approval.
3. Dùng least-privilege credential, dry-run/info/audit, backup/clone rehearsal và fail-fast khi target fingerprint không khớp.
4. Tắt setting sau onboarding nếu không còn use case.

**Required trade-offs:**

- Automation giảm manual error lặp lại nhưng tăng blast radius của một cấu hình sai.

**Follow-up ladder:**

- Foundation: Non-empty schema được xác định theo configured schemas nào?
- Senior: Target fingerprint gồm tín hiệu gì?
- Architect: Secret/credential separation ngăn nhầm environment ra sao?
- Expert: Control nào vẫn hữu hiệu khi DNS/secret mapping cùng sai?

**Red flags:**

- Xem `baselineOnMigrate=true` như cấu hình production vĩnh viễn không cần review.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-007 — `SENIOR`

**Question:**

Flyway báo checksum mismatch cho migration đã apply. Bạn điều tra và xử lý thế nào? Khi nào `repair` hợp lệ và khi nào nó chỉ che drift?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có reconcile repository, history và actual schema trước khi mutate metadata.

**Answer outline:**

1. Dừng rollout; xác định file nào đổi, ai/commit nào, environment nào đã apply và actual schema/data result.
2. Nếu migration downstream đã thành công, khôi phục original file và tạo migration roll-forward mới cho change cần thiết.
3. `repair` chỉ sau khi intentional state đã được xác minh/approved; nó sửa history metadata chứ không tự sửa schema/data.
4. Lưu audit evidence và bổ sung CI guard ngăn sửa migration immutable.

**Required trade-offs:**

- Roll-forward giữ lịch sử trung thực nhưng thêm migration; rewrite chỉ an toàn trước permanent downstream boundary.

**Follow-up ladder:**

- Foundation: Vì sao whitespace/comment change có thể ảnh hưởng checksum?
- Senior: Dev-only applied migration có thể reset không?
- Architect: Branching/version policy ngăn collision thế nào?
- Expert: Reconcile khi nhiều environment có checksum khác nhau ra sao?

**Red flags:**

- Chạy `repair` ngay để pipeline xanh; sửa row checksum bằng SQL thủ công.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-008 — `SENIOR`

**Question:**

Migration fail giữa chừng. Bạn phân biệt transactional rollback hoàn toàn với partial side effect như thế nào và quyết định rerun, repair hay forward-fix ra sao?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có kiểm tra database/statement semantics và actual state, không giả định mọi DDL atomic.

**Answer outline:**

1. Xem Flyway error/history, PostgreSQL transaction boundary, logs và actual objects/data; không suy luận chỉ từ status.
2. Nếu transaction rollback sạch, sửa migration chưa từng downstream rồi rerun theo policy; nếu partial state, viết idempotent cleanup/forward migration hoặc restore theo blast radius.
3. `repair` chỉ reconcile metadata sau khi actual state đúng với intended state.
4. Rehearsal failure path, timeout/lock và permission trước production.

**Required trade-offs:**

- Transactional DDL giảm partial state nhưng long transaction/lock vẫn có operational cost; idempotency tăng script complexity.

**Follow-up ladder:**

- Foundation: Successful statement log có đồng nghĩa committed không?
- Senior: DML backfill cùng DDL nên tách khi nào?
- Architect: Retry policy cho migration process là gì?
- Expert: External side effect từ callback phá transaction model thế nào?

**Red flags:**

- Rerun liên tục; đánh dấu success thủ công trước khi inspect schema/data.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-009 — `SENIOR`

**Question:**

Spring Boot startup có Flyway, JPA/Hibernate, `schema.sql` và `data.sql`. Bạn ngăn multiple initialization mechanisms và ordering race như thế nào?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có hiểu application-context initialization dependency và schema ownership.

**Answer outline:**

1. Chọn Flyway làm schema owner; tắt/không dùng competing schema initialization cho production, đặt Hibernate validate/none theo policy.
2. Spring Boot gọi Flyway migrate trong startup và sắp initializer dependencies, nhưng custom startup bean/data access có thể cần dependency declaration rõ.
3. Test/dev data tách khỏi production migrations bằng location/profile/resource boundary đã kiểm soát.
4. Startup phải fail-fast khi migration/validation fail; không tiếp tục với half-ready schema.

**Required trade-offs:**

- Startup migration đơn giản deployment nhưng kết hợp availability/permission với app lifecycle; pipeline migration tách quyền nhưng cần orchestration.

**Follow-up ladder:**

- Foundation: `ddl-auto=validate` chạy trước hay sau migration cần kỳ vọng gì?
- Senior: Startup bean query DB quá sớm biểu hiện thế nào?
- Architect: Khi nào tách migrator khỏi application?
- Expert: Nhiều instances startup đồng thời tác động migration lock ra sao?

**Red flags:**

- Bật Flyway, `ddl-auto=update` và `schema.sql` để “cái nào chạy được thì chạy”.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-010 — `SENIOR`

**Question:**

Thiết kế hermetic clean-bootstrap test bằng Testcontainers cho Flyway như thế nào? Test data nào thuộc migration và data nào thuộc fixture?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có tách production schema/reference data khỏi scenario fixture và tạo evidence từ database sạch.

**Answer outline:**

1. Start exact PostgreSQL image với database/schema mới, chạy production migrations, verify Flyway validate/history và application context.
2. Assert critical tables, constraints, indexes/reference data và một số persistence/business smoke paths; không chỉ `contextLoads`.
3. Stable reference data cần cho mọi environment có thể là versioned migration; scenario/user data thuộc test fixture hoặc test-only migration không đóng gói production.
4. Test rerun/isolation, locale/timezone/extension/permission assumptions và failure diagnostics.

**Required trade-offs:**

- Test-only migration tiện bootstrap scenario nhưng có thể tạo schema/data path khác production nếu lạm dụng.

**Follow-up ladder:**

- Foundation: Vì sao H2 không chứng minh PostgreSQL migration?
- Senior: Verify constraint behavior hay chỉ metadata?
- Architect: Clean bootstrap chạy bao nhiêu DB versions?
- Expert: Baseline script và full history được differential-test ra sao?

**Red flags:**

- Mount dump mutable; fixture production nằm trong `src/main`; test pass nhờ `ddl-auto=create`.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-011 — `ARCHITECT`

**Question:**

Bạn thiết kế governance cho migration naming/version, review, ownership và merge conflict như thế nào khi nhiều developer cùng thay schema?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có biến schema change thành controlled delivery artifact thay vì file SQL tùy ý.

**Answer outline:**

1. Quy ước unique version/naming, immutable-after-downstream boundary và owner theo data/domain.
2. Review SQL về invariant, lock/scan/data risk, reversibility, privilege, observability và compatibility scope.
3. CI validate naming/checksum/duplicate version, clean bootstrap và migration from supported state.
4. Merge collision được renumber/rebase trước deploy; không bật out-of-order mặc định để né conflict.
5. Emergency path vẫn tạo audited migration/hotfix reconciliation với owner.

**Required trade-offs:**

- Central approval tăng consistency nhưng có thể bottleneck; federated ownership cần automated policy và clear accountability.

**Follow-up ladder:**

- Foundation: Timestamp version giải quyết và không giải quyết gì?
- Senior: Migration đã lên staging rồi branch khác collision xử lý ra sao?
- Architect: CODEOWNERS/data owner boundary nào hợp lý?
- Expert: Monorepo nhiều services cùng schema có governance model nào?

**Red flags:**

- Cho phép sửa migration nếu “chưa lên production” dù đã lên shared downstream; không owner cho destructive DDL.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-012 — `ARCHITECT`

**Question:**

Migration nên chạy trong mỗi application startup hay qua một deployment job/migrator riêng? So sánh concurrency, permissions, availability và rollback ownership.

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có chọn execution topology theo operational constraints.

**Answer outline:**

1. Startup migration đơn giản và bảo đảm app không chạy trước schema, nhưng mọi instance mang DDL privilege và startup phụ thuộc migration lock/time.
2. Dedicated job centralizes privilege/log/approval và chạy trước rollout, nhưng orchestration phải chặn app incompatible và xử lý job failure.
3. Dùng least-privilege tách migrator/application credential khi risk yêu cầu; chỉ một actor thực thi, nhiều app instances quan sát readiness.
4. MIG-01 cần clean bootstrap/ownership foundation; lock-heavy online strategy được đào sâu ở `DB-04`.

**Required trade-offs:**

- Operational simplicity đối đầu privilege separation, controllability và failure isolation.

**Follow-up ladder:**

- Foundation: Flyway lock bảo vệ gì?
- Senior: App instances chờ migration timeout thế nào?
- Architect: Deployment gate nối migrator result với rollout ra sao?
- Expert: Multi-region control plane tránh split migration authority thế nào?

**Red flags:**

- Mọi replica có superuser credential; migration job xanh nhưng app rollout không kiểm tra schema version.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-013 — `ARCHITECT`

**Question:**

Rollback strategy cho migration nền tảng nên ưu tiên database restore, down migration hay roll-forward? Bạn chọn dựa trên data loss, time và compatibility như thế nào?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có phân biệt code rollback với irreversible data/schema transition.

**Answer outline:**

1. Classify additive/destructive/data transform; code rollback chỉ an toàn nếu schema/data vẫn backward-compatible.
2. Down migration có thể không phục hồi dữ liệu đã mất và bản thân cũng rủi ro; roll-forward thường giữ audit/history tốt hơn cho lỗi logic có thể sửa.
3. Restore cần verified backup, restore time và acceptable RPO/RTO; có thể làm mất writes sau backup.
4. Rehearse chosen recovery, define trigger/owner và capture actual timing; advanced zero-downtime compatibility thuộc `DB-04`.

**Required trade-offs:**

- Giữ backward-compatible window tăng schema/code complexity nhưng mua rollback safety.

**Follow-up ladder:**

- Foundation: `DROP COLUMN` rollback bằng add column có phục hồi data không?
- Senior: Data correction sai nên reverse hay compensate?
- Architect: RPO/RTO chọn restore threshold thế nào?
- Expert: Legal/audit constraints làm rollback model thay đổi ra sao?

**Red flags:**

- “Có down script nên rollback được”; backup chưa từng restore-test.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-014 — `ARCHITECT`

**Question:**

Làm sao chứng minh dev/test/staging/prod dùng cùng canonical migration history dù khác data volume, roles, extensions và topology?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có quản environment parity theo declared differences, không giả định schema equality từ filename.

**Answer outline:**

1. Cùng immutable migration artifact/digest và Flyway configuration contract; environment-specific secret/URL không đổi migration semantics tùy tiện.
2. Validate history/checksum/schema version ở deployment; detect out-of-band object/privilege/extension drift.
3. Test clean bootstrap trên exact DB family/version; rehearsal trên representative clone/dataset cho volume/lock risk sau này.
4. Document allowed differences như roles/tablespace/extension provisioning và assign owner ngoài schema migration nếu cần.

**Required trade-offs:**

- Perfect parity đắt/không thực tế; declared-difference model tập trung evidence vào differences có risk.

**Follow-up ladder:**

- Foundation: Cùng schema version có bảo đảm cùng schema không?
- Senior: Extension provisioning thuộc Flyway hay platform?
- Architect: Drift detection chạy khi nào và chặn deploy ra sao?
- Expert: Tenant-specific schema customization được governance thế nào?

**Red flags:**

- Copy SQL thủ công theo environment; production hotfix không back-port vào history.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-015 — `EXPERT`

**Question:**

Production có emergency hotfix DDL ngoài Flyway, staging không có, còn repository chứa migration tương đương nhưng chưa apply. Bạn reconcile ba sự thật này mà không mất audit hoặc chạy DDL hai lần thế nào?

**Target depth:** `D4`

**Interviewer evaluates:**

- Có xử lý out-of-band drift dựa trên actual state, idempotency và controlled metadata repair.

**Answer outline:**

1. Freeze related rollout; capture exact prod/staging schema, hotfix statement/time/owner và repository migration.
2. Xác định semantic equivalence và side effects, không chỉ object name; test reconciliation trên clone.
3. Chọn audited path: migration nhận biết state an toàn, mark/skip execution bằng supported controlled mechanism, hoặc replacement migration; repair metadata chỉ sau state approval.
4. Đưa mọi environment về canonical history/state, giữ incident/change record và thêm emergency procedure bắt buộc back-port.
5. Verify clean bootstrap vẫn tạo cùng final state; không biến production drift thành hidden baseline.

**Required trade-offs:**

- Idempotent/state-aware reconciliation tăng script complexity; metadata-only fix nhanh nhưng có thể hợp thức hóa semantic drift.

**Follow-up ladder:**

- Foundation: Schema diff và migration history diff trả lời hai câu hỏi nào?
- Senior: `IF NOT EXISTS` có đủ chứng minh equivalence không?
- Architect: Emergency DBA access được audit/revoke thế nào?
- Expert: Data side effect không thể reverse được reconcile bằng invariant nào?

**Red flags:**

- Xóa migration pending hoặc insert history row thủ công không rehearsal/audit.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### DB-FLYWAY-016 — `EXPERT`

**Question:**

Một migration gồm transactional PostgreSQL DDL, long-running data update và callback gọi external system; process chết giữa chừng khi nhiều application instances đang startup. Hãy thiết kế bounded recovery model.

**Target depth:** `D4`

**Interviewer evaluates:**

- Có tách atomic boundary, external side effect, lock/readiness và recovery ownership.

**Answer outline:**

1. Không đặt external side effect vào giả định DB transaction; tách schema step, resumable/backfill work và post-commit integration có idempotency/outbox nếu thật sự cần.
2. Một migrator authority giữ lock; application instances fail/wait bounded và chỉ ready khi required schema version đạt.
3. Backfill có checkpoint/batch/idempotent predicate, observability và pause/resume; inspect actual transaction/history before retry.
4. Recovery decision tree phân biệt rolled-back, partial committed và external-effect states; repair chỉ sau reconciliation.
5. MIG-01 chỉ cần nhận diện boundary và clean bootstrap; production expand-contract/backfill rehearsal thuộc `DB-04`.

**Required trade-offs:**

- Tách migration/background work tăng orchestration nhưng giảm lock duration, partial failure và startup blast radius.

**Follow-up ladder:**

- Foundation: External HTTP call có rollback cùng DB transaction không?
- Senior: Backfill resumable cần key/checkpoint gì?
- Architect: Readiness gate biết required schema version từ đâu?
- Expert: Nếu callback đã thành công nhưng DB rollback, compensation/inbox model nào phù hợp?

**Red flags:**

- Retry toàn migration mù; giữ mọi app startup vô hạn; coi callback external là atomic với PostgreSQL.

**Evidence:**

- Theory: [Core](../theory/core/versioned-schema-migration.md)
- Deep-dive: `NOT CREATED`
- Learning case: `MIG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `MIG-01` thực sự active:

1. Pin exact Spring Boot/Flyway/PostgreSQL versions và re-check module, baseline, clean, validation và transaction behavior từ official docs.
2. Tạo theory/deep-dive theo checkpoint rồi thay marker `NOT CREATED` bằng canonical links.
3. Audit current schema/DDL ownership, tạo clean-bootstrap reproducer và chỉ ghi evidence từ output thật.
4. Chuyển lock-heavy, expand-contract, rolling-version và large-data migration questions sang liên kết `DB-04` khi artifact đó tồn tại; không làm phình scope `MIG-01`.
5. Giữ stable question IDs; deprecated question phải trỏ replacement, không tái sử dụng ID.
