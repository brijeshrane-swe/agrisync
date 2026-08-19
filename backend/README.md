# AgriSync Backend Proxy Server

Lightweight proxy server for the **Into the Scrape-Verse Hackathon** (August 17–23, 2026).

## Purpose
1. Safely protects your Bright Data API token from being exposed in client-side applications.
2. Initiates asynchronous batch triggers via Bright Data Scraper Studio Data Collection API (`POST /dca/trigger`).
3. Handles polling or webhooks for batch dataset extraction (`GET /dca/dataset`).
4. Serves as a free-tier compatible intermediary (deployable on Render, Vercel, Railway, or Cloudflare Workers).

---

## Free Tier Deployment Guide

### Option 1: Render.com (Free Web Service)
1. Push this repository to GitHub.
2. Sign in to [Render.com](https://render.com) and click **New Web Service**.
3. Select this repository and set:
   - **Root Directory**: `backend`
   - **Build Command**: `npm install`
   - **Start Command**: `npm start`
4. Add Environment Variables:
   - `BRIGHTDATA_API_KEY`: Your Bright Data API Key
   - `BRIGHTDATA_COLLECTOR_ID`: `c_apmc_spice_v1_09x`
5. Click **Deploy**. Your free URL will be `https://your-service.onrender.com`.

### Option 2: Local Development
```bash
cd backend
npm install
cp .env.example .env
# Edit .env with your Bright Data credentials
npm start
```
