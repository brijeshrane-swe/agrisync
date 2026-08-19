import { Request, Response, NextFunction } from 'express';
import { IBrightDataService } from '../domain/repositories';
import { z } from 'zod';

const WebhookSchema = z.object({
  id: z.string().optional(),
  collection_id: z.string().optional(),
  data: z.array(z.any()).optional()
});

export class AgriSyncController {
  constructor(
    private brightDataService: IBrightDataService,
    private collectorId: string
  ) {}

  getHealth = (_req: Request, res: Response): void => {
    res.json({
      service: 'AgriSync Bright Data DCA Proxy (Stateless Clean Architecture)',
      status: 'ONLINE',
      architecture: 'Domain -> Data -> Presentation (Stateless API Gateway)',
      security: 'Helmet + RateLimiter + Zod Hardened',
      hackathon: 'Into the Scrape-Verse (Aug 17-23, 2026)',
      collectorId: this.collectorId,
      timestamp: new Date().toISOString()
    });
  };

  triggerSync = async (_req: Request, res: Response, _next: NextFunction): Promise<void> => {
    try {
      const result = await this.brightDataService.triggerDCABatch();
      res.json(result);
    } catch (error: any) {
      console.error('Error triggering DCA batch scrape:', error.message);
      res.status(502).json({
        error: 'Failed to trigger Bright Data scraper job',
        details: error.message || error.response?.data
      });
    }
  };

  getSyncStatus = async (req: Request, res: Response): Promise<void> => {
    const collectionId = req.params.id;
    try {
      const result = await this.brightDataService.getDCADataset(collectionId);
      res.json(result);
    } catch (error: any) {
      res.status(500).json({
        error: 'Failed to retrieve dataset from Bright Data',
        details: error.message || error.response?.data
      });
    }
  };

  handleWebhook = (req: Request, res: Response): void => {
    const parseResult = WebhookSchema.safeParse(req.body);
    if (!parseResult.success) {
      res.status(400).json({ error: 'Invalid webhook payload structure', details: parseResult.error.format() });
      return;
    }

    const payload = parseResult.data;
    const targetId = payload.id || payload.collection_id;
    console.log('Received DCA Webhook completion notification for collection:', targetId);

    // Stateless webhook acknowledgement
    res.status(200).json({ status: 'ACCEPTED', collection_id: targetId });
  };
}
