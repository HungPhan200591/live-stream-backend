# Graph Report - D:\Study\Project\live-stream-backend  (2026-07-25)

## Corpus Check
- Corpus is ~40,368 words - fits in a single context window. You may not need a graph.

## Summary
- 551 nodes · 1163 edges · 26 communities (22 shown, 4 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 91 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Stream Lifecycle
- Authentication Service
- Users and Roles
- Session Management
- API Error Handling
- Agent Docs Contracts
- Security Filter Chain
- User Profiles
- Implementation Roadmap
- Redis Serialization
- Simulation Wallet
- Infrastructure Test Endpoints
- Webhook Lifecycle
- Maven Wrapper
- AI Learning Prompts
- Stream Persistence Model
- Security Configuration Docs
- OpenAPI Configuration
- SQL Logging Formatter
- Application Bootstrap
- Smoke Tests
- Redis Usage Docs
- End Stream Simulation DTO
- Start Stream Simulation DTO
- Data Initialization
- Maven Project Metadata

## God Nodes (most connected - your core abstractions)
1. `ApiResponse` - 40 edges
2. `UserSession` - 25 edges
3. `StreamService` - 24 edges
4. `StreamDTO` - 22 edges
5. `UserService` - 19 edges
6. `User` - 18 edges
7. `UserRepository` - 18 edges
8. `AuthService` - 18 edges
9. `UserSessionRepository` - 16 edges
10. `SessionCacheDTO` - 15 edges

## Surprising Connections (you probably didn't know these)
- `Diagnose Livestream Backend Skill` --references--> `Repository Guidance`  [EXTRACTED]
  .agents/skills/diagnose-livestream-backend/SKILL.md → AGENTS.md
- `Repository Guidance` --references--> `Risk-Based Execution Plan`  [EXTRACTED]
  AGENTS.md → PLANS.md
- `Implement Livestream Feature Skill` --references--> `Risk-Based Execution Plan`  [EXTRACTED]
  .agents/skills/implement-livestream-feature/SKILL.md → PLANS.md
- `JWT Security Configuration` --conceptually_related_to--> `Security Belongs in the Flow`  [INFERRED]
  src/main/resources/application.yml → docs/usage/security_best_practices.md
- `PostgreSQL Redis and RabbitMQ Configuration` --shares_data_with--> `Purpose-Based One-Time Action Tokens`  [INFERRED]
  src/main/resources/application.yml → docs/usage/security_best_practices.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Business-First Document Chain** — docs_business_flows_business_flows, docs_api_endpoints_specification_api_contract, docs_api_roadmap_api_implementation_roadmap [EXTRACTED 1.00]
- **Session Security Model** — docs_authorization_flow_session_backed_jwt, docs_authorization_flow_action_token, docs_api_endpoints_specification_two_tier_authorization [INFERRED 0.85]
- **Implementation Phase Dependency Chain** — docs_implementation_phase_4_streaming_phase_4_streaming, docs_implementation_phase_6_realtime_chat_phase_6_realtime_chat, docs_implementation_phase_7_gifts_phase_7_gifts, docs_implementation_phase_8_analytics_phase_8_analytics [EXTRACTED 1.00]
- **Local PostgreSQL and Observability Tooling** — docs_implementation_phase_1_foundation_phase_1_foundation, docs_codex_mcp_postgres_mcp_postgresql_for_codex, docs_usage_p6spy_sql_logging_p6spy_sql_logging [INFERRED 0.85]
- **Risk-Adaptive Security Flow** — docs_usage_security_best_practices_risk_tiered_security, docs_usage_security_best_practices_secure_flow, docs_usage_security_best_practices_purpose_based_action_tokens [EXTRACTED 1.00]

## Communities (26 total, 4 thin omitted)

### Community 0 - "Stream Lifecycle"
Cohesion: 0.06
Nodes (31): ResponseStatus, ResourceNotFoundException, GetMapping, GetMapping, HttpServletRequest, Operation, PostMapping, PreAuthorize (+23 more)

### Community 1 - "Authentication Service"
Cohesion: 0.07
Nodes (39): Authentication, AuthController, HttpServletRequest, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController (+31 more)

### Community 2 - "Users and Roles"
Cohesion: 0.06
Nodes (45): ConditionalOnProperty, JpaRepository, PostConstruct, DataInitializer, Component, PasswordEncoder, RequiredArgsConstructor, Slf4j (+37 more)

### Community 3 - "Session Management"
Cohesion: 0.07
Nodes (29): Modifying, PrePersist, Query, Scheduled, AllArgsConstructor, Builder, Data, Entity (+21 more)

### Community 4 - "API Error Handling"
Cohesion: 0.13
Nodes (18): AccessDeniedException, ExceptionHandler, MethodArgumentNotValidException, NoResourceFoundException, RestControllerAdvice, ApiResponse, AllArgsConstructor, Data (+10 more)

### Community 5 - "Agent Docs Contracts"
Cohesion: 0.10
Nodes (30): Diagnose Skill Interface, Diagnose Livestream Backend Skill, Implement Feature Skill Interface, Implement Livestream Feature Skill, Manage Local Port Skill Interface, Manage Local Port Skill, Refine Prompt Skill Interface, Refine Engineering Prompt Skill (+22 more)

### Community 6 - "Security Filter Chain"
Cohesion: 0.13
Nodes (21): AuthenticationConfiguration, DaoAuthenticationProvider, EnableMethodSecurity, EnableWebSecurity, FilterChain, HttpSecurity, HttpServletResponse, OncePerRequestFilter (+13 more)

### Community 7 - "User Profiles"
Cohesion: 0.14
Nodes (20): PutMapping, GetMapping, Operation, PreAuthorize, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+12 more)

