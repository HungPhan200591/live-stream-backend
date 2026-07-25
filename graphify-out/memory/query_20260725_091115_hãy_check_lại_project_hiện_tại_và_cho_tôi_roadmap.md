---
type: "query"
date: "2026-07-25T09:11:15.770368+00:00"
question: "Hãy check lại project hiện tại và cho tôi roadmap chuẩn. Ngoài ra, tôi cũng cần xây dựng bộ rules/workflow/skills và docs để làm việc với AI Agent cho cả quá trình này, bạn cần lên structure/roadmap cho docs, rules/skills này luôn. Refactor hay tái sử dụng đc docs cũ thì càng tốt, nếu ko thì xây mới cũng đc. Tôi chủ yếu dùng codex. Tạo file .md tronng folder /docs giúp tôi."
contributor: "graphify"
outcome: "useful"
source_nodes: ["Implementation Roadmap", "SecurityConfig.java", "SessionCacheService", "StreamService", "LiveStreamBackendApplicationTests.java", "RabbitTemplate"]
---

# Q: Hãy check lại project hiện tại và cho tôi roadmap chuẩn. Ngoài ra, tôi cũng cần xây dựng bộ rules/workflow/skills và docs để làm việc với AI Agent cho cả quá trình này, bạn cần lên structure/roadmap cho docs, rules/skills này luôn. Refactor hay tái sử dụng đc docs cũ thì càng tốt, nếu ko thì xây mới cũng đc. Tôi chủ yếu dùng codex. Tạo file .md tronng folder /docs giúp tôi.

## Answer

Expanded from original query via graph vocab: [roadmap, implementation, stream, security, session, redis, rabbit, webhook, test, cache, authentication, wallet]. Traversal confirms implemented clusters around authentication/session, stream lifecycle, Redis cache, simulation wallet and webhook, while RabbitMQ remains primarily a connectivity surface and testing is a single context smoke test. The resulting plan prioritizes security/test stabilization, then Java/concurrency/transaction/database, Redis, RabbitMQ/Kafka reliability, observability, replica/partition experiments, and microservice extraction last. Documentation was separated into current-state evidence, canonical Senior roadmap, AI-agent operating model, and a reusable learning-case template.

## Outcome

- Signal: useful

## Source Nodes

- Implementation Roadmap
- SecurityConfig.java
- SessionCacheService
- StreamService
- LiveStreamBackendApplicationTests.java
- RabbitTemplate