# Prototype Architecture

The prototype is a modular monolith plus a Vite web app. Frontend routes call the Spring Boot API through `/api/*`. The backend is wired to PostgreSQL, Redis, RabbitMQ, and Mailpit, but AI/RAG/tool/price integrations return deterministic seed-backed responses.

## Runtime Flow

1. User enters requirements in the web app.
2. API parses requirements through mock LLM service.
3. API returns seed Build recommendations and tool evidence.
4. User can save quotes, request part-change comparison, register target-price alerts, or create AS tickets.
5. Admin screens inspect agent sessions, tool invocations, price jobs, and tickets.

## Constraints

- Desktop-only first viewport.
- No real payment, shipping, custom remote control, exact FPS guarantee, or lowest-price guarantee.
- PC Agent is a local Python CLI skeleton that generates JSONL samples for AS upload tests.