### Community 8 - "Implementation Roadmap"
Cohesion: 0.14
Nodes (25): MCP PostgreSQL for Codex, Coding Standards, Explicit ID Data Model, Layered API Pattern, RTMP-driven Stream Lifecycle, Webhooks, Implementation Roadmap, Phase 10 Production Readiness (+17 more)

### Community 9 - "Redis Serialization"
Cohesion: 0.17
Nodes (16): JsonIgnore, JsonIgnoreProperties, JsonTypeInfo, JsonTypeName, ObjectMapper, RedisConnectionFactory, Bean, Configuration (+8 more)

### Community 10 - "Simulation Wallet"
Cohesion: 0.18
Nodes (15): Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag, SimulationController, Data (+7 more)

### Community 11 - "Infrastructure Test Endpoints"
Cohesion: 0.26
Nodes (11): JdbcTemplate, RabbitTemplate, GetMapping, Operation, RedisTemplate, RequestMapping, RequiredArgsConstructor, RestController (+3 more)

### Community 12 - "Webhook Lifecycle"
Cohesion: 0.23
Nodes (11): Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Slf4j, Tag, WebhookController (+3 more)

### Community 13 - "Maven Wrapper"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 14 - "AI Learning Prompts"
Cohesion: 0.39
Nodes (8): Active Recall, AI Usage Notes, Durable Learning Through Knowledge Reconstruction, AI Book Tutor Prompt, Four-Step Tutoring Method, Teach-Back and Memory Notes Workflow, COSTAR Prompt Framework, Prompt Guide

### Community 15 - "Stream Persistence Model"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Builder, Data, Entity, NoArgsConstructor, Table, Stream

### Community 16 - "Security Configuration Docs"
Cohesion: 0.43
Nodes (7): Purpose-Based One-Time Action Tokens, Three-Tier Risk-Based Security, Security Belongs in the Flow, Security Best Practices, Live Stream Backend Application Configuration, PostgreSQL Redis and RabbitMQ Configuration, JWT Security Configuration

### Community 17 - "OpenAPI Configuration"
Cohesion: 0.53
Nodes (4): OpenAPI, Bean, Configuration, OpenApiConfig

### Community 18 - "SQL Logging Formatter"
Cohesion: 0.47
Nodes (3): SingleLineFormat, CompactSqlFormatter, Override

### Community 19 - "Application Bootstrap"
Cohesion: 0.60
Nodes (3): EnableScheduling, SpringBootApplication, LiveStreamBackendApplication

### Community 20 - "Smoke Tests"
Cohesion: 0.60
Nodes (3): SpringBootTest, LiveStreamBackendApplicationTests, Test

### Community 21 - "Redis Usage Docs"
Cohesion: 0.67
Nodes (3): Redis Usage Guide, Type-safe Redis Cache, Versioned Cache Keys and TTL

## Knowledge Gaps
- **19 isolated node(s):** `com.stream:demo`, `ROLE_USER`, `ROLE_STREAMER`, `ROLE_ADMIN`, `ACTIVE` (+14 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ApiResponse` connect `API Error Handling` to `Stream Lifecycle`, `Authentication Service`, `User Profiles`, `Simulation Wallet`, `Infrastructure Test Endpoints`, `Webhook Lifecycle`?**
  _High betweenness centrality (0.179) - this node is a cross-community bridge._
- **Why does `AuthService` connect `Authentication Service` to `Users and Roles`, `Session Management`?**
  _High betweenness centrality (0.084) - this node is a cross-community bridge._
- **Why does `UserService` connect `User Profiles` to `Stream Lifecycle`, `Authentication Service`, `Users and Roles`?**
  _High betweenness centrality (0.078) - this node is a cross-community bridge._
- **What connects `com.stream:demo`, `ROLE_USER`, `ROLE_STREAMER` to the rest of the system?**
  _19 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Stream Lifecycle` be split into smaller, more focused modules?**
  _Cohesion score 0.06398390342052314 - nodes in this community are weakly interconnected._
- **Should `Authentication Service` be split into smaller, more focused modules?**
  _Cohesion score 0.06538461538461539 - nodes in this community are weakly interconnected._
- **Should `Users and Roles` be split into smaller, more focused modules?**
  _Cohesion score 0.05961538461538462 - nodes in this community are weakly interconnected._