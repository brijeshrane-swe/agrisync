# AgriSync 🌾

> **Into the Scrape-Verse Hackathon (August 17–23, 2026)**
> *Offline-First Native Android Market Intelligence powered by Bright Data Scraper Studio & Gemini AI*

---

## 🌟 Executive Summary & Hackathon Tracks Alignment

AgriSync solves agricultural commodity market price asymmetry for farmers and rural traders across regional Agricultural Produce Market Committee (APMC) yards.

- **Web-Slinger Track (Grand Prize)**: Deep integration with **Bright Data Scraper Studio Data Collection API (DCA)** using persistent Collector ID (`c_apmc_spice_v1_09x`), async batch triggers (`POST /dca/trigger`), and zero-downtime AI DOM self-healing (`bdata scraper heal`).
- **Suit-Up Track (UI Excellence)**: Native **Jetpack Compose (Material 3)** interface with fluid filtering, price spread indicators, dark/light themes, and interactive telemetry sheets.
- **Spider-Sense Track (Code Quality)**: Strict **Clean Architecture** (Domain -> Data -> Presentation), **Offline-First Room (SQLite)** single source of truth, Coroutines & Flow, and zero hardcoded secrets.

---

## 🏗️ System Architecture

```
+-------------------------------------------------------------------------------+
|                      AgriSync Native Android Application                       |
|                                                                               |
|  [ Presentation Layer (Jetpack Compose M3 + ViewModel + StateFlow) ]          |
|         |                                                             ^       |
|         v                                                             |       |
|  [ Domain Layer (UseCases: SyncMarket, GetCommodities, GetAdvisory) ]         |
|         |                                                             |       |
|         v                                                             |       |
|  [ Data Layer (Repository: Offline-First Single Source of Truth) ]            |
|         |                                                             |       |
|         +------------------------+------------------------------------+       |
|         | (Local Disk IO)        | (Remote Async IO)                          |
|         v                        v                                            |
|   [ Room SQLite DB ]      [ Retrofit / Moshi Client ]                         |
|   (Entities & DAOs)              |                                            |
+----------------------------------|--------------------------------------------+
                                   |
                                   v
             +-------------------------------------------+
             | Bright Data Scraper Studio & DCA Engine   |
             |                                           |
             | • POST /dca/trigger?collector=c_xxxx      |
             | • GET  /dca/dataset?id=j_xxxx             |
             | • CLI: bdata scraper heal c_xxxx          |
             | • Gemini 3.1 Pro Agricultural Advisor     |
             +-------------------------------------------+
                                   |
                                   v
             +-------------------------------------------+
             | Regional APMC & Spices Board Portals      |
             | (Legacy HTML Tables, Long-Tail Data)      |
             +-------------------------------------------+
```

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

## 🚀 Getting Started & Forking Instructions

### 1. Prerequisites
- Android Studio Ladybug / Meerkat or IntelliJ IDEA with Android SDK 36 (Java 17/21)
- Node.js 20+ (optional, for `/backend/` proxy)

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

### 3. Build & Run Android App
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
