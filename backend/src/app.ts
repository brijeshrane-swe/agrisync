import express, { Application } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import rateLimit from 'express-rate-limit';
import compression from 'compression';

import { BrightDataService } from './data/repositories';
import { AgriSyncController } from './controllers/AgriSyncController';

/**
 * Creates and configures the Express application instance.
 * Separated from server startup (`index.ts`) for clean unit testing and testability.
 */
export function createApp(): Application {
  const app = express();

  // Trust reverse proxy (Render / Nginx / Vercel) for accurate client IP rate limiting
  app.set('trust proxy', 1);

  // ─── Security Middleware ───────────────────────────────────────────────
  app.use(
    helmet({
      crossOriginResourcePolicy: { policy: 'cross-origin' }, // Allows mobile Retrofit & web clients
      contentSecurityPolicy: false // API server returning JSON payloads
    })
  );

  app.use(
    cors({
      origin: (origin, callback) => {
        // Allow requests with no origin (native mobile apps, Retrofit, curl, Postman)
        if (!origin) return callback(null, true);
        
        // Allow localhost origins in development
        if (process.env.NODE_ENV !== 'production' && /^http:\/\/localhost(:\d+)?$/.test(origin)) {
          return callback(null, true);
        }

        // Production domains or wildcard for open API access
        return callback(null, true);
      },
      methods: ['GET', 'POST', 'OPTIONS'],
      allowedHeaders: ['Content-Type', 'Authorization']
    })
  );

  // ─── Rate Limiting & Compression ────────────────────────────────────────
  const apiLimiter = rateLimit({
    windowMs: 15 * 60 * 1000,
    max: 100,
    standardHeaders: true,
    legacyHeaders: false,
    message: {
      error: 'Too many requests from this IP, please try again after 15 minutes.'
    }
  });

  app.use('/api/', apiLimiter);
  app.use(express.json({ limit: '1mb' }));
  app.use(compression());

  // ─── Dependency Injection Setup (Stateless Gateway) ──────────────────────
  const BRIGHTDATA_API_KEY = process.env.BRIGHTDATA_API_KEY || '';
  const BRIGHTDATA_COLLECTOR_ID = process.env.BRIGHTDATA_COLLECTOR_ID || 'c_apmc_spice_v1_09x';
  const TARGET_APMC_URL = process.env.TARGET_APMC_URL || 'https://www.indianspices.com/marketing/price/domestic/current-market-price.html';

  const brightDataService = new BrightDataService(
    BRIGHTDATA_API_KEY,
    BRIGHTDATA_COLLECTOR_ID,
    TARGET_APMC_URL
  );

  const controller = new AgriSyncController(brightDataService, BRIGHTDATA_COLLECTOR_ID);

  // ─── Route Mapping ─────────────────────────────────────────────────────
  app.get('/', controller.getHealth);
  app.post('/api/sync/trigger', controller.triggerSync);
  app.get('/api/sync/status/:id', controller.getSyncStatus);
  app.post('/api/webhook/dca', controller.handleWebhook);

  return app;
}
