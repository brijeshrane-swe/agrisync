# AgriSync AI Assistant Instructions & Project Directives

Welcome to **AgriSync**, an offline-first native Android application powered by Bright Data Scraper Studio self-healing scrapers and Gemini AI reasoning for agricultural commodity market intelligence.

---

## 🧭 Mandatory Session Protocol

1. **At the START of every session/conversation**:
   - Read `.context/PROGRESS.md` to understand current progress, active tasks, and recent gotchas.
2. **Before performing ANY coding or refactoring task**:
   - Read `.context/CORE_RULES.md` to comply with architectural constraints, banned anti-patterns, and coding standards.
3. **Before asking questions or exploring file structure**:
   - Consult `.context/ARCHITECTURE.md` to locate directories, entities, use cases, and route mappings.
4. **At the END of every task / turn**:
   - Update `.context/PROGRESS.md` (check off completed items, add in-progress tasks, log any new gotchas, and refresh the update timestamp).

---

## ⚡ Key Constraints (Summary)

1. **Offline-First Single Source of Truth**: The local Room Database is the absolute source of truth for the presentation layer. Jetpack Compose UI observes Room via reactive `Flow<List<T>>`; network data updates the database atomically.
2. **No Monolithic Leaks (Clean Architecture)**: Keep the Domain layer pure Kotlin (no Android or Room imports). Data layer handles DB/Network. UI layer handles Compose/M3.
3. **Asynchronous Bright Data DCA Integration**: Always use asynchronous batch triggers (`POST /dca/trigger?collector=c_*`) and polling (`GET /dca/dataset?id=j_*`) or proxy webhooks. NEVER block mobile foreground threads with synchronous DOM scraping.
4. **Zero-Downtime Self-Healing**: Scraper drift is repaired on Bright Data Scraper Studio via `bdata scraper heal` and `bdata scraper approve` without altering client-side code or changing the persistent Collector ID (`c_*`).
5. **No Leaked Secrets**: API keys (Bright Data, Gemini) must be accessed via `BuildConfig` and configured using `.env` / `.env.example` placeholders. Never hardcode live keys in git.
6. **Gemini High-Thinking Advisory**: Agricultural AI advisory queries must utilize `gemini-3.1-pro-preview` with `thinkingLevel = "high"` or `gemini-3.7-flash` with graceful offline fallback.
7. **Accessibility & Material 3**: Ensure all interactive touch targets meet the 48dp x 48dp minimum and include `Modifier.testTag("snake_case_tag")`.
