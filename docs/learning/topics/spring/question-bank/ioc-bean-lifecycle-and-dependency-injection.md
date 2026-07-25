# Spring Interview Question Bank — IoC, Bean Lifecycle and Dependency Injection

> Status: `DRAFT`<br>
> Domain owner: `Spring Framework`<br>
> Active slice: `NONE`; preview target `SPR-01`<br>
> Framework baseline: `Spring Boot 3.4` current project; re-check when active<br>
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)<br>
> Related depth rubric: [Spring](../../../knowledge-depth-rubric.md#35-spring-framework-và-spring-boot--p0-target-d3)<br>
> Related theory: [Spring IoC, Bean Lifecycle và Dependency Injection](../theory/core/ioc-bean-lifecycle-and-dependency-injection.md), [bean creation deep-dive](../theory/deep-dives/bean-creation-scopes-cycles-and-startup-conditions.md) — `TEACHABLE_DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `SPR-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Slice | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| IoC/lifecycle core | 4 | 2 | 0 | 0 |
| Failure/context | 0 | 2 | 1 | 1 |
| **Tổng** | **4** | **4** | **1** | **1** |

## Recommended practice order

1. First pass: `SPR-IOC-001` đến `SPR-IOC-006`.
2. Senior follow-up: `SPR-IOC-007`, `SPR-IOC-008`.
3. Project application: `SPR-IOC-008`.
4. Stretch: `SPR-IOC-009`, `SPR-IOC-010`.

## Questions

### SPR-IOC-001 — `FOUNDATION`
**Question:** IoC và Dependency Injection là gì?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — Spring interview foundation.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Container owns construction/wiring/lifecycle, DI là mechanism.<br>
**Answer outline:** IoC chuyển control tạo/lifecycle sang container; DI cung cấp dependencies thay vì class tự lookup/new; business class phụ thuộc contract và test được.<br>
**Required trade-offs:** Container giảm wiring boilerplate nhưng hidden graph/startup complexity.<br>
**Follow-up ladder:** Service locator? BeanFactory vs ApplicationContext?<br>
**Red flags:** DI chỉ là `@Autowired`.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-IOC-002 — `FOUNDATION`
**Question:** Constructor, setter và field injection khác nhau; vì sao ưu tiên constructor?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — DI question phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Required dependency, immutability, testability và optional config.<br>
**Answer outline:** Constructor makes required deps explicit/final and allows plain tests; setter for truly optional/reconfigurable; field hides contract/reflection and hampers test. Too many constructor args signal cohesion issue.<br>
**Required trade-offs:** Constructor cycles fail visibly; setter can break cycle but often masks design flaw.<br>
**Follow-up ladder:** Multiple constructors? Lombok RequiredArgsConstructor?<br>
**Red flags:** Field injection ngắn hơn nên tốt hơn.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-IOC-003 — `FOUNDATION`
**Question:** `@Component`, `@Service`, `@Repository`, `@Controller` khác nhau thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — stereotype question rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Semantic specialization và infrastructure behavior.<br>
**Answer outline:** Đều stereotype/component candidates; specialized annotations diễn đạt layer, controller participates MVC, repository supports persistence exception translation. Annotation không tự đảm bảo architecture.<br>
**Required trade-offs:** Stereotypes improve intent/tooling but need package/module rules/tests.<br>
**Follow-up ladder:** `@RestController`? Custom stereotype? Proxy?<br>
**Red flags:** `@Service` tự mở transaction.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-IOC-004 — `FOUNDATION`
**Question:** Singleton, prototype, request và session scopes khác nhau thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — bean-scope question phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Container instance lifecycle và thread safety.<br>
**Answer outline:** Singleton one bean per context; prototype new per lookup/injection creation; web scopes per request/session via scoped proxy as needed. Singleton service must not hold mutable request state.<br>
**Required trade-offs:** Narrow scopes isolate state but add lifecycle/proxy/serialization complexity.<br>
**Follow-up ladder:** Prototype destruction? Singleton injecting request bean?<br>
**Red flags:** Singleton nghĩa là one instance toàn JVM.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-IOC-005 — `SENIOR`
**Question:** Bean lifecycle từ instantiate tới destroy có những extension points chính nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — Spring internals common.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Dependency population, aware/post-processors, init/destroy và proxy timing.<br>
**Answer outline:** Definitions discovered, instantiate/inject, aware callbacks, BeanPostProcessor before/after init, init callbacks, usable/proxied bean, destroy callbacks on context close. Exact early behavior matters for proxies/cycles.<br>
**Required trade-offs:** Lifecycle hooks powerful but hidden ordering; prefer explicit collaboration when possible.<br>
**Follow-up ladder:** `@PostConstruct`? SmartInitializingSingleton? Lazy?<br>
**Red flags:** Constructor runs after all proxies/post-processors.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-IOC-006 — `SENIOR`
**Question:** Component scanning và `@Bean` configuration khác nhau; khi nào dùng mỗi cách?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — configuration question phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Ownership, third-party objects, explicit wiring và package boundary.<br>
**Answer outline:** Scan discovers owned stereotypes; `@Bean` explicitly constructs third-party/config-sensitive objects. Keep scan roots narrow; explicit names/qualifiers for multiple candidates; configuration class semantics matter.<br>
**Required trade-offs:** Scanning convenient but accidental beans; explicit config verbose but auditable.<br>
**Follow-up ladder:** Lite/full configuration? `proxyBeanMethods`? Qualifier?<br>
**Red flags:** Scan entire classpath để khỏi thiếu bean.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-IOC-007 — `SENIOR`
**Question:** Circular dependency phát sinh vì sao và sửa thế nào thay vì dùng lazy bừa bãi?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — design/startup follow-up.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Responsibility cycle, constructor-cycle fail và refactoring seam.<br>
**Answer outline:** A↔B thường signal boundary/cohesion sai; extract third service/domain event/interface direction, remove bidirectional orchestration. `@Lazy`/setter only deliberate bridge with documented risk.<br>
**Required trade-offs:** Event decouples compile dependency nhưng adds async/consistency complexity.<br>
**Follow-up ladder:** Early reference? Transactions/proxies in cycle?<br>
**Red flags:** Enable circular references globally.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-IOC-008 — `SENIOR`
**Question:** Configuration properties, validation và auto-configuration conditions nên được debug thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — Boot startup application.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Typed binding, fail-fast và condition report.<br>
**Answer outline:** Use typed `@ConfigurationProperties` + validation, no secret defaults; inspect property sources/condition evaluation report/bean list; test profile context presence/absence and fail-fast production config.<br>
**Required trade-offs:** Flexible defaults improve dev UX but unsafe defaults expand production risk.<br>
**Follow-up ladder:** Precedence? Relaxed binding? `@ConditionalOnMissingBean`?<br>
**Red flags:** Add fallback secret to make startup pass.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-IOC-009 — `ARCHITECT`
**Question:** Tổ chức multiple application contexts/modules để giảm accidental coupling thế nào?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — architecture stretch.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Bean visibility, ownership, startup/cycle isolation.<br>
**Answer outline:** Prefer module package boundaries/explicit exports and architecture tests; child contexts only when lifecycle/isolation need; minimize shared mutable singleton/config leakage.<br>
**Required trade-offs:** Multiple contexts isolate but complicate events/properties/debugging.<br>
**Follow-up ladder:** Parent lookup? Test slice? Plugin?<br>
**Red flags:** Một giant scan/context là modular architecture.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-IOC-010 — `EXPERT`
**Question:** BeanPostProcessor/proxy/early-reference ordering có thể làm bean injected khác final exposed bean thế nào?<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — Spring internals discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Early singleton exposure, proxy identity và lifecycle evidence.<br>
**Answer outline:** Cycles may expose early reference before full post-processing; later proxy can differ, losing advice/identity. Reproduce minimal context, inspect bean/proxy class and lifecycle logs; refactor cycle rather than depend on internal order.<br>
**Required trade-offs:** Framework extension hooks enable infrastructure but raise upgrade fragility.<br>
**Follow-up ladder:** SmartInstantiationAwareBPP? AOP proxy? `@Async`?<br>
**Red flags:** Every injected reference always same raw instance.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SPR-01` active: create ApplicationContext tests/condition report and actual startup evidence. Stable IDs không tái sử dụng.
