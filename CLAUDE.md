# AgriSync - Claude Code System Directives

## Quick AI Guidelines

1. **Protocol**:
   - Read `.context/PROGRESS.md` at start of conversation.
   - Read `.context/CORE_RULES.md` before writing code.
   - Consult `.context/ARCHITECTURE.md` for directory layout and routes.
   - Update `.context/PROGRESS.md` at end of work.

2. **Core Constraints**:
   - **Offline-First**: Room DB is the single source of truth; Compose observes Room `Flow`.
   - **Clean Architecture**: Domain (pure Kotlin) -> Data (Room, Retrofit) -> Presentation (Compose M3, ViewModel).
   - **Bright Data DCA**: Asynchronous batch collection (`/dca/trigger`, `/dca/dataset`) with exponential backoff.
   - **Self-Healing**: Upstream fixes via `bdata scraper heal`; Collector ID `c_*` remains unchanged.
   - **Security**: No secrets in repo. Use `BuildConfig` from `.env` / `.env.example` placeholders.
   - **Build**: Use `gradle` commands, never `./gradlew`.
