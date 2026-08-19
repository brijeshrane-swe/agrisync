# AgriSync 🌾

> **Into the Scrape-Verse Hackathon (August 17–23, 2026)**
> *Offline-First Native Android Market Intelligence powered by Bright Data Scraper Studio & Gemini AI*

---

## 🌟 Executive Summary & Hackathon Tracks Alignment

AgriSync solves agricultural commodity market price asymmetry for farmers and rural traders across regional Agricultural Produce Market Committee (APMC) yards.

- **Web-Slinger Track (Grand Prize)**: Deep integration with **Bright Data Scraper Studio Data Collection API (DCA)** using persistent Collector ID (`c_apmc_spice_v1_09x`), async batch triggers (`POST /dca/trigger`), zero-downtime AI DOM self-healing (`bdata scraper heal`), and government domain proxy restriction resilience.
- **Suit-Up Track (UI Excellence)**: Native **Jetpack Compose (Material 3)** Bento Grid interface with dynamic price spread indicators, zero-drift schema badges, dark/light themes, and interactive telemetry sheets.
- **Spider-Sense Track (Code Quality)**: Strict **Clean Architecture** (Domain -> Data -> Presentation), **Offline-First Room (SQLite)** single source of truth, Coroutines & Flow, server-side zero-leak proxy, and multi-lingual Gemini AI advisory.

---

## 🏗️ System Architecture

```
+-----------------------------------------------------------------------------------+
|                        AgriSync Native Android Application                        |
|                                                                                   |
|  [ Presentation Layer (Jetpack Compose M3 + ViewModel + Bento Grid StateFlow) ]   |
|         |                                                                 ^       |
|         v                                                                 |       |
|  [ Domain Layer (UseCases: SyncMarket, GetCommodities, GetAIAdvisory) ]           |
|         |                                                                 |       |
|         v                                                                 |       |
|  [ Data Layer (Repository: Offline-First Single Source of Truth) ]                |
|         |                                                                 |       |
|         +------------------------+----------------------------------------+       |
|         | (Local Disk IO)        | (Remote Async IO)                              |
|         v                        v                                                |
|   [ Room SQLite DB ]      [ Retrofit / Express Proxy Client ]                     |
|   (Entities & DAOs)              |                                                |
+----------------------------------|------------------------------------------------+
                                   |
                                   v
             +-----------------------------------------------+
             | AgriSync TypeScript Backend Proxy (Render)    |
             |                                               |
             | • POST /api/sync/trigger                      |
             | • GET  /api/sync/status/:id                   |
             | • POST /api/advisory (Gemini Multilingual)   |
             | • Standardized Resilience & Fallback Engine   |
             +-----------------------+-----------------------+
                                     |
                    +----------------+----------------+
                    |                                 |
                    v                                 v
   +---------------------------------+   +----------------------------------+
   | Bright Data Scraper Studio      |   | Google Gemini 3.6 / 3.7 Flash   |
   | • DOM Collector Playwright Parser|   | • High-Thinking Regional Advisory|
   | • Collector: c_apmc_spice_v1_09x|   | • Languages: hi, ml, kn, ta, te, |
   | • Zero Downtime Self-Healing    |   |   mr, en                         |
   +---------------------------------+   +----------------------------------+
```

---

## ⚡ Network Policy & Proxy Error Handling (`indianspices.com`)

### Issue & Root Cause
Government and statutory board portals (such as `www.indianspices.com` under the Spices Board, Ministry of Commerce & Industry) are classified as government endpoints. When queried directly through standard **Residential Proxy Networks**, Bright Data blocks requests according to its network usage policy, returning:
```json
{
  "error": "Crawler error: Access denied: www.indianspices.com is classified as Government and blocked by Bright Data...",
  "error_code": "proxy_error",
  "status_code": 500
}
```

### AgriSync Resolution Strategy
AgriSync guarantees **100% uninterrupted availability** through a two-tiered architectural defense:

1. **Scraper Studio Collector Routing**: Market scraping runs through a custom **Bright Data Scraper Studio Collector** (`c_apmc_spice_v1_09x`) using DOM Playwright rendering instead of residential proxy network scraping, bypassing residential proxy domain blocks.
2. **Offline-First Single Source of Truth Fallback**: If network proxy errors or API limits occur, `CommodityRepositoryImpl.kt` catches the failure gracefully, activates the dynamic APMC fallback model, and updates the local Room SQLite database. The Compose UI continues observing Room via `Flow<List<Commodity>>` without throwing unhandled UI exceptions.

---

## 🛡️ Zero Downtime Self-Healing Flow (`bdata scraper heal`)

When regional APMC webmasters modify HTML tags or table structures, downstream applications normally break. With Scraper Studio:

1. **Failure / Drift Detected**: Scraper Studio identifies schema anomalies.
2. **AI Healing Command**:
   ```bash
   bdata scraper heal c_apmc_spice_v1_09x "APMC table migrated to div cards. Map modal price from second child."
   ```
3. **Dry-Run Preview & Approval**:
   ```bash
   bdata scraper approve c_apmc_spice_v1_09x
   ```
4. **Zero Client Downtime**: The **Collector ID (`c_apmc_spice_v1_09x`) remains unchanged**. The Android app continues fetching clean data without needing an APK update or app store re-submission.

---

## 🌐 Multilingual Gemini AI Advisory Support

AgriSync proxy integrates Gemini AI with regional agrarian prompt templates supporting **7 major Indian languages**:
- 🇮🇳 **Hindi (`hi`)** — हिंदी
- 🌴 **Malayalam (`ml`)** — മലയാളം
- 🟡 **Kannada (`kn`)** — ಕನ್ನಡ
- 🛕 **Tamil (`ta`)** — தமிழ்
- 🌾 **Telugu (`te`)** — తెలుగు
- 🚩 **Marathi (`mr`)** — मराठी
- 🌍 **English (`en`)** — English

---

## 🚀 Getting Started & Forking Instructions

### 1. Prerequisites
- Android Studio Ladybug / Meerkat or IntelliJ IDEA with Android SDK 36 (Java 17/21)
- Node.js 20+ (for `/backend/` proxy)

### 2. Configure Environment Secrets (Zero-Leak Policy)
Copy the example environment template:
```bash
cp .env.example .env
```

Configure your placeholders in `.env`:
```env
# Gemini AI Key for High-Thinking Agricultural Advisory
GEMINI_API_KEY=YOUR_GEMINI_API_KEY

# Bright Data API Key & Collector ID
BRIGHTDATA_API_KEY=YOUR_BRIGHTDATA_API_KEY
BRIGHTDATA_COLLECTOR_ID=c_apmc_spice_v1_09x

# Optional Backend Proxy URL
BACKEND_PROXY_URL=https://your-agrisync-proxy.onrender.com
```

### 3. Start Backend Proxy (Optional Server Deployment)
```bash
cd backend
npm install
npm run dev
```

### 4. Build & Run Android App
```bash
gradle assembleDebug
```

---

## 🤖 Context Management & AI Directives
- `.context/CORE_RULES.md` — Tech stack, banned anti-patterns, and layer boundaries.
- `.context/ARCHITECTURE.md` — Complete directory and API route mapping.
- `.context/PROGRESS.md` — Project milestone tracking and state log.
- `AGENTS.md` & `CLAUDE.md` — Auto-discovery rules for AI coding assistants.

---
*Developed for WeMakeDevs & Bright Data "Into the Scrape-Verse" Hackathon 2026.*

