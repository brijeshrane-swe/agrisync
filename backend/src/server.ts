import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import rateLimit from 'express-rate-limit';
import dotenv from 'dotenv';

import { InMemoryCacheRepository, BrightDataService } from './data/repositories';
import { AgriSyncController } from './controllers/AgriSyncController';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

// Security Middleware (OWASP Compliant)
app.use(helmet());
app.use(cors());
app.use(express.json({ limit: '1mb' }));

// Rate Limiter (Max 100 requests per 15 mins per IP)
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

// Dependency Injection Setup (SOLID - Dependency Inversion Principle)
const BRIGHTDATA_API_KEY = process.env.BRIGHTDATA_API_KEY || '';
const BRIGHTDATA_COLLECTOR_ID = process.env.BRIGHTDATA_COLLECTOR_ID || 'c_apmc_spice_v1_09x';
const TARGET_APMC_URL = process.env.TARGET_APMC_URL || 'https://www.indianspices.com/marketing/price/domestic/current-market-price.html';

const cacheRepository = new InMemoryCacheRepository();
const brightDataService = new BrightDataService(
  BRIGHTDATA_API_KEY,
  BRIGHTDATA_COLLECTOR_ID,
  TARGET_APMC_URL,
  cacheRepository
);

const controller = new AgriSyncController(brightDataService, cacheRepository, BRIGHTDATA_COLLECTOR_ID);

// Clean Architecture Route Mapping
app.get('/', controller.getHealth);
app.post('/api/sync/trigger', controller.triggerSync);
app.get('/api/sync/status/:id', controller.getSyncStatus);
app.post('/api/webhook/dca', controller.handleWebhook);

app.listen(PORT, () => {
  console.log(`[TypeScript Clean Architecture] AgriSync Backend running on port ${PORT}`);
});
